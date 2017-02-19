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
        
        ArrayList<MetricsFile> metricsfiles = new ArrayList<MetricsFile>();
        // Define MetricsFile for process metrics
        MetricsFile processFile = new MetricsFile(filepath + File.separator + "process.txt", MetricsFile.MetricType.SINGLE);
        processFile.addField("cpu");
        processFile.addField("vm");
        metricsfiles.add(processFile);
        // Define MetricsFile for capacity metrics
        MetricsFile capacityFile = new MetricsFile(filepath + File.separator + "capacity.txt", MetricsFile.MetricType.SINGLE);
        capacityFile.addField("con_total");
        capacityFile.addField("out_avg");
        capacityFile.addField("out_peek");
        capacityFile.addField("out_bytes_avg");
        capacityFile.addField("out_bytes_peek");
        metricsfiles.add(capacityFile);
        // Define MetricsFile for Client Connection Metrics
        MetricsFile ccFile = new MetricsFile(filepath + File.separator + "client.txt", MetricsFile.MetricType.SUMMARY);
        ccFile.addField("user_id", MetricsFile.SummaryType.COUNT);
        ccFile.addField("out_bytes_peek", MetricsFile.SummaryType.SUM);
        ccFile.addField("out_bytes_avg", MetricsFile.SummaryType.SUM);
        ccFile.addField("out_lat_peek", MetricsFile.SummaryType.SUM);
        ccFile.addField("out_lat_avg", MetricsFile.SummaryType.SUM);
        ccFile.addField("rt_lat_peek", MetricsFile.SummaryType.SUM);
        ccFile.addField("rt_lat_avg", MetricsFile.SummaryType.SUM);
        ccFile.addField("qd_peek", MetricsFile.SummaryType.SUM);
        ccFile.addField("qd_avg", MetricsFile.SummaryType.SUM);
        metricsfiles.add(ccFile);
        // Define MetricsFile for Data Metrics
        MetricsFile dataFile = new MetricsFile(filepath + File.separator + "data.txt", MetricsFile.MetricType.SUMMARY);
        dataFile.addField("instr_id", MetricsFile.SummaryType.COUNT);
        dataFile.addField("in_md", MetricsFile.SummaryType.SUM);
        dataFile.addField("in_td", MetricsFile.SummaryType.SUM);
        dataFile.addField("out", MetricsFile.SummaryType.SUM);
        dataFile.addField("avg_lat", MetricsFile.SummaryType.SUM);
        metricsfiles.add(dataFile);

        Stats container = new Stats();
        for (MetricsFile mFile : metricsfiles)
        {
            MetricsFileHandler handler = new MetricsFileHandler();
            container = handler.load(mFile, container);
        }
        
        ArrayList<String> statlist = new ArrayList<String>();
        container.dumpAsList(statlist);
        Path outfile = Paths.get(filepath + File.separator + "result.txt");
        try
        {
            Files.write(outfile, statlist, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("Unable to write to output file");
        }
    }
    
}
