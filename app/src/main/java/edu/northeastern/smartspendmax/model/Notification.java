package edu.northeastern.smartspendmax.model;

// TODO: rename to Coupon?
public class Notification {

//    "discount": "10%",
//    "discountCategory": "grocery",
//    "target_category": "grocery",
//    "description": "Get 25% off your next purchase with code SAVE25!",
//    "validity": "2100-12-31T00:00:00Z",
//    "adMakerName": "Target"

    private String adMakerName;
    private String discount;
    private String discountCategory;
    private String targetCategory;
    private String description;
    private String validity;

    private String couponId;

    public String getAdMakerName() {
        return adMakerName;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDiscountCategory() {
        return discountCategory;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public String getDescription() {
        return description;
    }

    public String getValidity() {
        return validity;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
}
