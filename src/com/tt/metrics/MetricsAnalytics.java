package com.tt.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 *
 * @author Udai
 */
public class MetricsAnalytics 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        String filepath = "";
        if (args.length > 0)
        {
            filepath = args[0];
        }
        
        MetricsConfig config = new MetricsConfig();
        config.load(filepath);
        if (!config.hasOutputFilepath())
        {
            System.out.println("No outputfile specified");
            return;
        }
        
        try
        {
            ArrayList<MetricsHandler> metricshandlers = config.getMetricsHandlers();
            Stats container = new Stats(config, TimeUnit.MINUTES);
            
            for (MetricsHandler mHandler : metricshandlers)
            {
                container = mHandler.load(config, container);
            }

            ArrayList<String> statlist = new ArrayList<String>();
            String header = config.dumpDataPointsHeader();
            statlist.add(header);
            container.dumpAsList(statlist);
    //        statlist = container.dumpCorrelation(statlist);
            Path outfile = Paths.get(config.getOutputFilepath());
            try
            {
                Files.write(outfile, statlist, Charset.forName("UTF-8"));
            } catch (IOException e) {
                System.out.println("Unable to write to output file");
            }
        }
        catch (Exception e)
        {
            System.out.println("Unable to run. e=" + e.getMessage());
        }
        
        // Perform Regression
//        String field1 = config.getDataPointHeader(0);
//        String field2 = config.getDataPointHeader(2);
//        System.out.println("Performing regression between " + field1 + " and " + field2);
//        container.performSimpleRegression(0, 2, 100.0d);
//        // Perform Regression
//        field1 = config.getDataPointHeader(0);
//        field2 = config.getDataPointHeader(3);
//        System.out.println("Performing regression between " + field1 + " and " + field2);
//        container.performSimpleRegression(0, 3, 100.0d);
//        // Perform Regression
//        field1 = config.getDataPointHeader(0);
//        field2 = config.getDataPointHeader(6);
//        System.out.println("Performing regression between " + field1 + " and " + field2);
//        container.performSimpleRegression(0, 6, 100.0d);
    }
    
}
