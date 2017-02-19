package com.tt.metrics;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Define an input Metrics file
 * @author Udai
 */
public class MetricsFile {
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
    private String m_filename = "";
    private MetricType m_type = MetricType.SINGLE;
    private HashSet<String> m_columns = new HashSet<String>();
    private HashSet<DataPoint> m_datapoints = new HashSet<DataPoint>();
    private HashMap<DataPoint, MetricsFile.SummaryType> m_summarytype = new HashMap<DataPoint, MetricsFile.SummaryType>();
    
    public MetricsFile(String filename, MetricType type)
    {
        m_filename = filename;
        m_type = type;
    }
    public String getFilename()
    {
        return m_filename;
    }
    public MetricType getType()
    {
        return m_type;
    }
    public void addField(String column)
    {
        m_columns.add(column);
        DataPoint dp = DataPoint.getDataPoint(column);
        if (dp != DataPoint.MAX)
        {
            m_datapoints.add(dp);
        }
    }
    public void addField(String column, MetricsFile.SummaryType type)
    {
        m_columns.add(column);
        DataPoint dp = DataPoint.getDataPoint(column);
        if (dp != DataPoint.MAX)
        {
            m_datapoints.add(dp);
        }
        m_summarytype.put(dp, type);
    }
    public boolean hasField(String column)
    {
        return m_columns.contains(column);
    }
    public boolean hasDataPoint(DataPoint dp)
    {
        return m_datapoints.contains(dp);
    }
    public MetricsFile.SummaryType getSummaryTypeForDataPoint(DataPoint dp)
    {
        return m_summarytype.get(dp);
    }
    public HashSet<DataPoint> getDataPoints()
    {
        return m_datapoints;
    }
}
