package com.tt.metrics;

/**
 * Factory class to create Metrics Handler
 * @author Udai
 */
public class MetricsHandlerFactory {
    
    public static MetricsHandler getHandler(MetricsSource config)
    {
        if (config.getSource() == MetricsSource.MetricSource.FILE)
        {
            return new MetricsFileHandler((MetricsFileSource)config);
        }
        else if (config.getSource() == MetricsSource.MetricSource.SPLUNK)
        {
            return new SplunkHandler((MetricsSplunkSource)config);
        }
        else if (config.getSource() == MetricsSource.MetricSource.GRAPHITE)
        {
            return new GraphiteRestHandler((MetricsGraphiteSource)config);
        }
        return null;
    }
    
}
