package org.example.lib;

import org.example.api.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SensorDataBufferedWriterFake implements SensorDataBufferedWriter {
    private static final Logger log = LoggerFactory.getLogger(SensorDataBufferedWriterFake.class);

    @Override
    public void writeBufferedData(List<SensorData> bufferedData) {
        var dataToWrite = bufferedData.stream().map(SensorData::toString).collect(Collectors.joining("\n"));
        log.info("Как будто куда-то записываем пачку данных: \n{}", dataToWrite);
    }
}
