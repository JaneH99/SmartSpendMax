package edu.northeastern.smartspendmax;

import java.io.Serializable;

public class InvoiceInformation implements Serializable {
    private String invoiceVendor;
    private String invoiceDate;
    private String invoiceAmount;
    private String invoiceCategory;

    public InvoiceInformation(String invoiceVendor, String invoiceDate, String invoiceAmount, String invoiceCategory) {
        this.invoiceVendor = invoiceVendor;
        this.invoiceDate = invoiceDate;
        this.invoiceAmount = invoiceAmount;
        this.invoiceCategory = invoiceCategory;
    }

    public InvoiceInformation() {
    }

    public String getInvoiceVendor() {
        return invoiceVendor;
    }

    public void setInvoiceVendor(String invoiceVendor) {
        this.invoiceVendor = invoiceVendor;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }
}
