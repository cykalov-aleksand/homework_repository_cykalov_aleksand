package org.example.dataprocessor;

import org.example.dataprocessor.processor.ProcessorAggregator;
import org.example.model.Measurement;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ProcessorAggregatorTest {
    private static final Logger logger= LoggerFactory.getLogger(ProcessorAggregatorTest.class);
    private final ProcessorAggregator processor = new ProcessorAggregator();

        @Test
        void shouldReturnEmptyMapWhenDataIsEmpty() {
            logger.info("Тест на проверку метода при пустых входных данных");
            List<Measurement> data = List.of();
            Map<String, Double> result = processor.process(data);
            assertThat(result).isEmpty();
        }

        @Test
        void shouldAggregateValuesByName() {
            logger.info("Тест на проверку работы метода и возврата значений упорядоченных по отношению к входным данным");
                    List<Measurement> data = List.of(
                    new Measurement("temp", 10.0),
                    new Measurement("pressure", 750.0),
                    new Measurement("temp", 20.0),
                    new Measurement("humidity", 60.0),
                    new Measurement("pressure", 50.0)
            );
Map<String, Double> result = processor.process(data);
            assertThat(result)
                    .containsExactly(
                            entry("temp", 30.0),        // 10 + 20
                            entry("pressure", 800.0),   // 750 + 50
                            entry("humidity", 60.0)     // 60
                    );
        }
    }
