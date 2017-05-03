package com.tt.metrics;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Target class to represent Graphite data
 * @author Udai
 */
public class Graphite {

@SerializedName("target")
@Expose
private String target;
@SerializedName("datapoints")
@Expose
private List<List<Double>> datapoints = null;

public String getTarget() {
return target;
}

public void setTarget(String target) {
this.target = target;
}

public List<List<Double>> getDatapoints() {
return datapoints;
}

public void setDatapoints(List<List<Double>> datapoints) {
this.datapoints = datapoints;
}

}