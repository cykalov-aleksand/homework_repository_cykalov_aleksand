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
    private final List<SensorData> dataBuffer = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        //синхронизируем запись с помощью ReentrantLock
        lock.lock();
        try {
            //заносим данные в список
            dataBuffer.add(data);
            // если буфер заполнился до порога — выполняем сброс через flush().
            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
            // гарантированно освобождаем лок при любом выходе из блока:
            // и при нормальном завершении, и при исключении
        } finally {
            lock.unlock();
        }
    }

    public void flush() {

        // Захватываем лок: получаем единоличный доступ к буферу
        lock.lock();
        try {
            //производим сброс информации в список
            flushLocked();
        } finally {
            // гарантированно освобождаем лок при любом выходе из блока:
            // и при нормальном завершении, и при исключении
            lock.unlock();
        }
    }

    private void flushLocked() {
        //если список пуст выходим из метода
        if (dataBuffer.isEmpty()) {
            return;
        }
        //создаем обычный список для записи для сброса входных данных по переполнению
        //достаточно обычного списка по причине того что обычный ArrayList, потому что под локом параллельные изменения буфера исключены
        List<SensorData> toWrite = new ArrayList<>(dataBuffer);
        //заносим данные и производим сортировку по времени изменения
        toWrite.sort(Comparator.comparing(SensorData::getMeasurementTime));
        // Очищаем буфер, пока держим лок, — чтобы второй поток не забрал те же данные
        dataBuffer.clear();
        try {
            // Записываем пачку данных через writer
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