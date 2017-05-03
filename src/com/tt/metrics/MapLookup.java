package com.tt.metrics;

/**
 * An interface for Lookup Functions
 * @author Udai
 */
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface MapLookup {
    <V> List<V> lookup(String regularExpression, Map<String,V> map);
}
