package com.tt.metrics;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Configuration Container
 * @author Udai
 */
public class MetricsConfig {
        
    private String m_filepath;
    private Properties m_props;
    private DataPoints m_dataPoints;
    private Date m_startTime;
    private long m_startMinute = 0;
    private long m_endMinute = 0;
    private Date m_endTime;
    private String m_outfilepath = null;
    
    private static String s_expectedPattern = "yyyy-MM-dd hh:mm:ss z";
    private static SimpleDateFormat s_formatter = new SimpleDateFormat(s_expectedPattern);

    public MetricsConfig() {
        m_dataPoints = new DataPoints();
    }
    
    public DataPoints getDataPoints() {
        return m_dataPoints;
    }
    
    public String dumpDataPointsHeader()
    {
        return m_dataPoints.dumpHeaders();
    }
    
    public String getDataPointHeader(int field_index)
    {
        return m_dataPoints.getDataPoint(field_index).getField();
    }
    
    public void load(String configFilePath) {
        this.m_filepath = configFilePath;
        m_props = new Properties();
        try {
            m_props.load(new FileInputStream(m_filepath));
            setStartAndEndDates();
            setOutputFile();
        } 
        catch (IOException ioe) {
            System.out.println("Unable to read file: " + m_filepath + ". Exception:" + ioe.getMessage());
        }
    }
    
//    public ArrayList<MetricsSource> getMetricsSourceConfig() {
//        
//        ArrayList<MetricsSource> sources = new ArrayList<MetricsSource>();
//        ArrayList<String> filekeys = getPropertyValues(Definitions.METRICS_FILES);
//        for (String filekey : filekeys) {
//            MetricsSource config = generateMetricsFile(filekey);
//            sources.add(config);
//        }
//        return sources;
//    }
    
    public ArrayList<MetricsHandler> getMetricsHandlers() {
        
        ArrayList<MetricsHandler> handlers = new ArrayList<MetricsHandler>();
        ArrayList<String> filekeys = getPropertyValues(Definitions.METRICS_FILES);
        for (String filekey : filekeys) {
            MetricsHandler handler = generateMetricsHandler(filekey);
            handlers.add(handler);
        }
        return handlers;
    }

    private ArrayList<String> getPropertyValues(String key) {
        ArrayList<String> vals = new ArrayList<String>();
        String val = m_props.getProperty(key);
        if (val != null) {
            String[] tokens = val.split("\\|");
            for (String token : tokens) {
                vals.add(token);
            }
        }
        return vals;
    }
    
    private String getProperty(String key) {
        return m_props.getProperty(key);
    }
    
