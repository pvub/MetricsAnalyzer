package com.tt.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author Udai
 */
public class Stats {
    private DataFrame m_frame;

    private TimeUnit m_unit;
    private long m_beginning_time_marker    = 0;
    private int  m_capacity                 = 0;
    
    public Stats(MetricsConfig config, TimeUnit unit) throws Exception
    {
        m_unit = unit;
        m_beginning_time_marker = config.getStartTimeMarker(unit);
        m_capacity              = (int) config.getTimeUnits(unit);
        m_frame = new DataFrame(config, unit);
    }
    
    public int getTimeMarkerIndex(long marker)
    {
        return (int) (marker - m_beginning_time_marker);
    }
    
    public void addStat(StatLine line)
    {
        int rowindex = getTimeMarkerIndex(line.getTimeMarker(m_unit));
        if (rowindex < 0 || rowindex >= getCapacity())
        {
            return;
        }
        RowContainer container;
        container = new RowContainer(rowindex, line.getSetPoints().size());
        line.getColumnIndicesAndValues(container);
        m_frame.addRowDetail(container);
    }
    
    public void performSimpleRegression(int col1, int col2, double predict)
    {
        this.m_frame.performSimpleRegression(col1, col2, predict);
    }
    
    public ArrayList<String> dumpAsList(ArrayList<String> statlist)
    {
        return m_frame.dumpAsList(statlist);
    }
    
    public ArrayList<String> dumpCorrelation(ArrayList<String> linelist)
    {
        return m_frame.dumpCorrelation(linelist);
    }

    /**
     * @return the m_capacity
     */
    public int getCapacity() {
        return m_capacity;
    }
}
