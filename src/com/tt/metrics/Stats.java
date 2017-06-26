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
    private StatLine[] m_lines = null;
    private DataFrame m_frame;
    private WekaFrame m_wekaFrame;

    private TimeUnit m_unit;
    private long m_beginning_time_marker    = 0;
    private int  m_capacity                 = 0;
    private String m_header;
    
    public Stats(MetricsConfig config, TimeUnit unit) throws Exception
    {
        m_unit                  = unit;
        m_beginning_time_marker = config.getStartTimeMarker(unit);
        m_capacity              = config.getTimeUnits(unit);
        m_frame                 = new DataFrame(config, unit);
        // Allocate Array of StatLines
        m_lines                 = new StatLine[m_capacity];
        Date dt                 = config.getStartTime();
        for (int index = 0; index < m_capacity; ++index) {
            StatLine line = new StatLine(config);
            line.setDate(dt);
            m_lines[index] = line;
            dt = DateHelper.next(m_unit, dt);
        }
        // Get header
        m_header = config.dumpDataPointsHeader();
        m_wekaFrame = new WekaFrame(config, unit);
    }
    
    public void save()
    {
        m_wekaFrame.save();
    }
    
    private int getTimeMarker(Date dt)
    {
        return (int) m_unit.convert(dt.getTime(), TimeUnit.MILLISECONDS);
    }
    
    public int getTimeMarkerIndex(long marker)
    {
        return (int) (marker - m_beginning_time_marker);
    }
    
    public void addInstant(StatLine line)
    {
        int rowindex = getTimeMarkerIndex(getTimeMarker(line.getDate()));
        // Validate if the idex is in range
        if (rowindex < 0 || rowindex >= m_capacity)
        {
            return;
        }
        // Get a StatLine for the index
//        System.out.println("Applying:" + line.toString());
//        System.out.println("Before:" + local_line.toString());
        m_lines[rowindex].apply(line);
//        System.out.println("After:" + local_line.toString());
        
    }
    
    public void processInstances()
    {
        // Now add all StatLines to the container
        for (StatLine line : m_lines)
        {
            if (line != null)
            {
                System.out.println("Stat Line: " + line.toString());
                addStat(line);
            }
        }
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
        System.out.println("Container: index=" + container.rowindex 
                          + " points=" + container.indices.length
                          + " values=" + container.values.length);
        m_frame.addRowDetail(container);
        m_wekaFrame.addRow(container);
    }
    
    public void performSimpleRegression(int col1, int col2, double predict)
    {
        this.m_frame.performSimpleRegression(col1, col2, predict);
    }
    
    public void openSink(OutputProcessor processor)
    {
        processor.openSink(m_unit.name());
    }
    
    public void outputHeader(OutputProcessor processor)
    {
        processor.processLine(m_header);
    }

    public void outputDataFrame(OutputProcessor processor)
    {
        m_frame.dumpAsList(processor);
    }
    
    public void outputFooter(OutputProcessor processor)
    {
        processor.processComplete();
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
