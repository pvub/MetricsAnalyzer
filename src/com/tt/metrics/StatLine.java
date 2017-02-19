package com.tt.metrics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Udai
 */
public class StatLine {
    private static String s_expectedPattern = "yyyy/dd/MM hh:mm:ss";
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    private double[] m_dataPoints;
    private MultiMetricField[] m_summaryPoints;
    private Date     m_date = null;
    private HashSet<DataPoint> m_setpoints = null;
    private MetricsFile.MetricType m_metricType;
    private MetricsFile m_metricsFile;
    
    public StatLine(MetricsFile mFile) 
    {
        m_metricType = mFile.getType();
        m_setpoints = mFile.getDataPoints();
        m_dataPoints = new double[DataPoint.MAX.getValue()];
        if (mFile.getType() == MetricsFile.MetricType.SUMMARY)
        {
            m_summaryPoints = new MultiMetricField[DataPoint.MAX.getValue()];
            int index = 0;
            while(index < DataPoint.MAX.getValue())
            {
                m_summaryPoints[index] = new MultiMetricField();
                ++index;
            }
        }
        this.m_metricsFile = mFile;
    }
    
    public HashSet<DataPoint> getSetPoints()
    {
        return m_setpoints;
    }
            
    public void setDataPoint(DataPoint point, double value)
    {
        if (m_metricType == MetricsFile.MetricType.SUMMARY)
        {
            m_summaryPoints[point.getValue()].addValue(value);
        }
        else
        {
            m_dataPoints[point.getValue()] = value;
        }
    }
    
    public double getDataPointValue(DataPoint point)
    {
        if (m_metricType == MetricsFile.MetricType.SUMMARY)
        {
            return m_summaryPoints[point.getValue()].getSummaryValue(m_metricsFile.getSummaryTypeForDataPoint(point));
        }
        else
        {
            return m_dataPoints[point.getValue()];
        }
    }
    
    public double getSummaryValue(DataPoint point, MetricsFile.SummaryType mtype)
    {
        return m_summaryPoints[point.getValue()].getSummaryValue(mtype);
    }
    
    public void setDate(Date dt)
    {
        this.m_date = dt;
    }
    
    public Date getDate()
    {
        return m_date;
    }
    
    public void apply(StatLine statline)
    {
        for (DataPoint dp : statline.getSetPoints())
        {
            setDataPoint(dp, statline.getDataPointValue(dp));
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(s_formatter.format(m_date)).append(" ");
        if (m_metricType == MetricsFile.MetricType.SUMMARY)
        {
            for (DataPoint dp : m_setpoints)
            {
                MultiMetricField field = m_summaryPoints[dp.getValue()];
                sb.append(field.getSummaryValue(m_metricsFile.getSummaryTypeForDataPoint(dp))).append(" ");
            }
        }
        else
        {
            for (double val : m_dataPoints)
            {
                sb.append(val).append(" ");
            }
        }
        return sb.toString();
    }
}
