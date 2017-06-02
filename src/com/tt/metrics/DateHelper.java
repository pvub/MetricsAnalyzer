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
    private static String s_outputPattern = "yyyy-MM-dd HH:mm:ss,k,EEE,ww,MMM";
    // Formatter to use the set pattern
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    private static SimpleDateFormat s_outputformatter = new SimpleDateFormat(s_outputPattern);
    
    public static String format(Date dt)
    {
        return s_formatter.format(dt);
    }
    
    public static String formatCSV(Date dt)
    {
        return s_outputformatter.format(dt);
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
