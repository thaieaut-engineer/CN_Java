package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class DashboardData {
    private long branches;
    private long employees;
    private long customers;
    private long pets;
    private long todayAppointments;
    private long lowStock;
    private BigDecimal monthRevenue = BigDecimal.ZERO;
    private BigDecimal unpaidAmount = BigDecimal.ZERO;
    private List<AppointmentSummary> appointments = new ArrayList<>();
    private List<String> monthLabels = new ArrayList<>();
    private List<BigDecimal> monthRevenues = new ArrayList<>();

    public long getBranches() { return branches; }
    public void setBranches(long value) { branches = value; }
    public long getEmployees() { return employees; }
    public void setEmployees(long value) { employees = value; }
    public long getCustomers() { return customers; }
    public void setCustomers(long value) { customers = value; }
    public long getPets() { return pets; }
    public void setPets(long value) { pets = value; }
    public long getTodayAppointments() { return todayAppointments; }
    public void setTodayAppointments(long value) { todayAppointments = value; }
    public long getLowStock() { return lowStock; }
    public void setLowStock(long value) { lowStock = value; }
    public BigDecimal getMonthRevenue() { return monthRevenue; }
    public void setMonthRevenue(BigDecimal value) { monthRevenue = value; }
    public BigDecimal getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(BigDecimal value) { unpaidAmount = value; }
    public List<AppointmentSummary> getAppointments() { return List.copyOf(appointments); }
    public void setAppointments(List<AppointmentSummary> values) { appointments = new ArrayList<>(values); }
    public void addAppointment(AppointmentSummary value) { appointments.add(value); }
    public List<String> getMonthLabels() { return List.copyOf(monthLabels); }
    public void setMonthLabels(List<String> values) { monthLabels = new ArrayList<>(values); }
    public void addMonthLabel(String value) { monthLabels.add(value); }
    public List<BigDecimal> getMonthRevenues() { return List.copyOf(monthRevenues); }
    public void setMonthRevenues(List<BigDecimal> values) { monthRevenues = new ArrayList<>(values); }
    public void addMonthRevenue(BigDecimal value) { monthRevenues.add(value); }
    public void setMonthRevenue(int index, BigDecimal value) { monthRevenues.set(index, value); }
}
