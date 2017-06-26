package com.tt.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Udai
 */
public class MetricsAnalytics 
{
    private static String getFilename(String outputfilepath, String label)
    {
        int i = outputfilepath.contains(".") ? outputfilepath.lastIndexOf('.') : outputfilepath.length();
        String dstName = outputfilepath.substring(0, i) + "_" + label + outputfilepath.substring(i);
        return dstName;
    }

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
            StatsContainer container = new StatsContainer();
            container.addStat(TimeUnit.MINUTES, config);
            container.addStat(TimeUnit.HOURS, config);
            
            for (MetricsHandler mHandler : metricshandlers)
            {
                mHandler.load(config, new StatLineProcessor() {
                    @Override
                    public void processLine(StatLine line)
                    {
                        container.addInstant(line);
                    }
                    @Override
                    public void processComplete()
                    {
                        // Done processing ALL lines for this Handler
                    }
                });
            }
            // Consolidate the various Stats containers
            container.processInstances();
            
            container.output(new OutputProcessor() {
                BufferedWriter writer = null;
                
                @Override
                public void openSink(String label)
                {
                    String filename = getFilename(config.getOutputFilepath(), label);
                    Path outfile = Paths.get(filename);
                    try
                    {
                        writer = Files.newBufferedWriter(outfile);
                    } catch (IOException e) {
                        System.out.println("Unable to write to output file:" + filename);
                    }
                    
                }
                
                @Override
                public void processLine(String line)
                {
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException ex) {
                        Logger.getLogger(MetricsAnalytics.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                @Override
                public void processComplete()
                {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MetricsAnalytics.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    writer = null;
                }
            });

//            ArrayList<String> statlist = new ArrayList<String>();
//            statlist.add(header);
//            container.dumpAsList(statlist);
    //        statlist = container.dumpCorrelation(statlist);
//            Path outfile = Paths.get(config.getOutputFilepath());
//            try
//            {
//                Files.write(outfile, statlist, Charset.forName("UTF-8"));
//            } catch (IOException e) {
//                System.out.println("Unable to write to output file");
//            }
        }
        catch (Exception e)
        {
            System.out.println("Unable to run. e=" + e.getMessage());
            e.printStackTrace();
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
