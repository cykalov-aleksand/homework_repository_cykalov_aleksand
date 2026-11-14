package example.services.processors;


import example.api.SensorDataProcessor;
import example.api.model.SensorData;
import example.lib.SensorDataBufferedWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final ArrayList<SensorData> dataBuffer = new ArrayList<>();
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
                flush(); // лок реентерабельный: второй захват уже удержанного лока пройдёт без ожидания
            }
        } finally {
            lock.unlock();
        }
    }

    public void flush() {
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
