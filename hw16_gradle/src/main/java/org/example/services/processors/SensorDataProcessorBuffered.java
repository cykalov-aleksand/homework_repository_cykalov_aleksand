package org.example.services.processors;

import org.example.api.SensorDataProcessor;
import org.example.api.model.SensorData;
import org.example.lib.SensorDataBufferedWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;

    private final Queue<SensorData> dataBuffer = new ConcurrentLinkedQueue<>();
    private final AtomicInteger size = new AtomicInteger();
    private final AtomicBoolean draining = new AtomicBoolean();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        dataBuffer.add(data);
            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
           }

    public void flush() {
        // Захватываем право на сброс: только один поток за раз может писать.
        if (!draining.compareAndSet(false, true)) {
            return; // другой поток уже сбрасывает — его работы достаточно
        }
        try {
            if (dataBuffer.isEmpty()) {
                return; // писать пустоту нельзя: тест ожидает ровно одну запись
            }
            List<SensorData> toWrite = new ArrayList<>();
            SensorData item;
            while ((item = dataBuffer.poll()) != null) {
                toWrite.add(item);
            }
            size.addAndGet(-toWrite.size());
            toWrite.sort(Comparator.comparing(SensorData::getMeasurementTime));
            try {
                writer.writeBufferedData(toWrite);
            } catch (Exception e) {
                log.error("Ошибка в процессе записи буфера", e);
            }
        } finally {
            draining.set(false);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
