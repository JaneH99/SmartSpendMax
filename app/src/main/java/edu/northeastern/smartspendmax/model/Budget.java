package edu.northeastern.smartspendmax.model;

public class Budget {

    // "housing": "2000",
// "transportation": "500",
// "grocery": "1000",
// "utilities": "1000",
// "personal_expense": "500",
// "other": "1000"
    private Integer housing;
    private Integer transportation;
    private Integer grocery;
    private Integer utilities;
    private Integer personalExpense;
    private Integer other;

    public Budget() {
    }

    public Budget(Integer housing, Integer transportation,
                  Integer grocery, Integer utilities,
                  Integer personalExpense, Integer other) {
        this.housing = housing;
        this.transportation = transportation;
        this.grocery = grocery;
        this.utilities = utilities;
        this.personalExpense = personalExpense;
        this.other = other;
    }

    public Integer getHousing() {
        return housing;
    }

    public Integer getTransportation() {
        return transportation;
    }

    public Integer getGrocery() {
        return grocery;
    }

    public Integer getUtilities() {
        return utilities;
    }

    public Integer getPersonalExpense() {
        return personalExpense;
    }

    public Integer getOther() {
        return other;
    }
}
