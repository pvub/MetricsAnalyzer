package com.tt.metrics;

/**
 * Ability to Match 'Pattern' Keys with Actual Strings
 * @author Udai
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexKeyLookup implements MapLookup 
{
    @Override
    public <V> List<V> lookup(String keyvalue, Map<String, V> map) 
    {
        List<String> keys  = map.keySet()
                .stream()
                .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String keypattern) {
                                Pattern pattern = Pattern.compile(keypattern);
                                return pattern.matcher(keyvalue).matches();
                            }
                        })
                .collect(Collectors.toList());
        if(keys != null && !keys.isEmpty())
        {
            return keys.stream().map((key) -> map.get(key)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