    public MetricsHandler generateMetricsHandler(String filekey)
    {
        String source   = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_SOURCE);
        String type     = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_FILE_TYPE);
        MetricsSource.MetricType mtype = getMetricsFileType(type);
        MetricsSource.MetricSource msource = getMetricsSource(source);
        MetricsHandler mHandler = null;
        switch (msource)
        {
            case FILE:
                mHandler = generateMetricsHandlerForFileSource(filekey, mtype, msource);
                break;
            case SPLUNK:
                mHandler = generateMetricsHandlerForSplunkSource(filekey, mtype, msource);
                break;
            case GRAPHITE:
                mHandler = generateMetricsHandlerForGraphiteSource(filekey, mtype, msource);
                break;
        }
        return mHandler;
    }
    
    public MetricsHandler generateMetricsHandlerForFileSource( String filekey
                                                            , MetricsSource.MetricType mtype
                                                            , MetricsSource.MetricSource msource)
    {
        MetricsFileSource mfile = new MetricsFileSource(filekey, mtype, msource);
        String path     = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_FILE_PATH);
        mfile.setFilename(path);
        
        String fields = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_FILE_FIELDS);
        String fields_array[] = fields.split("\\|");
        for (String field : fields_array) 
        {
            m_dataPoints.addField(filekey, field);
            if (mfile.getType() == MetricsSource.MetricType.SINGLE) {
                mfile.addField(field, filekey, m_dataPoints);
            } else {
                String summarytype = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + field + "." + Definitions.METRICS_FIELD_SUMMARY);
                MetricsSource.SummaryType stype = getMetricsFieldSummaryType(summarytype);
                mfile.addField(field, filekey, m_dataPoints, stype);
            }
        }
        
        MetricsFileHandler handler = (MetricsFileHandler) MetricsHandlerFactory.getHandler(mfile);
        
        return handler;
    }
    
    public MetricsHandler generateMetricsHandlerForSplunkSource(String filekey
                                                            , MetricsSource.MetricType mtype
                                                            , MetricsSource.MetricSource msource)
    {
        MetricsSplunkSource mfile = new MetricsSplunkSource(filekey, mtype, msource);
        String url     = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.URL);
        String query   = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.QUERY);
        String user    = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.USERNAME);
        String pwd     = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.PASSWORD);
        mfile.setUrl(url);
        mfile.setQuery(query);
        mfile.setUsername(user);
        mfile.setPassword(pwd);
        
        String fields = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_FILE_FIELDS);
        String fields_array[] = fields.split("\\|");
        for (String field : fields_array) 
        {
            m_dataPoints.addField(filekey, field);
            String fieldpattern = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + field + "." + Definitions.METRICS_FIELD_PATTERN);
            String fieldextract = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + field + "." + Definitions.METRICS_EXTRACT_FEILD);
            if (mfile.getType() == MetricsSource.MetricType.SINGLE) {
                // Unsupported
            } else {
                String summarytype = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + field + "." + Definitions.METRICS_FIELD_SUMMARY);
                MetricsSource.SummaryType stype = getMetricsFieldSummaryType(summarytype);
                if (fieldpattern != null) {
                    mfile.addFieldWithPattern(field, fieldpattern, filekey, m_dataPoints, stype);
                }
                if (fieldextract != null) {
                    mfile.addExtractField(field, fieldextract, filekey, m_dataPoints, stype);
                }
            }
        }
        
        // Set Ignore patterns
        String ignore_patterns = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_IGNORE_PATTERNS);
        if (ignore_patterns != null)
        {
            String ignore_array[] = ignore_patterns.split("\\|");
            mfile.addIgnorePatterns(ignore_array);
        }
        
        SplunkHandler handler = (SplunkHandler) MetricsHandlerFactory.getHandler(mfile);
        
        return handler;
    }
    
    public MetricsHandler generateMetricsHandlerForGraphiteSource(String filekey
                                                            , MetricsSource.MetricType mtype
                                                            , MetricsSource.MetricSource msource)
    {
        MetricsGraphiteSource mfile = new MetricsGraphiteSource(filekey, mtype, msource);
        String url     = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.URL);
        String user    = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.USERNAME);
        String pwd     = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.PASSWORD);
        mfile.setUrl(url);
        mfile.setUsername(user);
        mfile.setPassword(pwd);
        
        String fields = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + Definitions.METRICS_FILE_FIELDS);
        String fields_array[] = fields.split("\\|");
        for (String field : fields_array) 
        {
            m_dataPoints.addField(filekey, field);
            String fieldquery = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + field + "." + Definitions.METRICS_FIELD_QUERY);
            if (mfile.getType() == MetricsSource.MetricType.SINGLE) {
                // Unsupported
            } else {
                String summarytype = getProperty(Definitions.METRICS_FILE_KEY + filekey + "." + field + "." + Definitions.METRICS_FIELD_SUMMARY);
                MetricsSource.SummaryType stype = getMetricsFieldSummaryType(summarytype);
                mfile.addFieldWithQuery(field, fieldquery, filekey, m_dataPoints, stype);
            }
        }
        
        GraphiteRestHandler handler = (GraphiteRestHandler) MetricsHandlerFactory.getHandler(mfile);
        
        return handler;
    }
    
    private MetricsSource.MetricType getMetricsFileType(String type) {
        MetricsSource.MetricType mtype = MetricsSource.MetricType.SINGLE;
        if (type.equalsIgnoreCase(Definitions.METRICS_TYPE_SINGLE)) {
            mtype = MetricsSource.MetricType.SINGLE;
        }
        if (type.equalsIgnoreCase(Definitions.METRICS_TYPE_SUMMARY)) {
            mtype = MetricsSource.MetricType.SUMMARY;
        }
        return mtype;
    }
    
    private MetricsSource.MetricSource getMetricsSource(String source) {
        MetricsSource.MetricSource msource = MetricsSource.MetricSource.FILE;
        if (source.equalsIgnoreCase(Definitions.METRICS_SOURCE_SPLUNK)) {
            msource = MetricsSource.MetricSource.SPLUNK;
        }
        else if (source.equalsIgnoreCase(Definitions.METRICS_SOURCE_GRAPHITE)) {
            msource = MetricsSource.MetricSource.GRAPHITE;
        }
        return msource;
    }
    
    private MetricsSource.SummaryType getMetricsFieldSummaryType(String type) {
        MetricsSource.SummaryType stype = MetricsSource.SummaryType.COUNT;
        if (type.equalsIgnoreCase(Definitions.METRICS_SUMMARY_COUNT)) {
            stype = MetricsSource.SummaryType.COUNT;
        }
        if (type.equalsIgnoreCase(Definitions.METRICS_SUMMARY_SUM)) {
            stype = MetricsSource.SummaryType.SUM;
        }
        if (type.equalsIgnoreCase(Definitions.METRICS_SUMMARY_MEAN)) {
            stype = MetricsSource.SummaryType.MEAN;
        }
        if (type.equalsIgnoreCase(Definitions.METRICS_SUMMARY_STDDEV)) {
            stype = MetricsSource.SummaryType.STDDEV;
        }
        if (type.equalsIgnoreCase(Definitions.METRICS_SUMMARY_VARIANCE)) {
            stype = MetricsSource.SummaryType.VARIANCE;
        }
        return stype;
    }
    
    private void setStartAndEndDates()
    {
        String start = getProperty(Definitions.METRICS_KEY + Definitions.METRICS_START);
        String end = getProperty(Definitions.METRICS_KEY + Definitions.METRICS_END);
        try
        {
            Date dtStart = s_formatter.parse(start);
            Date dtEnd = s_formatter.parse(end);
            if (dtEnd.before(dtStart))
            {
                System.out.println("Start Date is after End Date.");
            }
            m_startTime = dtStart;
            m_startMinute = DateHelper.getMinutes(dtStart);
            m_endTime = dtEnd;
            m_endMinute = DateHelper.getMinutes(dtEnd);
        }
        catch (ParseException e)
        {
            System.out.println("Unable to parse Start and End dates. e:" + e.getMessage());
        }
    }
    
    private void setOutputFile()
    {
        String outfile = getProperty(Definitions.METRICS_KEY + Definitions.METRICS_OUTPUTFILE);
        if (outfile != null && outfile.trim().length() > 0)
        {
            m_outfilepath = outfile;
        }
    }
    
    public String getStartDateStr()
    {
        String start = getProperty(Definitions.METRICS_KEY + Definitions.METRICS_START);
        return start;
    }
    public String getEndDateStr()
    {
        String end = getProperty(Definitions.METRICS_KEY + Definitions.METRICS_END);
        return end;
    }
    public Date getStartTime()
    {
        return m_startTime;
    }
    public Date getEndTime()
    {
        return m_endTime;
    }
    public long getStartMinute()
    {
        return m_startMinute;
    }
    public long getEndMinute()
    {
        return m_endMinute;
    }
    public int getMinutes()
    {
        return (int) (m_endMinute - m_startMinute);
    }
    public boolean isBetweenSearchDates(Date dt)
    {
        return (dt.after(m_startTime) 
            && dt.before(m_endTime))
            || (dt.getTime() - m_startTime.getTime() > (-1000 * 60)
            && dt.getTime() - m_endTime.getTime() < (1000 * 60));
    }
    public String getFromattedSearchDates()
    {
        StringBuilder sb = new StringBuilder();
        return sb.append(" Start=").append(s_formatter.format(m_startTime))
                 .append(" End=").append(s_formatter.format(m_endTime))
                 .toString();
    }
    
    public String getOutputFilepath()
    {
        return m_outfilepath;
    }
    public boolean hasOutputFilepath()
    {
        return (m_outfilepath != null);
    }
}
