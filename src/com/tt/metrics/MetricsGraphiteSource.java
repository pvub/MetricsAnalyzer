package com.tt.metrics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Configuration for processing Splunk based Metrics
 * @author Udai
 */
public class MetricsGraphiteSource extends MetricsSource {
    
    private String m_url = "";
    private String m_username = "";
    private String m_password = "";
    private HashSet<String> m_queries = new HashSet<String>();
    private HashMap<String, String> m_mappedqueries = new HashMap<String, String>();
    
    public MetricsGraphiteSource(String filekey, MetricType type, MetricSource source)
    {
        super(filekey, type, source);
    }
    public String getUrl() 
    {
        return m_url;
    }
    public void setUrl(String url) 
    {
        this.m_url = url;
    }
    
    // Query can be a wildcard expression
    public void addFieldWithQuery(String column, String query, String filekey, DataPoints dps, MetricsSource.SummaryType type)
    {
        m_columns.add(filekey + "-" + column);
        m_queries.add(query);
        m_mappedqueries.put(query, filekey + "-" + column);
        DataPoint dp = dps.getDataPoint(filekey + "-" + column);
        if (dp.getIndex() != DataPoints.getMax())
        {
            m_datapoints.add(dp);
        }
        m_summarytype.put(dp, type);
    }
    
    public String getQueryString() 
    {
        StringBuilder sb = new StringBuilder(m_url);
        try
        {
            for (String target : m_queries)
            {
                String formattedTarget = getFormattedTarget(target);
                String encoded = URLEncoder.encode(formattedTarget, "UTF-8");
                sb.append("&").append("target=").append(encoded);
            }
        }
        catch(UnsupportedEncodingException e)
        {}
        return sb.toString();
    }
    private String getFormattedTarget(String target)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("summarize(")
          .append(target)
          .append(", \"1min\")");
        return sb.toString();
    }
    
    // Since we can add wildcard query, we'll need to match for a pattern
    public String getKeyForTarget(String target)
    {
        RegexKeyLookup lookup = new RegexKeyLookup();
        List<String> keys = lookup.lookup(target, m_mappedqueries);
        return keys.get(0);
    }
    public String trimTarget(String target)
    {
        int start = target.indexOf('(');
        int end   = target.indexOf(',');
        String trimmed = target.substring(start+1, end);
        return trimmed;
    }

    /**
     * @return the m_username
     */
    public String getUsername() {
        return m_username;
    }

    /**
     * @param m_username the m_username to set
     */
    public void setUsername(String m_username) {
        this.m_username = m_username;
    }

    /**
     * @return the m_password
     */
    public String getPassword() {
        return m_password;
    }

    /**
     * @param m_password the m_password to set
     */
    public void setPassword(String m_password) {
        this.m_password = m_password;
    }
}
