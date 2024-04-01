package edu.northeastern.smartspendmax;

import java.time.LocalDate;
import java.util.Date;

public class SpendingTransaction {

    private LocalDate transactionDate;
    private Category category;
    private String vendor;
    private double amount;

    public SpendingTransaction(LocalDate transactionDate, Category category, String vendor, double amount) {
        this.transactionDate = transactionDate;
        this.category = category;
        this.vendor = vendor;
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
