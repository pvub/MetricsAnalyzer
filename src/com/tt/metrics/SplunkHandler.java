package com.tt.metrics;

import com.splunk.Event;
import com.splunk.JobExportArgs;
import com.splunk.MultiResultsReaderXml;
import com.splunk.SearchResults;
import com.splunk.Service;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Helper class to query Splunk
 * @author Udai
 */
public class SplunkHandler implements MetricsHandler 
{
    
    private static String s_expectedPattern = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    private Service m_service = null;
    private MetricsSplunkSource m_sourceconfig = null;
    private StatLine[] m_lines = null;
    
    public SplunkHandler(MetricsSplunkSource sourceconfig) 
    {
        s_formatter.setTimeZone(TimeZone.getTimeZone("CDT"));
        m_sourceconfig = sourceconfig;
    }
    
    private void connect(MetricsConfig config) 
    {
        // Log in using a basic authorization header
        // Note: Do not call the Service.login method
        m_service = new Service(m_sourceconfig.getUrl(), 8089);
        String credentials = m_sourceconfig.getUsername() + ":" + m_sourceconfig.getPassword();
        String basicAuthHeader = Base64.encode(credentials.getBytes());
        m_service.setToken("Basic " + basicAuthHeader);
		
        // Print the session token
        System.out.println("Your session token: " + m_service.getToken());
    }
        
    /**
     *
     * @param mFile
     * @param config
     * @param container
     * @return 
     */
    @Override
    public Stats load(MetricsConfig config, Stats container) 
    {
        // Allocate Array of StatLines
        m_lines = new StatLine[container.getCapacity()];
        // Connect to Splunk
        connect(config);
        
        // Get Search Dates
        String startDateStr = translateDateFormat(config.getStartDateStr());
        String endDateStr   = translateDateFormat(config.getEndDateStr());
        
        // Create an argument map for the export arguments
        JobExportArgs exportArgs = new JobExportArgs();
        exportArgs.setEarliestTime(startDateStr);
        exportArgs.setLatestTime(endDateStr);
        exportArgs.setSearchMode(JobExportArgs.SearchMode.NORMAL); 
        exportArgs.setOutputMode(JobExportArgs.OutputMode.XML);
        
        // Run the search with a search query and export arguments
        String mySearch = m_sourceconfig.getQuery();
        InputStream exportSearch = m_service.export(mySearch, exportArgs);
        
        System.out.println("Splunk Query=" + mySearch + " start=" + startDateStr + " end=" + endDateStr);
        
        try {
            MultiResultsReaderXml reader = new MultiResultsReaderXml(exportSearch);
            for (SearchResults searchResults : reader)
            {
                if (searchResults.isPreview())
                {
                    continue;
                }
                for (Event event : searchResults) 
                {
                    System.out.println("_raw:  " + event.get("_raw"));
                    ParseLineSummary(config, event.get("_raw"), container);
                }
            }
            reader.close();
        }
        catch(IOException e) {
            
        }
        
        // Now add all StatLines to the container
        for (StatLine line : m_lines)
        {
            if (line != null)
            {
                System.out.println("Stat Line: " + line.toString());
                container.addMinuteStat(line);
            }
        }
        return container;
    }
    
    private boolean ParseLineSummary(MetricsConfig config, String input, Stats stats)
    {
        Date dt = null;
        String[] tokens = input.split("\\|");

        // Get Time from Metrics line
        try
        {
            dt = s_formatter.parse(tokens[0].trim());
        } catch (ParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
        }
        // Filter for Search dates
        if (!config.isBetweenSearchDates(dt))
        {
            return false;
        }
        // Check if we have any of our patterns in the line
        ArrayList<String> matched = m_sourceconfig.getMatchedKeys(tokens[5]);
        if (matched.isEmpty())
        {
            return false;
        }
        
        // Get the StatLine for this time slot
        long minute = dt.getTime() / (1000 * 60);
        // Calculate row index
        int rowindex = stats.getMinuteIndex(minute);
        // Validate if the idex is in range
        if (rowindex < 0 || rowindex >= stats.getCapacity())
        {
            return false;
        }
        // Get a StatLine for the index
        StatLine line = m_lines[rowindex];
        if (line == null)
        {
            line = new StatLine(m_sourceconfig);
            line.setDate(dt);
        }
        // Create DataPoint
        for (String key : matched)
        {
            DataPoint dp = config.getDataPoints().getDataPoint(key);
            if (dp.getIndex() != DataPoints.getMax())
            {
                line.setDataPoint(dp, 1.0);
            }
        }
        m_lines[rowindex] = line;
        
        return true;
    }

    private String translateDateFormat(String inStr) {
        String outStr = null;
        
        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        SimpleDateFormat sdfout = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        try {
            Date dt = sdfin.parse(inStr);
            outStr = sdfout.format(dt);
        }
        catch (ParseException e) {
            
        }
        return outStr;
    }
    
}
