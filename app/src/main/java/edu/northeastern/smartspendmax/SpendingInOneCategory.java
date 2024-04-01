package edu.northeastern.smartspendmax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpendingInOneCategory{

    private Category category;
    private List<SpendingTransaction> spendingInTheCategory;
    private boolean isExpandable;

    public SpendingInOneCategory(Category category) {
        this.category = category;
        this.spendingInTheCategory = new ArrayList<>();
        this.isExpandable = false;
    }

    public SpendingInOneCategory(Category category, List<SpendingTransaction> spendingInTheCategory, boolean isExpandable) {
        this.category = category;
        this.spendingInTheCategory = spendingInTheCategory;
        this.isExpandable = isExpandable;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<SpendingTransaction> getSpendingInTheCategory() {
        return spendingInTheCategory;
    }

    public void setSpendingInTheCategory(List<SpendingTransaction> spendingInTheCategory) {
        this.spendingInTheCategory = spendingInTheCategory;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public void sortByTransactionDate() {
        spendingInTheCategory.sort(new Comparator<SpendingTransaction>() {
            @Override
            public int compare(SpendingTransaction transaction1, SpendingTransaction transaction2) {
                // Compare transaction dates
                return transaction2.getTransactionDate().compareTo(transaction1.getTransactionDate());
            }
        });
    }
}
