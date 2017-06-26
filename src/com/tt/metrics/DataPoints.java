package com.tt.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Collection of Fields we extract from all metric files
 * @author Udai
 */
public class DataPoints {
    private HashMap<String, DataPoint> m_dataPoints;
    private ArrayList<DataPoint>       m_arrayDataPoints;
    private static int s_maxPointIndex = 0;
    
    DataPoints() {
        m_dataPoints = new HashMap<String, DataPoint>();
        m_arrayDataPoints = new ArrayList<DataPoint>();
    }
    
    public void addField(String filekey, String field, MetricsSource.SummaryType sType) {
        DataPoint dp = new DataPoint(filekey, field, s_maxPointIndex++, sType);
        m_arrayDataPoints.add(dp);
        m_dataPoints.put(filekey + "-" + field, dp);
    }
    
    public void fillDataPoints(HashSet<DataPoint> dps)
    {
        dps.addAll(this.m_arrayDataPoints);
    }
    
    public DataPoint getDataPoint(String label) {
        return m_dataPoints.get(label);
    }
    
    public DataPoint getDataPoint(int index) {
        return m_arrayDataPoints.get(index);
    }
    
    public static int getMax() {
        return s_maxPointIndex;
    }
    
    public String dumpHeaders()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("time").append(",")
          .append("hour").append(",")
          .append("day").append(",")
          .append("week").append(",")
          .append("month").append(",");
        for (DataPoint dp : m_arrayDataPoints)
        {
            sb.append(dp.getField()).append(",");
        }
        return sb.toString();
    }
}
