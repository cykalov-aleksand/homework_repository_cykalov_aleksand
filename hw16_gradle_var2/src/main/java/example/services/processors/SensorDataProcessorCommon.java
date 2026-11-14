package example.services.processors;


import example.api.SensorDataProcessor;
import example.api.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorDataProcessorCommon implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorCommon.class);

    @Override
    public void process(SensorData data) {
        if (data.getValue() == null || data.getValue().isNaN()) {
            return;
        }
        log.info("Обработка данных: {}", data);
    }
}
