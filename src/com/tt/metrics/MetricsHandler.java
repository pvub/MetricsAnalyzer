package com.tt.metrics;

/**
 * Interface for a Source Handler
 * @author Udai
 */
public interface MetricsHandler {
    
    public Stats load(MetricsConfig config, Stats container);
    
}
