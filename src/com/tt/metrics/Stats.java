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

    private Date m_beginning = null;
    private long m_beginning_minutes = 0;
    
    public Stats()
    {
        m_stat_lines = new ArrayList<StatLine>();
    }
    
    public void addMinuteStat(StatLine min)
    {
        long line_minutes = min.getDate().getTime() / (1000 * 60);
        if (m_beginning == null) 
        {
            m_beginning = min.getDate();
            m_beginning_minutes = m_beginning.getTime() / (1000 * 60);
        }
        else
        {
            long duration = m_beginning_minutes - line_minutes;
            if (duration > 0) {
                return;
            }
        }
        
        // Check if we have a StatLine for this Minute
        long index = line_minutes - m_beginning_minutes;
        if (index >= m_stat_lines.size())
        {
            m_stat_lines.add(min);
        }
        else
        {
            StatLine line = m_stat_lines.get((int)index);
            line.apply(min);
        }
    }
    
    public ArrayList<String> dumpAsList(ArrayList<String> statlist)
    {
        for (StatLine line : m_stat_lines)
        {
            statlist.add(line.toString());
        }
        return statlist;
    }
}
