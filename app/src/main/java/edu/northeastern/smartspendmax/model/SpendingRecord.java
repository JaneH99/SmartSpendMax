package edu.northeastern.smartspendmax.model;

public class SpendingRecord {

    private Double amount;
    private String category;
    private String timestamp;
    private String vendor;

    public Double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVendor() {
        return vendor;
    }
}
