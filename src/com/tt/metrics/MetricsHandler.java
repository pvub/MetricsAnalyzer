package com.tt.metrics;

/**
 * Interface for a Source Handler
 * @author Udai
 */
public interface MetricsHandler {
    
    public void load(MetricsConfig config, StatLineProcessor processor);
    
}
