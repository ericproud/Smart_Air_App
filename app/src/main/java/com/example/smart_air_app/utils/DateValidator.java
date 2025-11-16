package com.example.smart_air_app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidator {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MMM dd yyyy");
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
}
