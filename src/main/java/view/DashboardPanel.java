package view;

import bus.DashboardBUS;
import model.DashboardData;
import model.Employee;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {
    private final Employee employee;
    private final DashboardBUS dashboardBUS = new DashboardBUS();
    private final JLabel branchesValue = new JLabel("—");
    private final JLabel employeesValue = new JLabel("—");
    private final JLabel customersValue = new JLabel("—");
    private final JLabel petsValue = new JLabel("—");
    private final JLabel appointmentsValue = new JLabel("—");
    private final JLabel revenueValue = new JLabel("—");
    private final JLabel unpaidValue = new JLabel("—");
    private final JLabel lowStockValue = new JLabel("—");
    private final JLabel status = new JLabel(" ");
    private final RevenueChart revenueChart = new RevenueChart();
    private final DefaultTableModel appointmentModel = new DefaultTableModel(
            new String[]{"Thời gian", "Thú cưng", "Khách hàng", "Chi nhánh", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public DashboardPanel(Employee employee) {
        this.employee = employee;
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        UiTheme.stylePage(this);

        HeroPanel heading = new HeroPanel();
        heading.setLayout(new BorderLayout(12, 8));
        heading.setBorder(BorderFactory.createEmptyBorder(19, 24, 19, 22));
        JLabel title = new JLabel("Tổng quan hệ thống");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        title.setForeground(Color.WHITE);
        JLabel greeting = new JLabel("Xin chào, " + employee.getFullName() + "  •  Chúc bạn một ngày làm việc hiệu quả");
        greeting.setForeground(new Color(219, 234, 254));
        greeting.setFont(greeting.getFont().deriveFont(13f));
        JPanel titleBlock = new JPanel(new GridLayout(0, 1, 0, 4));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(greeting);
        JButton refresh = new JButton("Làm mới");
        refresh.setIcon(new ClinicIcon("history", UiTheme.BLUE, 16));
        refresh.setBackground(Color.WHITE);
        refresh.setForeground(UiTheme.BLUE_DARK);
        refresh.setFocusPainted(false);
        refresh.putClientProperty("JButton.buttonType", "roundRect");
        refresh.addActionListener(event -> loadDashboard());
        heading.add(titleBlock, BorderLayout.WEST);
        heading.add(refresh, BorderLayout.EAST);
        add(heading, BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(2, 4, 12, 12));
        metrics.setOpaque(false);
        metrics.add(metricCard("Chi nhánh", branchesValue, "branch", "Đang hoạt động", new Color(37, 99, 235)));
        metrics.add(metricCard("Nhân viên", employeesValue, "employee", "Tài khoản toàn chuỗi", new Color(79, 70, 229)));
        metrics.add(metricCard("Khách hàng", customersValue, "customer", "Hồ sơ khách hàng", new Color(8, 145, 178)));
        metrics.add(metricCard("Thú cưng", petsValue, "pet", "Được đăng ký khám", new Color(13, 148, 136)));
        metrics.add(metricCard("Lịch hẹn hôm nay", appointmentsValue, "appointment", "Chưa hủy", new Color(37, 99, 235)));
        metrics.add(metricCard("Doanh thu tháng này", revenueValue, "report", "Hóa đơn đã thanh toán", new Color(29, 78, 216)));
        metrics.add(metricCard("Hóa đơn chưa trả", unpaidValue, "invoice", "Tổng số tiền còn nợ", new Color(79, 70, 229)));
        metrics.add(metricCard("Sắp hết hàng", lowStockValue, "inventory", "Thuốc / vắc-xin còn ≤ 5", new Color(8, 145, 178)));

        JTable appointments = new JTable(appointmentModel);
        UiTheme.styleTable(appointments);
        appointments.setAutoCreateRowSorter(true);
        JPanel appointmentsPanel = new JPanel(new BorderLayout(8, 8));
        UiTheme.styleSurface(appointmentsPanel);
        JLabel appointmentsTitle = new JLabel("Lịch hẹn sắp tới");
        appointmentsTitle.setFont(appointmentsTitle.getFont().deriveFont(Font.BOLD, 16f));
        appointmentsTitle.setIcon(new ClinicIcon("appointment", UiTheme.BLUE, 19));
        appointmentsTitle.setIconTextGap(9);
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);
        appointmentsPanel.add(new JScrollPane(appointments), BorderLayout.CENTER);

        JPanel chartPanel = new JPanel(new BorderLayout(8, 8));
        UiTheme.styleSurface(chartPanel);
        JLabel chartTitle = new JLabel("Doanh thu 6 tháng gần nhất");
        chartTitle.setFont(chartTitle.getFont().deriveFont(Font.BOLD, 16f));
        chartTitle.setIcon(new ClinicIcon("report", UiTheme.BLUE, 19));
        chartTitle.setIconTextGap(9);
        chartPanel.add(chartTitle, BorderLayout.NORTH);
        revenueChart.setPreferredSize(new Dimension(480, 270));
        chartPanel.add(revenueChart, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setOpaque(false);
        content.add(metrics, BorderLayout.NORTH);
        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 0));
        lower.setOpaque(false);
        lower.add(appointmentsPanel);
        lower.add(chartPanel);
        content.add(lower, BorderLayout.CENTER);
        status.setForeground(new Color(100, 116, 139));
        content.add(status, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
        loadDashboard();
    }

    private JPanel metricCard(String label, JLabel value, String icon, String hint, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 10, 14)));
        JLabel caption = new JLabel(label);
        caption.setForeground(UiTheme.MUTED);
        caption.setFont(caption.getFont().deriveFont(Font.PLAIN, 12f));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 22f));
        value.setForeground(UiTheme.TEXT);
        JLabel symbol = new JLabel(new ClinicIcon(icon, accent, 21));
        JPanel iconBadge = new JPanel(new BorderLayout());
        iconBadge.setBackground(new Color(239, 246, 255));
        iconBadge.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        iconBadge.add(symbol);
        JPanel upper = new JPanel(new BorderLayout(8, 0));
        upper.setOpaque(false);
        upper.add(caption, BorderLayout.CENTER);
        upper.add(iconBadge, BorderLayout.EAST);
        JLabel footnote = new JLabel(hint);
        footnote.setForeground(new Color(148, 163, 184));
        footnote.setFont(footnote.getFont().deriveFont(10f));
        JPanel valueBlock = new JPanel(new GridLayout(0, 1, 0, 3));
        valueBlock.setOpaque(false);
        valueBlock.add(value);
        valueBlock.add(footnote);
        card.add(upper, BorderLayout.NORTH);
        card.add(valueBlock, BorderLayout.CENTER);
        return card;
    }

    private void loadDashboard() {
        status.setText("Đang cập nhật dữ liệu...");
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                return dashboardBUS.getDashboard();
            }

            @Override
            protected void done() {
                setCursor(java.awt.Cursor.getDefaultCursor());
                try {
                    DashboardData data = get();
                    branchesValue.setText(formatCount(data.getBranches()));
                    employeesValue.setText(formatCount(data.getEmployees()));
                    customersValue.setText(formatCount(data.getCustomers()));
                    petsValue.setText(formatCount(data.getPets()));
                    appointmentsValue.setText(formatCount(data.getTodayAppointments()));
                    revenueValue.setText(formatMoney(data.getMonthRevenue()));
                    unpaidValue.setText(formatMoney(data.getUnpaidAmount()));
                    lowStockValue.setText(formatCount(data.getLowStock()));
                    appointmentModel.setRowCount(0);
                    for (model.AppointmentSummary appointment : data.getAppointments()) {
                        appointmentModel.addRow(new Object[]{appointment.getAppointmentDate(), appointment.getPetName(),
                            appointment.getCustomerName(), appointment.getBranchName(), appointment.getStatus()});
                    }
                    revenueChart.setData(data.getMonthLabels(), data.getMonthRevenues());
                    status.setText("Cập nhật lúc " + java.time.LocalTime.now().withNano(0));
                } catch (Exception e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    status.setText("Không thể tải dashboard.");
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                            "Không thể tải dữ liệu tổng quan:\n" + cause.getMessage(),
                            "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private String formatCount(long value) {
        return String.format(java.util.Locale.forLanguageTag("vi-VN"), "%,d", value);
    }

    private String formatMoney(BigDecimal value) {
        return String.format(java.util.Locale.forLanguageTag("vi-VN"), "%,.0f đ",
                value == null ? BigDecimal.ZERO : value);
    }

    private static final class HeroPanel extends JPanel {
        private HeroPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(29, 78, 216),
                    getWidth(), getHeight(), new Color(14, 165, 233)));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g.setColor(new Color(255, 255, 255, 25));
            g.fillOval(getWidth() - 150, -80, 210, 210);
            g.fillOval(getWidth() - 95, 35, 100, 100);
            g.dispose();
            super.paintComponent(graphics);
        }
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
            g.setColor(new Color(248, 250, 252));
            g.fillRect(0, 0, width, height);
            if (amounts.isEmpty()) {
                g.setColor(new Color(100, 116, 139));
                g.drawString("Chưa có dữ liệu doanh thu.", 18, height / 2);
                g.dispose();
                return;
            }
            BigDecimal maximum = amounts.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            int plotHeight = Math.max(1, height - 78);
            int chartTop = 28;
            int baseline = height - 38;
            int slotWidth = Math.max(1, (width - 42) / amounts.size());
            int barWidth = Math.max(12, Math.min(38, slotWidth - 14));
            g.setColor(new Color(226, 232, 240));
            for (int grid = 0; grid < 4; grid++) {
                int y = chartTop + grid * Math.max(1, plotHeight / 3);
                g.drawLine(18, y, width - 10, y);
            }
            for (int index = 0; index < amounts.size(); index++) {
                int x = 21 + index * slotWidth + (slotWidth - barWidth) / 2;
                int barHeight = maximum.signum() == 0 ? 0
                        : amounts.get(index).multiply(BigDecimal.valueOf(plotHeight))
                                .divide(maximum, 0, java.math.RoundingMode.HALF_UP).intValue();
                int y = baseline - barHeight;
                g.setPaint(new GradientPaint(x, y, new Color(56, 189, 248),
                        x + barWidth, baseline, new Color(37, 99, 235)));
                g.fillRoundRect(x, y, barWidth, barHeight, 9, 9);
                g.setColor(new Color(71, 85, 105));
                String label = labels.get(index);
                g.drawString(label.substring(5), x - 3, height - 13);
                if (barHeight > 22) {
                    g.setColor(Color.WHITE);
                    g.drawString(String.format(java.util.Locale.ROOT, "%.1ftr",
                            amounts.get(index).doubleValue() / 1_000_000d), x - 2, y + 15);
                }
            }
            g.dispose();
        }
    }
}
