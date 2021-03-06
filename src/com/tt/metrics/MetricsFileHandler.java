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
public class MetricsFileHandler implements MetricsHandler 
{
    private static String s_expectedPattern = "yyyy-MM-dd hh:mm:ss";
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    private MetricsFileSource m_sourceconfig = null;
    private StatLine m_multistatline = null;
    private Date     m_summarydate = null;
    
    public MetricsFileHandler(MetricsFileSource sourceconfig)
    {
        m_sourceconfig = sourceconfig;
    }
    
    @Override
    public void load(MetricsConfig config, StatLineProcessor processor) 
    {
        System.out.println("Loading: " + m_sourceconfig.getFilename());
        try (Stream<String> stream = Files.lines(Paths.get(m_sourceconfig.getFilename()))) 
        {
            stream.forEach((String str) -> {
                if (m_sourceconfig.getType() == MetricsSource.MetricType.SUMMARY)
                {
                    ParseLineSummary(config, str, processor, m_sourceconfig);
                }
                else
                {
                    ParseLine(config, str, processor, m_sourceconfig);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        
//        if (m_sourceconfig.getType() == MetricsSource.MetricType.SUMMARY && m_multistatline != null)
//        {
//            System.out.println("Stat Line: " + m_multistatline.toString());
//            container.addStat(m_multistatline);            
//        }
//        
//        return container;
    }
    
    private boolean ParseLine(MetricsConfig config, String input, StatLineProcessor processor, MetricsSource mFile)
    {
        Date dt = null;
        String[] pairs = input.split(" ");

        // Get Time from Metrics line
        try
        {
            dt = s_formatter.parse(pairs[0] + " " + pairs[1]);
        } catch (ParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
        }
        // Filter for Search dates
        if (!config.isBetweenSearchDates(dt))
        {
            return false;
        }

        int index = 0;
        StatLine statline = new StatLine(mFile);
        statline.setDate(dt);
        for (String pair : pairs) 
        {
            if (index <= 1)
            {
                index++;
                continue;
            }
            else
            {
                String[] parts = pair.split("=");
                String key = parts[0];
                String value = parts[1];
                if (mFile.hasField(key, mFile.getFilekey()))
                {
                    DataPoint dp = config.getDataPoints().getDataPoint(mFile.getFilekey() + "-" + key);
                    if (dp.getIndex() != DataPoints.getMax())
                    {
                        double val = Double.parseDouble(value);
                        statline.setDataPoint(dp, val);
                    }
                }
            }
            index++;
        }
        System.out.println("Stat Line: " + statline.toString());
        processor.processLine(statline);
        
        return true;
    }

    private boolean ParseLineSummary(MetricsConfig config, String input, StatLineProcessor processor, MetricsSource mFile)
    {
        Date dt = null;
        String[] pairs = input.split(" ");

        // Get Time from Metrics line
        try
        {
            dt = s_formatter.parse(pairs[0] + " " + pairs[1]);
        } catch (ParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
        }
        // Filter for Search dates
        if (!config.isBetweenSearchDates(dt))
        {
            return false;
        }

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
                processor.processLine(m_multistatline);
                m_summarydate = dt;
                m_multistatline = new StatLine(mFile);
                m_multistatline.setDate(dt);
            }
        }

        for (String pair : pairs) 
        {
            String[] parts = pair.split("=");
            if (parts.length != 2) {
                continue;
            }
            String key = parts[0];
            String value = parts[1];
            if (mFile.hasField(key, mFile.getFilekey()))
            {
                DataPoint dp = config.getDataPoints().getDataPoint(mFile.getFilekey() + "-" + key);
                if (dp.getIndex() != DataPoints.getMax())
                {
                    double val = Double.parseDouble(value);
                    m_multistatline.setDataPoint(dp, val);
                }
            }
        }
        
        return true;
    }
}
