package com.tt.metrics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author Udai
 */
public class DataFrame {
    private TimeUnit   m_unit;
    private double[][] m_points;
    private Date       m_startdate;
    private static NumberFormat df = new DecimalFormat("0.00");
    
    public DataFrame(MetricsConfig config, TimeUnit unit) 
            throws Exception
    {
        this.m_unit = unit;
        this.m_points = new double[config.getTimeUnits(unit)][DataPoints.getMax()];
        this.m_startdate = config.getStartTime();
    }
    
    // Create a DataFrame from another with just two columns
    public DataFrame(DataFrame frame, int col1, int col2)
    {
        this.m_startdate = frame.m_startdate;
        this.m_points = new double[frame.m_points.length][2];
        if (col1 > frame.m_points[0].length || col2 > frame.m_points[0].length)
        {
            System.out.println("Unabel to create DataFrame. Invalid Column Indices");
            return;
        }
        int rowIndex = 0;
        while (rowIndex < frame.m_points.length)
        {
            this.m_points[rowIndex][col1] = frame.m_points[rowIndex][col1];
            this.m_points[rowIndex][col2] = frame.m_points[rowIndex][col2];
            ++rowIndex;
        }
    }
        
    public void addRowDetail(RowContainer container) 
    {
        int valindex = 0;
        for (int column : container.indices) {
            m_points[container.rowindex][column] = container.values[valindex];
            ++valindex;
        }
    }
    
    public ArrayList<String> dumpCorrelation(ArrayList<String> linelist)
    {
        Array2DRowRealMatrix mtx = new Array2DRowRealMatrix(m_points);
        int rows = mtx.getRowDimension();
        int cols = mtx.getColumnDimension();
        PearsonsCorrelation corr = new PearsonsCorrelation();
        RealMatrix matrix = corr.computeCorrelationMatrix(m_points);
        dumpMatrixAsList(linelist, matrix);
        return linelist;
    }
    
    public void dumpAsList(OutputProcessor processor)
    {
        StringBuilder sb = new StringBuilder();
        int cols = m_points[0].length;
        int rows = m_points.length;
        int rowIndex = 0;
        Date dt = this.m_startdate;
        while (rowIndex < rows)
        {
            sb.append(DateHelper.formatCSV(m_unit,dt)).append(",");
            int colIndex = 0;
            while (colIndex < cols)
            {
                sb.append(df.format(m_points[rowIndex][colIndex])).append(",");
                ++colIndex;
            }
            processor.processLine(sb.toString());
            sb = new StringBuilder();
            ++rowIndex;
            dt = DateHelper.next(m_unit, dt);
        }
    }
    
    public ArrayList<String> dumpMatrixAsList(ArrayList<String> linelist, RealMatrix matrix)
    {
        StringBuilder sb = new StringBuilder();
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        int rowIndex = 0;
        while (rowIndex < rows)
        {
            int colIndex = 0;
            while (colIndex < cols)
            {
                sb.append(matrix.getEntry(rowIndex, colIndex)).append(",");
                ++colIndex;
            }
            linelist.add(sb.toString());
            sb = new StringBuilder();
            ++rowIndex;
        }
        return linelist;
        
    }
    
    public void performSimpleRegression(int col1, int col2, double predict)
    {
        if (col1 > this.m_points[0].length || col2 > this.m_points[0].length)
        {
            System.out.println("Unabel to perform Regression. Invalid Column Indices");
            return;
        }
        double[][] regframe = new double[this.m_points.length][2];
        int rowIndex = 0;
        while (rowIndex < this.m_points.length)
        {
            regframe[rowIndex][0] = this.m_points[rowIndex][col1];
            regframe[rowIndex][1] = this.m_points[rowIndex][col2];
            ++rowIndex;
        }
        SimpleRegression r = new SimpleRegression();
        r.addData(regframe);
        System.out.println("Intercept: " + r.getIntercept());
        System.out.println("Slope: " + r.getSlope());
        System.out.println("R-Square: " + r.getRSquare());
        if (predict != 0.0d)
        {
            System.out.println("Prediction for val=" + predict + " is " + r.predict(predict));
        }
    }
}
