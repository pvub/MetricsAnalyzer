package com.tt.metrics;

import java.util.ArrayList;
import java.util.Date;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 *
 * @author Udai
 */
public class Stats {
    private ArrayList<StatLine> m_stat_lines;
    private static long MAX_DURATION = MILLISECONDS.convert(1, MINUTES);
    private DataFrame m_frame;

    private long m_beginning_minute = 0;
    private int  m_capacity         = 0;
    
    public Stats(MetricsConfig config)
    {
        m_beginning_minute = config.getStartMinute();
        m_capacity         = config.getMinutes();
        m_frame = new DataFrame(config);
        m_stat_lines = new ArrayList<StatLine>();
    }
    
    public int getMinuteIndex(long minute)
    {
        return (int) (minute - m_beginning_minute);
    }
    
    public void addMinuteStat(StatLine line)
    {
        int rowindex = getMinuteIndex(line.getMinute());
        if (rowindex < 0 || rowindex >= getCapacity())
        {
            return;
        }
        RowContainer container;
        container = new RowContainer(rowindex, line.getSetPoints().size());
        line.getColumnIndicesAndValues(container);
        m_frame.addRowDetail(container);
    }
    
//    public void addMinuteStat(StatLine min)
//    {
//        if (m_beginning_minute == 0) 
//        {
//            m_beginning_minute = min.getMinute();
//        }
//        else
//        {
//            long duration = m_beginning_minute - min.getMinute();
//            if (duration > 0) {
//                return;
//            }
//        }
//        
//        // Check if we have a StatLine for this Minute
//        long index = min.getMinute() - m_beginning_minute;
//        if (index >= m_stat_lines.size())
//        {
//            m_stat_lines.add(min);
//        }
//        else
//        {
//            StatLine line = m_stat_lines.get((int)index);
//            line.apply(min);
//        }
//    }
    
//    public ArrayList<String> dumpAsList(ArrayList<String> statlist)
//    {
//        for (StatLine line : m_stat_lines)
//        {
//            statlist.add(line.toString());
//        }
//        return statlist;
//    }
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
