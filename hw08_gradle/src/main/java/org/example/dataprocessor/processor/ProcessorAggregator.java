package org.example.dataprocessor.processor;

import org.example.model.Measurement;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProcessorAggregator implements Processor {



    @Override
    public Map<String, Double> process(List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value
        Map<String,Double>result=new LinkedHashMap<>();
        if(data.isEmpty()) {
            return Collections.emptyMap();
        }
        for(Measurement variable:data){
            result.merge(variable.name(), variable.value(), Double::sum);
        }
        return result;
    }
}
