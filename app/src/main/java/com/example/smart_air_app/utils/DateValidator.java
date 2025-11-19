package com.example.smart_air_app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MMM dd yyyy");
    static {
        FORMATTER.setLenient(false);
    }

    public static boolean isValidDate(String dateString) {
        try {
            Date date = FORMATTER.parse(dateString);
            return FORMATTER.format(date).equals(dateString);
        } catch (ParseException e) {
            return false;
        }
    }

    public static String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return DateValidator.makeDateString(day, month, year);
    }

    public static String makeDateString(int day, int month, int year) {
        return DateValidator.getMonthFormat(month) + " " + day + " " + year;
    }

    public static String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        return "JAN";
    }
}
