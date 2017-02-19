package com.tt.metrics;

/**
 *
 * @author Udai
 */
public class SingleMetricField extends MetricField 
{
    private double value;
    @Override
    public void addValue(double val)
    {
        value = val;
    }
    public double getValue()
    {
        return value;
    }
}
