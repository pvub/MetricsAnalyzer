package com.tt.metrics;

/**
 * Configuration for File based Metrics file processing
 * @author Udai
 */
public class MetricsFileSource extends MetricsSource {
    private String m_filename = "";

    public MetricsFileSource(String filekey, MetricType type, MetricSource source)
    {
        super(filekey, type, source);
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
    public void addField(String column, String filekey, DataPoints dps, MetricsSource.SummaryType type)
    {
        m_columns.add(filekey + "-" + column);
        DataPoint dp = dps.getDataPoint(filekey + "-" + column);
        if (dp.getIndex() != DataPoints.getMax())
        {
            m_datapoints.add(dp);
        }
        m_summarytype.put(dp, type);
    }
    
    public String getFilename()
    {
        return m_filename;
    }
    public void setFilename(String filename)
    {
        m_filename = filename;
    }
}
