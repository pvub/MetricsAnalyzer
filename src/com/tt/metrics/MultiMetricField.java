package com.tt.metrics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.util.ArrayList;

/**
 *
 * @author Udai
 */
public class MultiMetricField extends MetricField 
{
    private DescriptiveStatistics ds = new DescriptiveStatistics();
    
    public MultiMetricField()
    {
    }
    @Override
    public void addValue(double val)
    {
        ds.addValue(val);
    }
    public double getMean()
    {
        return ds.getMean();
    }
    public double getSummaryValue(MetricsSource.SummaryType mtype)
    {
        double value = 0.0;
        switch (mtype)
        {
            case COUNT:
                value = ds.getN();
                break;
            case SUM:
                value = ds.getSum();
                break;
            case MEAN:
                value = ds.getMean();
                break;
            case STDDEV:
                value = ds.getStandardDeviation();
                break;
            case VARIANCE:
                value = ds.getPopulationVariance();
        }
        return value;
    }

}
