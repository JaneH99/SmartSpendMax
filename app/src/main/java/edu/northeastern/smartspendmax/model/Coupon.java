package edu.northeastern.smartspendmax.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Coupon {

    private String adMakerName;
    private String discount;
    private String discountCategory;
    private String targetCategory;
    private String description;
    private String validity;
    private int collectedNumber;
    private String couponId;
    private Boolean collected;

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

    public Coupon(String couponId, String discount, String description, String validity, int collectedNumber) {
        this.couponId = couponId;
        this.discount = discount;
        this.description = description;
        this.validity = validity;
        this.collectedNumber = collectedNumber;
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

    public int getCollectedNumber() {
        return collectedNumber;
    }

    public void setCollectedNumber(int collectedNumber) {
        this.collectedNumber = collectedNumber;
    }


    public Boolean getCollected() {
        return collected;
    }

    public void setCollected(Boolean collected) {
        this.collected = collected;
    }
}
