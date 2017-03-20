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
    private double[] m_dataPoints;
    private MultiMetricField[] m_summaryPoints;
    private Date     m_date = null;
    private long     m_minute = 0;
    private HashSet<DataPoint> m_setpoints = null;
    private MetricsFile.MetricType m_metricType;
    private MetricsFile m_metricsFile;
    
    public StatLine(MetricsFile mFile) 
    {
        m_metricType = mFile.getType();
        m_setpoints = mFile.getDataPoints();
        m_dataPoints = new double[DataPoints.getMax()];
        if (mFile.getType() == MetricsFile.MetricType.SUMMARY)
        {
            m_summaryPoints = new MultiMetricField[DataPoints.getMax()];
            int index = 0;
            while(index < DataPoints.getMax())
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
    
    public void getColumnIndicesAndValues(RowContainer container)
    {
        int i = 0;
        for (DataPoint dp : m_setpoints)
        {
            container.indices[i] = dp.getIndex();
            container.values[i] = getDataPointValue(dp);
            ++i;
        }
    }
    
    public void setDataPoint(DataPoint point, double value)
    {
        if (m_metricType == MetricsFile.MetricType.SUMMARY)
        {
            m_summaryPoints[point.getIndex()].addValue(value);
        }
        else
        {
            m_dataPoints[point.getIndex()] = value;
        }
    }
    
    public double getDataPointValue(DataPoint point)
    {
        if (m_metricType == MetricsFile.MetricType.SUMMARY)
        {
            return m_summaryPoints[point.getIndex()].getSummaryValue(m_metricsFile.getSummaryTypeForDataPoint(point));
        }
        else
        {
            return m_dataPoints[point.getIndex()];
        }
    }
    
    public double getSummaryValue(DataPoint point, MetricsFile.SummaryType mtype)
    {
        return m_summaryPoints[point.getIndex()].getSummaryValue(mtype);
    }
    
    public void setDate(Date dt)
    {
        this.m_date = dt;
        this.m_minute = DateHelper.getMinutes(dt);
    }
    
    public Date getDate()
    {
        return m_date;
    }
    public long getMinute()
    {
        return m_minute;
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
        sb.append(DateHelper.format(m_date)).append(" ");
        if (m_metricType == MetricsFile.MetricType.SUMMARY)
        {
            for (DataPoint dp : m_setpoints)
            {
                MultiMetricField field = m_summaryPoints[dp.getIndex()];
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
