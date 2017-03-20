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
    
    private static String METRICS_KEY = "tt.metrics.";
    private static String METRICS_START = "starttime";
    private static String METRICS_END = "endtime";
    private static String METRICS_OUTPUTFILE = "outputfile";
    private static String METRICS_FILES = "tt.metrics.files";
    private static String METRICS_FILE_KEY = "tt.metrics.file.";
    private static String METRICS_FILE_PATH = "path";
    private static String METRICS_FILE_TYPE = "type";
    private static String METRICS_FILE_FIELDS = "fields";
    private static String METRICS_TYPE_SINGLE = "single";
    private static String METRICS_TYPE_SUMMARY = "summary";
    private static String METRICS_FIELD_SUMMARY = "summary";
    private static String METRICS_SUMMARY_COUNT = "count";
    private static String METRICS_SUMMARY_SUM = "sum";
    private static String METRICS_SUMMARY_MEAN = "mean";
    private static String METRICS_SUMMARY_STDDEV = "stddev";
    private static String METRICS_SUMMARY_VARIANCE = "variance";
    
    private String m_filepath;
    private Properties m_props;
    private DataPoints m_dataPoints;
    private Date m_startTime;
    private long m_startMinute = 0;
    private long m_endMinute = 0;
    private Date m_endTime;
    private String m_outfilepath = null;
    
    private static String s_expectedPattern = "yyyy-MM-dd hh:mm:ss";
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
    
    public ArrayList<MetricsFile> getMetricsFileConfig() {
        
        ArrayList<MetricsFile> files = new ArrayList<MetricsFile>();
        ArrayList<String> filekeys = getPropertyValues(METRICS_FILES);
        for (String filekey : filekeys) {
            MetricsFile config = getMetricsFileConfig(filekey);
            files.add(config);
        }
        return files;
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
    
    private MetricsFile getMetricsFileConfig(String filekey) {
        String path = getProperty(METRICS_FILE_KEY + filekey + "." + METRICS_FILE_PATH);
        String type = getProperty(METRICS_FILE_KEY + filekey + "." + METRICS_FILE_TYPE);
        MetricsFile.MetricType mtype = getMetricsFileType(type);
        MetricsFile mFile = new MetricsFile(path, filekey, mtype);
        String fields = getProperty(METRICS_FILE_KEY + filekey + "." + METRICS_FILE_FIELDS);
        String fields_array[] = fields.split("\\|");
        for (String field : fields_array) 
        {
            m_dataPoints.addField(filekey, field);
            if (mtype == MetricsFile.MetricType.SINGLE) {
                mFile.addField(field, filekey, m_dataPoints);
            } else {
                String summarytype = getProperty(METRICS_FILE_KEY + filekey + "." + field + "." + METRICS_FIELD_SUMMARY);
                MetricsFile.SummaryType stype = getMetricsFieldSummaryType(summarytype);
                mFile.addField(field, filekey, m_dataPoints, stype);
            }
        }
        return mFile;
    }
    
    private MetricsFile.MetricType getMetricsFileType(String type) {
        MetricsFile.MetricType mtype = MetricsFile.MetricType.SINGLE;
        if (type.equalsIgnoreCase(METRICS_TYPE_SINGLE)) {
            mtype = MetricsFile.MetricType.SINGLE;
        }
        if (type.equalsIgnoreCase(METRICS_TYPE_SUMMARY)) {
            mtype = MetricsFile.MetricType.SUMMARY;
        }
        return mtype;
    }
    
    private MetricsFile.SummaryType getMetricsFieldSummaryType(String type) {
        MetricsFile.SummaryType stype = MetricsFile.SummaryType.COUNT;
        if (type.equalsIgnoreCase(METRICS_SUMMARY_COUNT)) {
            stype = MetricsFile.SummaryType.COUNT;
        }
        if (type.equalsIgnoreCase(METRICS_SUMMARY_SUM)) {
            stype = MetricsFile.SummaryType.SUM;
        }
        if (type.equalsIgnoreCase(METRICS_SUMMARY_MEAN)) {
            stype = MetricsFile.SummaryType.MEAN;
        }
        if (type.equalsIgnoreCase(METRICS_SUMMARY_STDDEV)) {
            stype = MetricsFile.SummaryType.STDDEV;
        }
        if (type.equalsIgnoreCase(METRICS_SUMMARY_VARIANCE)) {
            stype = MetricsFile.SummaryType.VARIANCE;
        }
        return stype;
    }
    
    private void setStartAndEndDates()
    {
        String start = getProperty(METRICS_KEY + METRICS_START);
        String end = getProperty(METRICS_KEY + METRICS_END);
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
        String outfile = getProperty(METRICS_KEY + METRICS_OUTPUTFILE);
        if (outfile != null && outfile.trim().length() > 0)
        {
            m_outfilepath = outfile;
        }
    }
    
    public Date getStartTime()
    {
        return m_startTime;
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
    
    public String getOutputFilepath()
    {
        return m_outfilepath;
    }
    public boolean hasOutputFilepath()
    {
        return (m_outfilepath != null);
    }
}
