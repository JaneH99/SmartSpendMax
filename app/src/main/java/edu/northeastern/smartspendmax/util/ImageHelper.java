package edu.northeastern.smartspendmax.util;

import edu.northeastern.smartspendmax.R;

public class ImageHelper {

    public static int setImageBasedOnString(String variable) {
        int resourceId;

        // Mapping string variable to resource IDs
        switch (variable.toLowerCase()) {
            case "target":
                resourceId = R.drawable.adsmaker_target;
                break;
            case "amazon":
                resourceId = R.drawable.adsmaker_amazon;
                break;
            case "booking":
                resourceId = R.drawable.adsmaker_booking;
                break;
            case "ebay":
                resourceId = R.drawable.adsmaker_ebay;
                break;
            case "expedia":
                resourceId = R.drawable.adsmaker_expedia;
                break;
            case "macys":
                resourceId = R.drawable.adsmaker_macys;
                break;
            case "nordstorm":
                resourceId = R.drawable.adsmaker_nordstrom;
                break;
            case "priceline":
                resourceId = R.drawable.adsmaker_priceline;
                break;
            case "walmart":
                resourceId = R.drawable.adsmaker_walmart;
                break;
            default:
                resourceId = R.drawable.logo;
                break;
        }
        return resourceId;
    }
}
