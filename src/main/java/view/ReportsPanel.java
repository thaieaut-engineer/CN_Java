package view;

import config.DatabaseConnection;
import util.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {
    private final JTextField fromDate = new JTextField(10);
    private final JTextField toDate = new JTextField(10);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Chi nhánh", "Hóa đơn đã trả", "Doanh thu"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final RevenueChart chart = new RevenueChart();
    private final JLabel status = new JLabel("Đang chuẩn bị báo cáo...");
    private final AtomicLong reportVersion = new AtomicLong();

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiTheme.stylePage(this);
        JLabel title = new JLabel("BÁO CÁO DOANH THU THEO CHI NHÁNH");
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        title.setIcon(new ClinicIcon("report", UiTheme.BLUE, 22));
        title.setIconTextGap(10);
        LocalDate today = LocalDate.now();
        fromDate.setText(today.withDayOfMonth(1).toString());
        toDate.setText(today.toString());
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        filters.add(fromDate);
        filters.add(new JLabel("Đến ngày:"));
        filters.add(toDate);
        JButton refresh = new JButton("Lập báo cáo");
        JButton export = new JButton("Xuất Excel");
        UiTheme.stylePrimary(refresh);
        UiTheme.styleSecondary(export);
        export.setIcon(new ClinicIcon("report", UiTheme.BLUE, 15));
        UiTheme.styleTextField(fromDate);
        UiTheme.styleTextField(toDate);
        filters.setOpaque(false);
        filters.add(refresh);
        filters.add(export);
        table.setAutoCreateRowSorter(true);
        UiTheme.styleTable(table);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(filters, BorderLayout.SOUTH);
        JPanel results = new JPanel(new BorderLayout(8, 8));
        status.setForeground(UiTheme.MUTED);
        status.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 0));
        JPanel tableContent = new JPanel(new BorderLayout(0, 6));
        tableContent.setOpaque(false);
        tableContent.add(status, BorderLayout.NORTH);
        tableContent.add(new JScrollPane(table), BorderLayout.CENTER);
        results.add(tableContent, BorderLayout.CENTER);
        chart.setPreferredSize(new Dimension(700, 230));
        chart.setBackground(Color.WHITE);
        results.add(chart, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(results, BorderLayout.CENTER);
        refresh.addActionListener(event -> loadReport());
        export.addActionListener(event -> export());
        loadReport();
    }

    private void loadReport() {
        final LocalDate from;
        final LocalDate to;
        try {
            from = LocalDate.parse(fromDate.getText().trim());
            to = LocalDate.parse(toDate.getText().trim());
            if (to.isBefore(from)) throw new IllegalArgumentException("Ngày kết thúc phải bằng hoặc sau ngày bắt đầu.");
        } catch (DateTimeParseException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage() == null ? "Ngày phải theo định dạng yyyy-MM-dd." : e.getMessage(),
                    "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long version = reportVersion.incrementAndGet();
        status.setText("Đang lập báo cáo...");
        new javax.swing.SwingWorker<ReportData, Void>() {
            @Override
            protected ReportData doInBackground() throws SQLException {
                String sql = "SELECT b.name AS branch_name, COUNT(i.invoice_id) AS invoice_count, "
                        + "COALESCE(SUM(i.total_amount), 0) AS revenue FROM Branch b "
                        + "LEFT JOIN MedicalRecord mr ON mr.branch_id=b.branch_id "
                        + "LEFT JOIN Invoice i ON i.record_id=mr.record_id AND i.status=N'Paid' "
                        + "AND i.created_date >= ? AND i.created_date < ? "
                        + "GROUP BY b.branch_id,b.name ORDER BY b.name";
                ReportData data = new ReportData();
                BigDecimal total = BigDecimal.ZERO;
                long invoices = 0;
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setDate(1, Date.valueOf(from));
                    statement.setDate(2, Date.valueOf(to.plusDays(1)));
                    try (ResultSet results = statement.executeQuery()) {
                        while (results.next()) {
                            String branch = results.getString("branch_name");
                            long count = results.getLong("invoice_count");
                            BigDecimal revenue = results.getBigDecimal("revenue");
                            data.labels.add(branch);
                            data.amounts.add(revenue);
                            data.rows.add(new Object[]{branch, count, revenue});
                            total = total.add(revenue);
                            invoices += count;
                        }
                    }
                }
                data.rows.add(new Object[]{"TỔNG CỘNG", invoices, total});
                return data;
            }

            @Override
            protected void done() {
                if (version != reportVersion.get()) {
                    return;
                }
                try {
                    ReportData data = get();
                    model.setRowCount(0);
                    data.rows.forEach(model::addRow);
                    chart.setData(data.labels, data.amounts);
                    status.setText(data.labels.size() + " chi nhánh");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    status.setText("Đã hủy lập báo cáo.");
                    showError(e);
                } catch (ExecutionException e) {
                    status.setText("Không lập được báo cáo.");
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError(cause instanceof Exception exception ? exception : new Exception(cause));
                }
            }
        }.execute();
    }

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("bao-cao-doanh-thu.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        if (!path.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".xlsx")) path = Path.of(path + ".xlsx");
        try {
            ExcelExporter.write(table, path);
            JOptionPane.showMessageDialog(this, "Đã xuất Excel: " + path);
        } catch (IOException e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Không thể lập báo cáo:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static final class ReportData {
        private final List<String> labels = new ArrayList<>();
        private final List<BigDecimal> amounts = new ArrayList<>();
        private final List<Object[]> rows = new ArrayList<>();
    }

    private static final class RevenueChart extends JPanel {
        private List<String> labels = List.of();
        private List<BigDecimal> amounts = List.of();

        void setData(List<String> labels, List<BigDecimal> amounts) {
            this.labels = List.copyOf(labels);
            this.amounts = List.copyOf(amounts);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            g.setColor(new java.awt.Color(244, 246, 248));
            g.fillRect(0, 0, width, height);
            if (amounts.isEmpty()) {
                g.setColor(java.awt.Color.DARK_GRAY);
                g.drawString("Chưa có dữ liệu doanh thu trong khoảng thời gian này.", 20, height / 2);
                g.dispose();
                return;
            }
            BigDecimal maximum = amounts.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
            int barWidth = Math.max(30, (width - 80) / amounts.size() - 16);
            int gap = Math.max(12, (width - 80 - barWidth * amounts.size()) / amounts.size());
            for (int index = 0; index < amounts.size(); index++) {
                int x = 40 + index * (barWidth + gap);
                int barHeight = maximum.signum() == 0 ? 0
                        : amounts.get(index).multiply(BigDecimal.valueOf(height - 80))
                                .divide(maximum, 0, java.math.RoundingMode.HALF_UP).intValue();
                int y = height - 42 - barHeight;
                g.setColor(new java.awt.Color(43, 134, 209));
                g.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
                g.setColor(java.awt.Color.DARK_GRAY);
                g.drawString(labels.get(index), x, height - 20);
                g.drawString(amounts.get(index).toPlainString(), x, Math.max(16, y - 5));
            }
            g.dispose();
        }
    }
}
