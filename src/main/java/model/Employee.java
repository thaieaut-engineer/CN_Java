/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Employee {
    private int employeeId;
    private int branchId;
    private String fullName;
    private String role; // Admin, BacSi, NhanVien
    private String username;
    private String password;
    private String accountStatus;

    public Employee() {
        this.accountStatus = "Active";
    }

    public Employee(int employeeId, int branchId, String fullName, String role, String username, String password) {
        this(employeeId, branchId, fullName, role, username, password, "Active");
    }

    public Employee(int employeeId, int branchId, String fullName, String role, String username,
            String password, String accountStatus) {
        this.employeeId = employeeId;
        this.branchId = branchId;
        this.fullName = fullName;
        this.role = role;
        this.username = username;
        this.password = password;
        this.accountStatus = accountStatus;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
}