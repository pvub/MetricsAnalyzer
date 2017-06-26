package com.tt.metrics;

/**
 * Interface to handle output
 * @author Udai
 */
public interface OutputProcessor {
    public void openSink(String label);
    public void processLine(String line);
    public void processComplete();
}
