package edu.northeastern.smartspendmax.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHandler {

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return dateFormat.format(now);
    }
}
