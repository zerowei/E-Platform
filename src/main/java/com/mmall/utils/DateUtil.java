package com.mmall.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtil {

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date transfer2Date(String str, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String transfer2Str(Date date, String format) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static Date transfer2Date(String str) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String transfer2Str(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(FORMAT);
    }
}
