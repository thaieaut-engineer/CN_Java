package view;

import config.DatabaseConnection;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        JPanel heading = new JPanel(new BorderLayout(12, 8));
        heading.setOpaque(false);
        JLabel title = new JLabel("Tổng quan hệ thống");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        JLabel greeting = new JLabel("Xin chào, " + employee.getFullName() + "  |  " + employee.getRole());
        greeting.setForeground(new Color(100, 116, 139));
        JPanel titleBlock = new JPanel(new GridLayout(0, 1, 0, 4));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(greeting);
        JButton refresh = new JButton("Làm mới dữ liệu");
        refresh.addActionListener(event -> loadDashboard());
        heading.add(titleBlock, BorderLayout.WEST);
        heading.add(refresh, BorderLayout.EAST);
        add(heading, BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(2, 4, 12, 12));
        metrics.setOpaque(false);
        metrics.add(metricCard("Chi nhánh", branchesValue, new Color(59, 130, 246)));
        metrics.add(metricCard("Nhân viên", employeesValue, new Color(139, 92, 246)));
        metrics.add(metricCard("Khách hàng", customersValue, new Color(16, 185, 129)));
        metrics.add(metricCard("Thú cưng", petsValue, new Color(245, 158, 11)));
        metrics.add(metricCard("Lịch hẹn hôm nay", appointmentsValue, new Color(6, 182, 212)));
        metrics.add(metricCard("Doanh thu tháng này", revenueValue, new Color(34, 197, 94)));
        metrics.add(metricCard("Hóa đơn chưa trả", unpaidValue, new Color(239, 68, 68)));
        metrics.add(metricCard("Thuốc/vắc-xin sắp hết", lowStockValue, new Color(249, 115, 22)));

        JTable appointments = new JTable(appointmentModel);
        appointments.setRowHeight(28);
        appointments.setAutoCreateRowSorter(true);
        JPanel appointmentsPanel = new JPanel(new BorderLayout(8, 8));
        appointmentsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel appointmentsTitle = new JLabel("Lịch hẹn sắp tới");
        appointmentsTitle.setFont(appointmentsTitle.getFont().deriveFont(Font.BOLD, 16f));
        appointmentsPanel.add(appointmentsTitle, BorderLayout.NORTH);
        appointmentsPanel.add(new JScrollPane(appointments), BorderLayout.CENTER);

        JPanel chartPanel = new JPanel(new BorderLayout(8, 8));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel chartTitle = new JLabel("Doanh thu 6 tháng gần nhất");
        chartTitle.setFont(chartTitle.getFont().deriveFont(Font.BOLD, 16f));
        chartPanel.add(chartTitle, BorderLayout.NORTH);
        revenueChart.setPreferredSize(new Dimension(480, 300));
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

    private JPanel metricCard(String label, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14))));
        JLabel caption = new JLabel(label);
        caption.setForeground(new Color(100, 116, 139));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
        value.setForeground(new Color(30, 41, 59));
        card.add(caption, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private void loadDashboard() {
        status.setText("Đang cập nhật dữ liệu...");
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                return queryDashboard();
            }

            @Override
            protected void done() {
                setCursor(java.awt.Cursor.getDefaultCursor());
                try {
                    DashboardData data = get();
                    branchesValue.setText(formatCount(data.branches));
                    employeesValue.setText(formatCount(data.employees));
                    customersValue.setText(formatCount(data.customers));
                    petsValue.setText(formatCount(data.pets));
                    appointmentsValue.setText(formatCount(data.todayAppointments));
                    revenueValue.setText(formatMoney(data.monthRevenue));
                    unpaidValue.setText(formatMoney(data.unpaidAmount));
                    lowStockValue.setText(formatCount(data.lowStock));
                    appointmentModel.setRowCount(0);
                    for (Object[] row : data.appointments) {
                        appointmentModel.addRow(row);
                    }
                    revenueChart.setData(data.monthLabels, data.monthRevenues);
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

    private DashboardData queryDashboard() throws SQLException {
        DashboardData data = new DashboardData();
        String metricsSql = "SELECT "
                + "(SELECT COUNT(*) FROM Branch) AS branches, "
                + "(SELECT COUNT(*) FROM Employee) AS employees, "
                + "(SELECT COUNT(*) FROM Customer) AS customers, "
                + "(SELECT COUNT(*) FROM Pet) AS pets, "
                + "(SELECT COUNT(*) FROM Appointment WHERE appointment_date >= CONVERT(date,GETDATE()) "
                + "AND appointment_date < DATEADD(day,1,CONVERT(date,GETDATE())) "
                + "AND status <> N'Cancelled') AS today_appointments, "
                + "(SELECT COALESCE(SUM(total_amount),0) FROM Invoice WHERE status=N'Paid' "
                + "AND created_date >= DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1) "
                + "AND created_date < DATEADD(month,1,DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1))) AS month_revenue, "
                + "(SELECT COALESCE(SUM(total_amount),0) FROM Invoice WHERE status=N'Unpaid') AS unpaid_amount, "
                + "(SELECT COUNT(*) FROM Service WHERE type IN (N'Thuoc',N'TiemPhong') "
                + "AND COALESCE(stock_quantity,0) <= 5) AS low_stock";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(metricsSql);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                data.branches = result.getLong("branches");
                data.employees = result.getLong("employees");
                data.customers = result.getLong("customers");
                data.pets = result.getLong("pets");
                data.todayAppointments = result.getLong("today_appointments");
                data.monthRevenue = result.getBigDecimal("month_revenue");
                data.unpaidAmount = result.getBigDecimal("unpaid_amount");
                data.lowStock = result.getLong("low_stock");
            }
            loadAppointments(connection, data);
            loadMonthlyRevenue(connection, data);
        }
        return data;
    }

    private void loadAppointments(Connection connection, DashboardData data) throws SQLException {
        String sql = "SELECT TOP 8 a.appointment_date,p.name AS pet_name,c.full_name AS customer_name,"
                + "b.name AS branch_name,a.status FROM Appointment a "
                + "JOIN Pet p ON p.pet_id=a.pet_id JOIN Customer c ON c.customer_id=a.customer_id "
                + "JOIN Branch b ON b.branch_id=a.branch_id "
                + "WHERE a.appointment_date >= CONVERT(date,GETDATE()) AND a.status IN (N'Pending',N'Confirmed') "
                + "ORDER BY a.appointment_date";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                data.appointments.add(new Object[]{
                    results.getTimestamp("appointment_date"), results.getString("pet_name"),
                    results.getString("customer_name"), results.getString("branch_name"),
                    results.getString("status")
                });
            }
        }
    }

    private void loadMonthlyRevenue(Connection connection, DashboardData data) throws SQLException {
        String sql = "SELECT DATEFROMPARTS(YEAR(created_date),MONTH(created_date),1) AS revenue_month,"
                + "COALESCE(SUM(total_amount),0) AS revenue FROM Invoice "
                + "WHERE status=N'Paid' AND created_date >= DATEADD(month,-5,"
                + "DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1)) "
                + "AND created_date < DATEADD(month,1,DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1)) "
                + "GROUP BY DATEFROMPARTS(YEAR(created_date),MONTH(created_date),1) ORDER BY revenue_month";
        java.time.YearMonth firstMonth = java.time.YearMonth.now().minusMonths(5);
        for (int month = 0; month < 6; month++) {
            java.time.YearMonth current = firstMonth.plusMonths(month);
            data.monthLabels.add(current.toString());
            data.monthRevenues.add(BigDecimal.ZERO);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                java.sql.Date date = results.getDate("revenue_month");
                java.time.YearMonth month = java.time.YearMonth.from(date.toLocalDate());
                int index = (int) java.time.temporal.ChronoUnit.MONTHS.between(firstMonth, month);
                if (index >= 0 && index < data.monthRevenues.size()) {
                    data.monthRevenues.set(index, results.getBigDecimal("revenue"));
                }
            }
        }
    }

    private String formatCount(long value) {
        return String.format(java.util.Locale.forLanguageTag("vi-VN"), "%,d", value);
    }

    private String formatMoney(BigDecimal value) {
        return String.format(java.util.Locale.forLanguageTag("vi-VN"), "%,.0f đ",
                value == null ? BigDecimal.ZERO : value);
    }

    private static final class DashboardData {
        private long branches;
        private long employees;
        private long customers;
        private long pets;
        private long todayAppointments;
        private long lowStock;
        private BigDecimal monthRevenue = BigDecimal.ZERO;
        private BigDecimal unpaidAmount = BigDecimal.ZERO;
        private final List<Object[]> appointments = new ArrayList<>();
        private final List<String> monthLabels = new ArrayList<>();
        private final List<BigDecimal> monthRevenues = new ArrayList<>();
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
            int plotHeight = Math.max(1, height - 58);
            int slotWidth = Math.max(1, (width - 40) / amounts.size());
            int barWidth = Math.max(12, Math.min(38, slotWidth - 12));
            for (int index = 0; index < amounts.size(); index++) {
                int x = 20 + index * slotWidth + (slotWidth - barWidth) / 2;
                int barHeight = maximum.signum() == 0 ? 0
                        : amounts.get(index).multiply(BigDecimal.valueOf(plotHeight))
                                .divide(maximum, 0, java.math.RoundingMode.HALF_UP).intValue();
                int y = height - 36 - barHeight;
                g.setColor(new Color(59, 130, 246));
                g.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
                g.setColor(new Color(71, 85, 105));
                String label = labels.get(index);
                g.drawString(label.substring(5), x - 4, height - 15);
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
