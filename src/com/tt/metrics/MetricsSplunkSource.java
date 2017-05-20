package com.tt.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Configuration for processing Splunk based Metrics
 * @author Udai
 */
public class MetricsSplunkSource extends MetricsSource {
    
    private String m_url = "";
    private String m_query = "";
    private String m_username = "";
    private String m_password = "";
    private HashSet<String> m_patterns = new HashSet<String>();
    private HashSet<String> m_fields = new HashSet<String>();
    private HashMap<String, String> m_mappedpatterns = new HashMap<String, String>();
    private HashMap<String, String> m_mappedfields = new HashMap<String, String>();
    private HashSet<String> m_ignore_patterns = new HashSet<String>();
    
    public MetricsSplunkSource(String filekey, MetricType type, MetricSource source)
    {
        super(filekey, type, source);
    }
    public void addFieldWithPattern(String column, String pattern, String filekey, DataPoints dps, MetricsSource.SummaryType type)
    {
        m_columns.add(filekey + "-" + column);
        m_patterns.add(pattern);
        m_mappedpatterns.put(pattern, filekey + "-" + column);
        DataPoint dp = dps.getDataPoint(filekey + "-" + column);
        if (dp.getIndex() != DataPoints.getMax())
        {
            m_datapoints.add(dp);
        }
        m_summarytype.put(dp, type);
    }
    public void addExtractField(String column, String field, String filekey, DataPoints dps, MetricsSource.SummaryType type)
    {
        m_columns.add(filekey + "-" + column);
        m_fields.add(field);
        m_mappedfields.put(field, filekey + "-" + column);
        DataPoint dp = dps.getDataPoint(filekey + "-" + column);
        if (dp.getIndex() != DataPoints.getMax())
        {
            m_datapoints.add(dp);
        }
        m_summarytype.put(dp, type);
    }
    public void getMatchedKeys(String line, HashMap<String, Double> matched) {
        for (String pattern : m_ignore_patterns)
        {
            if (line.contains(pattern))
            {
                return;
            }
        }
        for (String pattern : m_patterns)
        {
            if (line.contains(pattern))
            {
                String key = m_mappedpatterns.get(pattern);
                if (key != null)
                {
                    matched.put(key, 1.0d);
                }
            }
        }
        for (String field : m_fields)
        {
            if (line.contains(field))
            {
                Double value = extractFieldValue(line, field);
                String key = m_mappedfields.get(field);
                if (key != null)
                {
                    matched.put(key, value);
                }
            }
        }
    }
    public void addIgnorePatterns(String[] ignore_patterns)
    {
        for (String pattern : ignore_patterns)
        {
            m_ignore_patterns.add(pattern);
        }
    }
    private Double extractFieldValue(String line, String field)
    {
        int indexOf = line.indexOf(field);
        String part = line.substring(indexOf);
        int eq = part.indexOf("=");
        int sp = part.indexOf(" ");
        String valstr = part.substring(eq+1, sp);
        return Double.valueOf(valstr);
    }
    public String getUrl() {
        return m_url;
    }
    public void setUrl(String url) {
        this.m_url = url;
    }
    public String getQuery() {
        return m_query;
    }
    public void setQuery(String query) {
        m_query = query;
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
