package edu.northeastern.smartspendmax;

public enum Category {
    HOUSING("Housing"),
    TRANSPORTATION("Transportation"),
    GROCERY("Grocery"),
    UTILITIES("Utilities"),
    PERSONAL_EXPENSE("Personal Expense"),
    OTHER("Other");

    private final String stringValue;

    Category(String stringValue) {
        this.stringValue = stringValue;
    }
    public String getStringValue() {
        return stringValue;
    }
}
