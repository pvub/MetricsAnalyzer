package com.tt.metrics;

/**
 * Functional Interface to process StatLine as Lambda
 * @author Udai
 */
@FunctionalInterface
public interface StatLineProcessor
{
    void processLine(StatLine line);
}
