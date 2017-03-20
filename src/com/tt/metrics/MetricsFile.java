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
    private String m_filekey = "";
    private MetricType m_type = MetricType.SINGLE;
    private HashSet<String> m_columns = new HashSet<String>();
    private HashSet<DataPoint> m_datapoints = new HashSet<DataPoint>();
    private HashMap<DataPoint, MetricsFile.SummaryType> m_summarytype = new HashMap<DataPoint, MetricsFile.SummaryType>();
    
    public MetricsFile(String filename, String filekey, MetricType type)
    {
        m_filename = filename;
        m_filekey = filekey;
        m_type = type;
    }
    public String getFilename()
    {
        return m_filename;
    }
    public String getFilekey()
    {
        return m_filekey;
    }
    public MetricType getType()
    {
        return m_type;
    }
    public void addField(String column, String filekey, DataPoints dps)
    {
        m_columns.add(filekey + "-" + column);
        DataPoint dp = dps.getDataPoint(filekey + "-" + column);
        if (dp.getIndex() != DataPoints.getMax())
        {
            m_datapoints.add(dp);
        }
    }
    public void addField(String column, String filekey, DataPoints dps, MetricsFile.SummaryType type)
    {
        m_columns.add(filekey + "-" + column);
        DataPoint dp = dps.getDataPoint(filekey + "-" + column);
        if (dp.getIndex() != DataPoints.getMax())
        {
            m_datapoints.add(dp);
        }
        m_summarytype.put(dp, type);
    }
    public boolean hasField(String column, String filekey)
    {
        return m_columns.contains(filekey + "-" + column);
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
