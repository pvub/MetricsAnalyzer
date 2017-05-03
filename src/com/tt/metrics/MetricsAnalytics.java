package com.tt.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        
        ArrayList<MetricsHandler> metricshandlers = config.getMetricsHandlers();
        Stats container = new Stats(config);
        for (MetricsHandler mHandler : metricshandlers)
        {
            container = mHandler.load(config, container);
        }
        
        ArrayList<String> statlist = new ArrayList<String>();
        String header = config.dumpDataPointsHeader();
        statlist.add(header);
        container.dumpAsList(statlist);
        statlist = container.dumpCorrelation(statlist);
        Path outfile = Paths.get(config.getOutputFilepath());
        try
        {
            Files.write(outfile, statlist, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("Unable to write to output file");
        }
    }
    
}
