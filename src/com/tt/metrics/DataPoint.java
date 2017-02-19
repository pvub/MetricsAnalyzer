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
public enum DataPoint {
    
    PROCESS_CPU {
        @Override
        public int getValue() {
            return 0;
        }
    },
    PROCESS_VM {
        @Override
        public int getValue() {
            return 1;
        }
    },
    CONN_TOTAL {
        @Override
        public int getValue() {
            return 2;
        }
    },
    CONN_OUT_BYTES_PEEK {
        @Override
        public int getValue() {
            return 3;
        }
    },
    CONN_OUT_BYTES_AVG {
        @Override
        public int getValue() {
            return 4;
        }
    },
    CONN_OUT_PEEK {
        @Override
        public int getValue() {
            return 5;
        }
    },
    CONN_OUT_AVG {
        @Override
        public int getValue() {
            return 6;
        }
    },
    USER_ID {
        @Override
        public int getValue() {
            return 7;
        }
    },
    RT_LAT_AVG {
        @Override
        public int getValue() {
            return 8;
        }
    },
    INSTR_ID {
        @Override
        public int getValue() {
            return 9;
        }
    },
    IN_MD {
        @Override
        public int getValue() {
            return 10;
        }
    },
    IN_TD {
        @Override
        public int getValue() {
            return 11;
        }
    },
    OUT {
        @Override
        public int getValue() {
            return 12;
        }
    },
    AVG_LAT {
        @Override
        public int getValue() {
            return 13;
        }
    },
    OUT_LAT_PEEK {
        @Override
        public int getValue() {
            return 14;
        }
    },
    OUT_LAT_AVG {
        @Override
        public int getValue() {
            return 15;
        }
    },
    QD_PEEK {
        @Override
        public int getValue() {
            return 16;
        }
    },
    QD_AVG {
        @Override
        public int getValue() {
            return 17;
        }
    },
    MAX {
        @Override
        public int getValue() {
            return 18;
        }
    };
    
    public abstract int getValue();
    
    public static DataPoint getDataPoint(String label)
    {
        if (label.trim().compareToIgnoreCase("cpu") == 0) {
            return DataPoint.PROCESS_CPU;
        }
        if (label.trim().compareToIgnoreCase("vm") == 0) {
            return DataPoint.PROCESS_VM;
        }
        if (label.trim().compareToIgnoreCase("con_total") == 0) {
            return DataPoint.CONN_TOTAL;
        }
        if (label.trim().compareToIgnoreCase("out_bytes_peek") == 0) {
            return DataPoint.CONN_OUT_BYTES_PEEK;
        }
        if (label.trim().compareToIgnoreCase("out_bytes_avg") == 0) {
            return DataPoint.CONN_OUT_BYTES_AVG;
        }
        if (label.trim().compareToIgnoreCase("out_peek") == 0) {
            return DataPoint.CONN_OUT_PEEK;
        }
        if (label.trim().compareToIgnoreCase("out_avg") == 0) {
            return DataPoint.CONN_OUT_AVG;
        }
        if (label.trim().compareToIgnoreCase("user_id") == 0) {
            return DataPoint.USER_ID;
        }
        if (label.trim().compareToIgnoreCase("rt_lat_avg") == 0) {
            return DataPoint.RT_LAT_AVG;
        }
        if (label.trim().compareToIgnoreCase("instr_id") == 0) {
            return DataPoint.INSTR_ID;
        }
        if (label.trim().compareToIgnoreCase("in_md") == 0) {
            return DataPoint.IN_MD;
        }
        if (label.trim().compareToIgnoreCase("in_td") == 0) {
            return DataPoint.IN_TD;
        }
        if (label.trim().compareToIgnoreCase("out") == 0) {
            return DataPoint.OUT;
        }
        if (label.trim().compareToIgnoreCase("avg_lat") == 0) {
            return DataPoint.AVG_LAT;
        }
        if (label.trim().compareToIgnoreCase("out_lat_peek") == 0) {
            return DataPoint.OUT_LAT_PEEK;
        }
        if (label.trim().compareToIgnoreCase("out_lat_avg") == 0) {
            return DataPoint.OUT_LAT_AVG;
        }
        if (label.trim().compareToIgnoreCase("qd_peek") == 0) {
            return DataPoint.QD_PEEK;
        }
        if (label.trim().compareToIgnoreCase("qd_avg") == 0) {
            return DataPoint.QD_AVG;
        }
        return DataPoint.MAX;
    }
}
