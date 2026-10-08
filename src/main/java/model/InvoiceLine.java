package model;

import java.math.BigDecimal;

public class InvoiceLine {
    private String name;
    private int quantity;
    private BigDecimal unitPrice;

    public InvoiceLine() {
    }

    public InvoiceLine(String name, int quantity, BigDecimal unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
