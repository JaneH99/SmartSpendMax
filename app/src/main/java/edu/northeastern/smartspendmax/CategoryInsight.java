package edu.northeastern.smartspendmax;

public class CategoryInsight {
    private String categoryName;
    private double budgetUsed;
    private double total;
    private double budgetLeft;

    public CategoryInsight(String categoryName, double budgetUsed, double total, double budgetLeft) {
        this.categoryName = categoryName;
        this.budgetUsed = budgetUsed;
        this.total = total;
        this.budgetLeft = budgetLeft;
    }

    public CategoryInsight(String categoryName, double budgetUsed, double total) {
        this.categoryName = categoryName;
        this.budgetUsed = budgetUsed;
        this.total = total;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getBudgetUsed() {
        return budgetUsed;
    }

    public void setBudgetUsed(float budgetUsed) {
        this.budgetUsed = budgetUsed;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public double getBudgetLeft() {
        return budgetLeft;
    }

    public void setBudgetLeft(float budgetLeft) {
        this.budgetLeft = budgetLeft;
    }
}
