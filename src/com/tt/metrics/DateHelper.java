package com.tt.metrics;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper functions for Date routines
 * @author Udai
 */
public class DateHelper {
    // Milliseconds
    private static int MINUTE_COUNT = 1000 * 60;
    // Date Time format from Log lines
    private static String s_expectedPattern = "yyyy-MM-dd HH:mm:ss";
    // Formatter to use the set pattern
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    
    public static String format(Date dt)
    {
        return s_formatter.format(dt);
    }
    
    public static long getMinutes(Date dt)
    {
        return dt.getTime() / (MINUTE_COUNT);
    }
    
    public static Date nextMinute(Date dt)
    {
        return new Date(dt.getTime() + MINUTE_COUNT);
    }
}
