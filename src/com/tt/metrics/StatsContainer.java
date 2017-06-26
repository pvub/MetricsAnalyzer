package com.tt.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Container to hold multiple Stats data frames
 * Each Data Frame is aggregated by a unique TimeUnit
 * @author Udai
 */
public class StatsContainer 
{
    private final ArrayList<Stats>    m_stats;
    private final HashSet<TimeUnit>   m_uniqueunits;
    
    public StatsContainer() 
    {
        m_stats         = new ArrayList<>();
        m_uniqueunits   = new HashSet<>();
    }
    
    public void addStat(TimeUnit unit, MetricsConfig config)
            throws Exception
    {
        if (m_uniqueunits.add(unit))
        {
            Stats s = new Stats(config, unit);
            m_stats.add(s);
        }
    }
    
    public void addInstant(StatLine line)
    {
        m_stats.stream().forEach((s) -> {
            s.addInstant(line);
        });
    }
    
    public void processInstances()
    {
        m_stats.stream().forEach((s) -> {
            s.processInstances();
        });
    }
    
    public void output(OutputProcessor output)
    {
        m_stats.stream().forEach((s) -> {
//            s.openSink(output);
//            s.outputHeader(output);
//            s.outputDataFrame(output);
//            s.outputFooter(output);
            s.save();
        });
    }
}
