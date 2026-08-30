package org.example.dataprocessor.processor;

import org.example.model.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProcessorAggregator implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorAggregator.class);

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        if (data == null || data.isEmpty()) {
            logger.info("Список измерений пуст или null. Возвращён пустой результат.");
            return Collections.emptyMap();
        }

        Map<String, Double> result = new LinkedHashMap<>();

        for (int i = 0; i < data.size(); i++) {
            Measurement measurement = data.get(i);

            if (measurement == null) {
                logger.info("Элемент с индексом {} в списке data равен null. Пропущен.", i);
                continue;
            }

            String name = measurement.name();

            if (name == null) {
                logger.info("Элемент [{}] пропущен: поле name равно null.", measurement.getClass().getSimpleName());
            } else {
                result.merge(name, measurement.value(), Double::sum);
            }
        }

        return result;
    }
}