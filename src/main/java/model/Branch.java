/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Branch {
    private int branchId;
    private String name;
    private String address;
    private String phone;

    public Branch() {}

    public Branch(int branchId, String name, String address, String phone) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters
    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return name; // Hữu ích khi hiển thị lên JComboBox trong Swing
    }
}