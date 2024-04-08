package edu.northeastern.smartspendmax.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Coupon {

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

    public Coupon() {
    }

    public Coupon(String adMakerName, String discount,
                  String discountCategory, String targetCategory,
                  String description, String validity) {
        this.adMakerName = adMakerName;
        this.discount = discount;
        this.discountCategory = discountCategory;
        this.targetCategory = targetCategory;
        this.description = description;
        this.validity = validity;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("adMakerName", adMakerName);
        result.put("discount", discount);
        result.put("discountCategory", discountCategory);
        result.put("targetCategory", targetCategory);
        result.put("description", description);
        result.put("validity", validity);

        return result;
    }

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
