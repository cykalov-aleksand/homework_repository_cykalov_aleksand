package org.example.services.processors;

import org.example.api.SensorDataProcessor;
import org.example.api.model.SensorData;
import org.example.lib.SensorDataBufferedWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final CopyOnWriteArrayList<SensorData> dataBuffer = new CopyOnWriteArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        lock.lock();
        try {
            dataBuffer.add(data);
            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
        } finally {
            lock.unlock();
        }
    }

    public void flush() {
        if (dataBuffer.isEmpty()) {
            return;
        }
        lock.lock();
        try {
            flushLocked();
        } finally {
            lock.unlock();
        }
    }

    private void flushLocked() {
        if (dataBuffer.isEmpty()) {
            return;
        }
        List<SensorData> toWrite = new ArrayList<>(dataBuffer);
        toWrite.sort(Comparator.comparing(SensorData::getMeasurementTime));
        dataBuffer.clear(); // очищаем под локом, чтобы второй поток не забрал те же данные
        try {
            writer.writeBufferedData(toWrite);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}