package edu.northeastern.smartspendmax.util;

import edu.northeastern.smartspendmax.model.Budget;

public class CommonConstants {

    public final static Budget defaultBudget = new Budget(2500, 500,
            2500, 500, 1000, 500);

    public final static String NODE_ADS = "ads";
    public final static String NODE_ADS_MAKERS = "adsMakers";
    public final static String NODE_BUDGET = "budget";
    public final static String NODE_CATEGORIES = "categories";

    public final static String NODE_COUPONS = "coupons";
    public final static String NODE_SPENDINGS = "spendings";
    public final static String NODE_USER_COUPON = "user-coupon";
    public final static String NODE_USERS = "users";
    public final static String ROLE = "role";

    public final static String ROLE_USER = "user";
    public final static String ROLE_ADS_MAKER = "adsMaker";


}