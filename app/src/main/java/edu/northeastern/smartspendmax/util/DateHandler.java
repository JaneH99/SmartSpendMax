package edu.northeastern.smartspendmax.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHandler {

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());


    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return dateFormat.format(now);
    }

//    public static String getCurrentTime() {
//        Date now = new Date();
//        return dateTimeFormatter.format(now);
//    }

    public static String getCurrentDate() {
        Date now = new Date();
        return dateFormatter.format(now);
    }

    public static String formatDateToStr(Date date) {
        return dateFormatter.format(date);
    }

    public static Date parseStrToDate(String dateStr) {
        try {
            return dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
