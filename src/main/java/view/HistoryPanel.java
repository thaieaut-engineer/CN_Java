package view;

import bus.HistoryBUS;
import model.HistoryEntry;
import util.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
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

public class HistoryPanel extends JPanel {
    private final HistoryBUS historyBUS = new HistoryBUS();
    private final JTextField query = new JTextField(24);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{
        "Ngày khám", "Thú cưng", "Loài", "Khách hàng", "Điện thoại", "Chi nhánh", "Bác sĩ", "Chẩn đoán", "Ghi chú", "Tái khám", "Hóa đơn"
    }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Đang tải lịch sử khám...");
    private final AtomicLong searchVersion = new AtomicLong();

    public HistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiTheme.stylePage(this);
        JLabel title = new JLabel("TRA CỨU LỊCH SỬ KHÁM TOÀN CHUỖI");
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        title.setIcon(new ClinicIcon("history", UiTheme.BLUE, 22));
        title.setIconTextGap(10);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        controls.add(new JLabel("Tìm tên khách, số điện thoại hoặc thú cưng (để trống để xem tất cả):"));
        controls.add(query);
        UiTheme.styleTextField(query);
        query.putClientProperty("JTextField.placeholderText", "Để trống để xem toàn bộ lịch sử");
        JButton search = new JButton("Tra cứu");
        JButton export = new JButton("Xuất Excel");
        UiTheme.stylePrimary(search);
        UiTheme.styleSecondary(export);
        export.setIcon(new ClinicIcon("report", UiTheme.BLUE, 15));
        controls.add(search);
        controls.add(export);
        table.setAutoCreateRowSorter(true);
        UiTheme.styleTable(table);
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(controls, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        status.setForeground(UiTheme.MUTED);
        status.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 0));
        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setOpaque(false);
        content.add(status, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        search.addActionListener(event -> search());
        query.addActionListener(event -> search());
        export.addActionListener(event -> export());
        search();
    }

    private void search() {
        String term = query.getText().trim();
        long version = searchVersion.incrementAndGet();
        status.setText(term.isEmpty() ? "Đang tải toàn bộ lịch sử..." : "Đang tra cứu...");
        new javax.swing.SwingWorker<List<HistoryEntry>, Void>() {
            @Override
            protected List<HistoryEntry> doInBackground() throws SQLException {
                return historyBUS.search(term);
            }

            @Override
            protected void done() {
                if (version != searchVersion.get()) {
                    return;
                }
                try {
                    List<HistoryEntry> rows = get();
                    model.setRowCount(0);
                    rows.forEach(entry -> model.addRow(new Object[]{entry.getVisitDate(), entry.getPetName(),
                        entry.getSpecies(), entry.getCustomerName(), entry.getPhone(), entry.getBranchName(),
                        entry.getDoctor(), entry.getDiagnosis(), entry.getNotes(), entry.getRevisitDate(),
                        entry.getTotalAmount()}));
                    status.setText(rows.isEmpty()
                            ? "Không có phiếu khám phù hợp. Nếu đây là dữ liệu mới, hãy tạo phiếu khám hoặc chạy sample_data.sql."
                            : rows.size() + " kết quả");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    status.setText("Đã hủy tra cứu.");
                    showError(e);
                } catch (ExecutionException e) {
                    status.setText("Tra cứu thất bại.");
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError(cause instanceof Exception exception ? exception : new Exception(cause));
                }
            }
        }.execute();
    }

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("lich-su-kham.xlsx"));
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
        JOptionPane.showMessageDialog(this, "Tra cứu thất bại:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
