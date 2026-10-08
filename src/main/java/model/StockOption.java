package model;

public class StockOption {
    private int serviceId;
    private String name;
    private int stock;

    public StockOption() {
    }

    public StockOption(int serviceId, String name, int stock) {
        this.serviceId = serviceId;
        this.name = name;
        this.stock = stock;
    }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return name + "  ·  Tồn kho: " + stock;
    }
}
