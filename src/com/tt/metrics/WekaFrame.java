package com.tt.metrics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/**
 * WEKA Instances
 * @author Udai
 */
public class WekaFrame {
    
    private ArrayList<Attribute> m_attributes = new ArrayList<Attribute>();
    private Instances            m_dataset;
    private TimeUnit             m_unit;
    private Date                 m_startdate;
    private SimpleDateFormat     m_dateformatter;
    private String               m_arffFileName;
    private String               m_csvFileName;
    
    public WekaFrame(MetricsConfig config, TimeUnit unit)
    {
        this.m_unit = unit;
        this.m_startdate = config.getStartTime();
        addAttributes(config, unit);
        m_dataset = new Instances("weka_"+unit.name(), m_attributes, 0);
        m_dateformatter = new SimpleDateFormat(DateHelper.getWekaDatePattern(unit));
        String outputfilepath = config.getOutputFilepath();
        int i = outputfilepath.contains(".") ? outputfilepath.lastIndexOf('.') : outputfilepath.length();
        m_arffFileName = outputfilepath.substring(0, i) + "_" + m_unit.name() + ".arff";
        m_csvFileName = outputfilepath.substring(0, i) + "_" + m_unit.name() + ".csv";
    }
    
    public void addRow(RowContainer container)
    {
        double[] values = new double[m_dataset.numAttributes()]; // One extra for Date
        values = addDateTimeValues(values, container);
        int valindex = 0;
        for (int column : container.indices) 
        {
            // Shift by 1 to accomodate the initial time field
            values[column + 5] = container.values[valindex];
            ++valindex;
        }
        m_dataset.add(new DenseInstance(1.0, values));

    }

    private double[] addDateTimeValues(double[] values, RowContainer container)
    {
        Date dt = DateHelper.next(m_unit, m_startdate, container.rowindex);
        values[0] = dt.getTime(); // time
        values[1] = m_dataset.attribute(1).indexOfValue(DateHelper.getHourOfDayLabel(dt)); // hour
        values[2] = m_dataset.attribute(2).indexOfValue(DateHelper.getDayOfWeekLabel(dt)); // day
        values[3] = m_dataset.attribute(3).indexOfValue(DateHelper.getWeekOfYearLabel(dt)); // week
        values[4] = m_dataset.attribute(4).indexOfValue(DateHelper.getMonthOfYearLabel(dt));  // month      
        return values;
    }
    
    public void save()
    {
        try {
            DataSink.write(m_arffFileName, m_dataset);
            DataSink.write(m_csvFileName, m_dataset);
        } catch (Exception ex) {
            Logger.getLogger(WekaFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addAttributes(MetricsConfig config, TimeUnit unit)
    {
        addDateTimeAttributes(config, unit);
        // Now add datapoints
        DataPoints dps = config.getDataPoints();
        HashSet<DataPoint> points = new HashSet<DataPoint>();
        dps.fillDataPoints(points);
        for (DataPoint point : points)
        {
            addAttribute(point);
        }
    }
    
    private void addDateTimeAttributes(MetricsConfig config, TimeUnit unit)
    {
        // Add Time attribute first
        Attribute time_attribute = new Attribute("time", DateHelper.getWekaDatePattern(unit));
        m_attributes.add(time_attribute);
        // Add HOUR nominal attribute
        ArrayList<String> hour_labels = 
                new ArrayList<>(new ArrayList<>(
                        Arrays.asList("00","01","02","03","04","05",
                                    "06","07","08","09","10",
                                    "11","12","13","14","15",
                                    "16","17","18","19","20",
                                    "21","22","23"))); 
        Attribute hours_nominal = new Attribute("hour", hour_labels);
        m_attributes.add(hours_nominal);
        // Add DAY nominal attribute
        ArrayList<String> day_labels = 
                new ArrayList<>(new ArrayList<>(
                        Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun"))); 
        Attribute days_nominal = new Attribute("day", day_labels);
        m_attributes.add(days_nominal);
        // Add WEEK nominal attribute
        ArrayList<String> week_labels = 
                new ArrayList<>(new ArrayList<>(
                        Arrays.asList("1","2","3","4","5","6","7","8","9","10",
                                      "11","12","13","14","15","16","17","18","19","20",
                                      "21","22","23","24","25","26","27","28","29","30",
                                      "31","32","33","34","35","36","37","38","39","40",
                                      "41","42","43","44","45","46","47","48","49","50",
                                      "51","52"))); 
        Attribute week_nominal = new Attribute("week", week_labels);
        m_attributes.add(week_nominal);
        // Add MONTH nominal attribute
        ArrayList<String> month_labels = 
                new ArrayList<>(new ArrayList<>(
                        Arrays.asList("Jan","Feb","Mar","Apr","May","Jun",
                                    "Jul","Aug","Sep","Oct","Nov","Dec"))); 
        Attribute month_nominal = new Attribute("month", month_labels);
        m_attributes.add(month_nominal);
        
    }
    
    private void addAttribute(DataPoint point)
    {
        Attribute att = new Attribute(point.getField());
        m_attributes.add(att);
    }
}
