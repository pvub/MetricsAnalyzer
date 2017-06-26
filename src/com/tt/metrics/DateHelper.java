package com.tt.metrics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Helper functions for Date routines
 * @author Udai
 */
public class DateHelper {
    // Milliseconds
    private static int MINUTE_COUNT = 1000 * 60;
    // Date Time format from Log lines
    private static String s_dayPatternWeka  = "yyyy-MM-dd";
    private static String s_hourPatternWeka = "yyyy-MM-dd HH:00:00";
    private static String s_minPatternWeka  = "yyyy-MM-dd HH:mm:00";
    private static String s_expectedPattern = "yyyy-MM-dd HH:mm:ss";
    private static String s_outputPattern   = "yyyy-MM-dd HH:mm:ss,HH,EEE,ww,MMM";
    private static String s_dayPattern      = "yyyy-MM-dd HH:mm:ss,EEE,ww,MMM";
    // Formatter to use the set pattern
    private static SimpleDateFormat s_formatter         = new SimpleDateFormat(s_expectedPattern);
    private static SimpleDateFormat s_outputformatter   = new SimpleDateFormat(s_outputPattern);
    private static SimpleDateFormat s_dayformatter      = new SimpleDateFormat(s_dayPattern);
    // Date Labels
    private static SimpleDateFormat s_hourofday         = new SimpleDateFormat("HH");
    private static SimpleDateFormat s_dayofweek         = new SimpleDateFormat("EEE");
    private static SimpleDateFormat s_weekofyear        = new SimpleDateFormat("ww");
    private static SimpleDateFormat s_monthofyear       = new SimpleDateFormat("MMM");
    
    private static String getDateTimeString(TimeUnit tu, Date dt)
    {
        String date_str;
        switch (tu)
        {
            case DAYS:
                date_str = s_dayformatter.format(dt);
                break;
            case HOURS:
            case MINUTES:
            default:
                date_str = s_outputformatter.format(dt);
        }
        return date_str;
    }
    
    public static String format(Date dt)
    {
        return s_formatter.format(dt);
    }
    
    public static String formatCSV(TimeUnit tu, Date dt)
    {
        return getDateTimeString(tu, dt);
    }
    
    public static long getMinutes(Date dt)
    {
        return dt.getTime() / (MINUTE_COUNT);
    }
    
    public static Date next(TimeUnit tu, Date dt)
    {
        return new Date(dt.getTime() + tu.toMillis(1));
    }
    
    public static Date next(TimeUnit tu, Date dt, int marker)
    {
        if (marker == 0) return dt;
        return new Date(dt.getTime() + (tu.toMillis(1) * marker));
    }
    
    public static String getWekaDatePattern(TimeUnit tu)
    {
        switch (tu)
        {
            case MINUTES:
                return s_minPatternWeka;
            case HOURS:
                return s_hourPatternWeka;
            case DAYS:
                return s_dayPatternWeka;
            default:
                return s_expectedPattern;
        }
    }
    
    public static String getHourOfDayLabel(Date dt)
    {
        return s_hourofday.format(dt);
    }
    public static String getDayOfWeekLabel(Date dt)
    {
        return s_dayofweek.format(dt);
    }
    public static String getWeekOfYearLabel(Date dt)
    {
        return s_weekofyear.format(dt);
    }
    public static String getMonthOfYearLabel(Date dt)
    {
        return s_monthofyear.format(dt);
    }
}
