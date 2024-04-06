package edu.northeastern.smartspendmax;

import java.time.LocalDate;

public class Coupon {
    private String id;
    private String discount;
    private String description;
    private LocalDate validity;

    private int collectedNumber;

    public Coupon(String id, String discount, String description, LocalDate validity, int collectedNumber) {
        this.id = id;
        this.discount = discount;
        this.description = description;
        this.validity = validity;
        this.collectedNumber = collectedNumber;
    }

    public String getId() {
        return id;
    }

    // Setter for the ID (if you need to change it after creation, which might not be typical for IDs)
    public void setId(String id) {
        this.id = id;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public int getCollectedNumber(){
        return collectedNumber;
    }

    public void setCollectedNumber(int collectedNumber) {
        this.collectedNumber = collectedNumber;
    }
}
