/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tt.metrics;

/**
 *
 * @author Udai
 */
public class DataPoint {
    
    private String m_filekey;
    private String m_field;
    private int m_index;
    private MetricsSource.SummaryType m_stype;
    
    DataPoint(String filekey, String field, int index, MetricsSource.SummaryType stype) {
        this.m_filekey = filekey;
        this.m_field = field;
        this.m_index = index;
        this.m_stype = stype;
    }
    
    int getIndex() {
        return this.m_index;
    }
    
    String getField() {
        return this.m_field;
    }
    
    String getFileKey() {
        return this.m_filekey;
    }
    
    MetricsSource.SummaryType getSummaryType() {
        return this.m_stype;
    }
    
    String getCombinedLabel() {
        return this.m_filekey + "-" + this.m_field;
    }
    
    @Override
    public boolean equals(Object rhs)
    {
        return this.m_index == ((DataPoint) rhs).getIndex();
    }
    
    @Override
    public int hashCode()
    {
        return this.m_index;
    }
}
