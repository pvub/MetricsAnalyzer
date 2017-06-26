package com.tt.metrics;

/**
 * Functional Interface to process StatLine as Lambda
 * @author Udai
 */
public interface StatLineProcessor
{
    void processLine(StatLine line);
    void processComplete();
}
