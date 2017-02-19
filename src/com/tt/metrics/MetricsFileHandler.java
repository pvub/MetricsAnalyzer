package com.tt.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Read a Metrics File, and load the fields into a Stats container
 * @author Udai
 */
public class MetricsFileHandler {
    private static String s_expectedPattern = "yyyy-dd-MM hh:mm:ss";
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    private StatLine m_multistatline = null;
    private Date     m_summarydate = null;
    
    public MetricsFileHandler()
    {
    }
    
    public Stats load(MetricsFile mFile, Stats container)
    {
        System.out.println("Loading: " + mFile.getFilename());
        try (Stream<String> stream = Files.lines(Paths.get(mFile.getFilename()))) 
        {
            stream.forEach((String str) -> {
                if (mFile.getType() == MetricsFile.MetricType.SUMMARY)
                {
                    ParseLineSummary(str, container, mFile);
                }
                else
                {
                    ParseLine(str, container, mFile);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (mFile.getType() == MetricsFile.MetricType.SUMMARY)
        {
            System.out.println("Stat Line: " + m_multistatline.toString());
            container.addMinuteStat(m_multistatline);            
        }
        
        return container;
    }
    
    private void ParseLine(String input, Stats stats, MetricsFile mFile)
    {
        String[] pairs = input.split(" ");
        int index = 0;
        String dateStr = "", timeStr = "";
        StatLine statline = new StatLine(mFile);
        for (String pair : pairs) 
        {
            if (index == 0)
            {
                dateStr = pair;
            }
            else if (index == 1)
            {
                timeStr = pair;
            }
            else
            {
                String[] parts = pair.split("=");
                String key = parts[0];
                String value = parts[1];
                if (mFile.hasField(key))
                {
                    DataPoint dp = DataPoint.getDataPoint(key);
                    if (dp != DataPoint.MAX)
                    {
                        double val = Double.parseDouble(value);
                        statline.setDataPoint(dp, val);
                    }
                }
            }
            index++;
        }
        try
        {
            Date dt = s_formatter.parse(dateStr + " " + timeStr);
            statline.setDate(dt);
            System.out.println("Stat Line: " + statline.toString());
            stats.addMinuteStat(statline);
        } catch (ParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
        }

    }

    private void ParseLineSummary(String input, Stats stats, MetricsFile mFile)
    {
        String[] pairs = input.split(" ");
        String dateStr = pairs[0];
        String timeStr = pairs[1];
        try
        {
            Date dt = s_formatter.parse(dateStr + " " + timeStr);
            if (m_summarydate == null)
            {
                m_summarydate = dt;
                m_multistatline = new StatLine(mFile);
                m_multistatline.setDate(dt);
            }
            else
            {
                long existingminute = m_summarydate.getTime() / (1000 * 60);
                long newminute = dt.getTime() / (1000 * 60);
                if (newminute != existingminute)
                {
                    System.out.println("Stat Line: " + m_multistatline.toString());
                    stats.addMinuteStat(m_multistatline);
                    m_summarydate = dt;
                    m_multistatline = new StatLine(mFile);
                    m_multistatline.setDate(dt);
                }
            }
        } catch (ParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
        }

        for (String pair : pairs) 
        {
            String[] parts = pair.split("=");
            if (parts.length != 2) {
                continue;
            }
            String key = parts[0];
            String value = parts[1];
            if (mFile.hasField(key))
            {
                DataPoint dp = DataPoint.getDataPoint(key);
                if (dp != DataPoint.MAX)
                {
                    double val = Double.parseDouble(value);
                    m_multistatline.setDataPoint(dp, val);
                }
            }
        }
    }
}
