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
import java.util.HashMap;
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
    
    public SplunkHandler(MetricsSplunkSource sourceconfig) 
    {
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
    public void load(MetricsConfig config, StatLineProcessor processor)
    {
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
//                    System.out.println("_time:  " + event.get("_time"));
//                    System.out.println("_raw:  " + event.get("_raw"));
                    ParseLineSummary(config, event.get("_time"), event.get("_raw"), processor);
                }
            }
            reader.close();
        }
        catch(IOException e) {
            
        }
        
        processor.processComplete();
    }
    
    private boolean ParseLineSummary(MetricsConfig config, String time_str, String input, StatLineProcessor processor)
    {
        Date dt = null;
        // Get Time from Metrics line
        try
        {
            dt = s_formatter.parse(time_str.trim());
        } catch (ParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
        }
        // Filter for Search dates
        if (!config.isBetweenSearchDates(dt))
        {
            return false;
        }
        // Check if we have any of our patterns in the line
        HashMap<String, Double> matched = new HashMap<String, Double>();
        m_sourceconfig.getMatchedKeys(input /*tokens[5]*/, matched);
        if (matched.isEmpty())
        {
            return false;
        }
        
        StatLine line = new StatLine(m_sourceconfig);
        line.setDate(dt);
        // Create DataPoint
        for (String key : matched.keySet())
        {
            DataPoint dp = config.getDataPoints().getDataPoint(key);
            if (dp.getIndex() != DataPoints.getMax())
            {
                line.setDataPoint(dp, matched.get(key));
            }
        }
        processor.processLine(line);
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
