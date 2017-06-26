package com.tt.metrics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Udai
 */
public class StatLine {
    private MultiMetricField[] m_summaryPoints;
    private Date     m_date = null;
    private long     m_minute = 0;
    private HashSet<DataPoint> m_setpoints = null;
    private static NumberFormat df = new DecimalFormat("0.00");
    
    public StatLine(MetricsSource mFile) 
    {
        m_setpoints = mFile.getDataPoints();
        if (mFile.getType() == MetricsSource.MetricType.SUMMARY)
        {
            m_summaryPoints = new MultiMetricField[DataPoints.getMax()];
            int index = 0;
            while(index < DataPoints.getMax())
            {
                m_summaryPoints[index] = new MultiMetricField();
                ++index;
            }
        }
    }
    
    public StatLine(MetricsConfig config) 
    {
        m_setpoints = new HashSet<DataPoint>();
        config.getDataPoints().fillDataPoints(m_setpoints);
        m_summaryPoints = new MultiMetricField[DataPoints.getMax()];
        int index = 0;
        while(index < DataPoints.getMax())
        {
            m_summaryPoints[index] = new MultiMetricField();
            ++index;
        }
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
        m_summaryPoints[point.getIndex()].addValue(value);
    }
    
    public double getDataPointValue(DataPoint point)
    {
        return m_summaryPoints[point.getIndex()].getSummaryValue(point.getSummaryType());
    }
    
    public double getSummaryValue(DataPoint point, MetricsSource.SummaryType mtype)
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
    public long getTimeMarker(TimeUnit unit)
    {
        return unit.convert(m_minute, TimeUnit.MINUTES);
    }
    
    public void apply(StatLine statline)
    {
        if (this.m_date == null)
        {
            this.setDate(statline.getDate());
        }
        for (DataPoint dp : statline.getSetPoints())
        {
            // Make sure we tag the DataPoint as Set in our StatLine
            // as we will be merging different stat lines
            m_setpoints.add(dp);
            setDataPoint(dp, statline.getDataPointValue(dp));
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(DateHelper.format(m_date)).append(" ");
        for (DataPoint dp : m_setpoints)
        {
            MultiMetricField field = m_summaryPoints[dp.getIndex()];
            sb.append(df.format(field.getSummaryValue(dp.getSummaryType())))
              .append("(").append(field.getCount()).append(")")
              .append(" ");
        }
        return sb.toString();
    }
}
