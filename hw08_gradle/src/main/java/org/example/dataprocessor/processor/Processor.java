package org.example.dataprocessor.processor;

import org.example.model.Measurement;

import java.util.List;
import java.util.Map;

public interface Processor {

    Map<String, Double> process(List<Measurement> data);

}
