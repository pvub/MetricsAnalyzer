package com.tt.metrics;

import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to query REST API
 * @author Udai
 */
public class GraphiteRestHandler implements MetricsHandler 
{
    private static String s_graphiteDatePattern = "HH:mm_yyyyMMdd";
    private static String s_expectedPattern = "yyyy-MM-dd HH:mm:ss z";
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);
    private static SimpleDateFormat s_graphiteformatter = new SimpleDateFormat(s_graphiteDatePattern);
    private MetricsGraphiteSource m_sourceconfig = null;
    private Gson gson = new Gson();
    
    public GraphiteRestHandler(MetricsGraphiteSource sourceconfig) 
    {
        m_sourceconfig = sourceconfig;
    }
    
    /**
     *
     * @param mFile
     * @param config
     * @param container
     * @return 
     */
    @Override
    public void load(MetricsConfig config, StatLineProcessor processor) 
    {
        // Build the URL
        StringBuilder sb = new StringBuilder();
        sb.append(m_sourceconfig.getQueryString());
        sb.append("&from=").append(formatDateForGraphite(config.getStartDateStr()));
        sb.append("&until=").append(formatDateForGraphite(config.getEndDateStr()));
        System.out.println("REST URL: " + sb.toString());
        // Call the API
        RestAPIHelper apiHelper = new RestAPIHelper(sb.toString());
        String response = apiHelper.getData();
        // Process Response
        ProcessResponse(config, response, processor);
    }
    
    private void ProcessResponse(MetricsConfig config, String response, StatLineProcessor processor) 
    {
        System.out.println(response);
        Graphite[] data = gson.fromJson(response, Graphite[].class);
        for (Graphite g : data)
        {
            String target = g.getTarget();
            System.out.println("target=" + target);
            String trimmedTarget = m_sourceconfig.trimTarget(target);
            System.out.println(" trimmed target=" + trimmedTarget);
            String key = m_sourceconfig.getKeyForTarget(trimmedTarget);
            // Process data from each Graphite target
            ProcessTarget(config, g, key, processor);
        }
        processor.processComplete();
    }
    
    private void ProcessTarget(MetricsConfig config, Graphite target, String key, StatLineProcessor processor)
    {
        final DecimalFormat doubleFormatter = new DecimalFormat("###############");
        final String        searchDates     = config.getFromattedSearchDates();
        for (List<Double> dp : target.getDatapoints())
        {
            Double val = dp.get(0);
            Double time = dp.get(1);
            if (val == null)
            {
                continue;
            }
            Date dt = new Date(time.longValue() * 1000);
            System.out.println("Conn=" + val + " time=" + doubleFormatter.format(time) + " date=" + s_formatter.format(dt) + searchDates);
            
            // Filter for Search dates
            if (!config.isBetweenSearchDates(dt))
            {
                continue;
            }
            
            StatLine line = new StatLine(m_sourceconfig);
            line.setDate(dt);
            // Create DataPoint
            DataPoint d = config.getDataPoints().getDataPoint(key);
            if (d.getIndex() != DataPoints.getMax())
            {
                line.setDataPoint(d, val);
            }
            processor.processLine(line);
        }
    }
    
    private String formatDateForGraphite(String dateStr)
    {
        Date dt = null;
        try {
            dt = s_formatter.parse(dateStr);
        } catch (ParseException ex) {
            Logger.getLogger(GraphiteRestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        s_graphiteformatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String convertedDateStr = s_graphiteformatter.format(dt);
        return convertedDateStr;
    }
    
}
