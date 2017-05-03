package com.tt.metrics;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Define an input Metrics file
 * @author Udai
 */
public class MetricsSource {

    public enum MetricSource
    {
        FILE,
        SPLUNK,
        GRAPHITE
    }
    public enum MetricType
    {
        SINGLE,
        SUMMARY
    }
    public enum SummaryType
    {
        COUNT,
        SUM,
        MEAN,
        STDDEV,
        VARIANCE
    }
    private String m_filekey = "";
    private MetricSource m_source = MetricSource.FILE;
    private MetricType m_type = MetricType.SINGLE;
    
    protected HashSet<String> m_columns = new HashSet<String>();
    protected HashSet<DataPoint> m_datapoints = new HashSet<DataPoint>();
    protected HashMap<DataPoint, MetricsSource.SummaryType> m_summarytype = new HashMap<DataPoint, MetricsSource.SummaryType>();
    
    public MetricsSource(String filekey, MetricType type, MetricSource source)
    {
        m_filekey = filekey;
        m_type = type;
        m_source = source;
    }
    public String getFilekey()
    {
        return m_filekey;
    }
    public MetricType getType()
    {
        return m_type;
    }
    public MetricSource getSource()
    {
        return m_source;
    }
    public boolean hasField(String column, String filekey)
    {
        return m_columns.contains(filekey + "-" + column);
    }
    public boolean hasDataPoint(DataPoint dp)
    {
        return m_datapoints.contains(dp);
    }
    public MetricsSource.SummaryType getSummaryTypeForDataPoint(DataPoint dp)
    {
        return m_summarytype.get(dp);
    }
    public HashSet<DataPoint> getDataPoints()
    {
        return m_datapoints;
    }
}
