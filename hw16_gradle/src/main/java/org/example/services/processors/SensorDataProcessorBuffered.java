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
        //заносим данные в очередь
        dataBuffer.add(data);
        // Атомарно увеличиваем счётчик и проверяем порог:
        // если буфер заполнился — запускаем сброс.
            if (size.incrementAndGet() >= bufferSize) {
                flush();
            }
           }

    public void flush() {
        // проверяем если атомарная переменная находится в значение false меняем ее на true
        // и если другой поток успел уже изменить значение на true метод вернёт false

        if (!draining.compareAndSet(false, true)) {
            return;
        }
        try {
            //проверяем наличие информации в очереди, если очередь пуста выходим из метода
            if (dataBuffer.isEmpty()) {
                return; // писать пустоту нельзя: тест ожидает ровно одну запись
            }
            // Под CAS-флагом очередь дренажит только этот поток,
            // поэтому для переноса достаточно обычного ArrayList.
            List<SensorData> toWrite = new ArrayList<>();
            SensorData item;
            // Атомарно вынимаем элементы по одному (poll), пока очередь не опустеет,
            // и переносим их в список
            while ((item = dataBuffer.poll()) != null) {
                toWrite.add(item);
            }
            // Уменьшаем счётчик на число фактически сброшенных элементов.
            size.addAndGet(-toWrite.size());
            //организуем сортировку полученного списка по времени
            toWrite.sort(Comparator.comparing(SensorData::getMeasurementTime));
            try {
                writer.writeBufferedData(toWrite);
            } catch (Exception e) {
                log.error("Ошибка в процессе записи буфера", e);
            }
            //независимо от того возникло ли исключение или нет, устанавливаем атомарной переменной draining значение false
           } finally {
            draining.set(false);
        }
    }

    @Override
    public void onProcessingEnd() {
        // По завершении обработки сбрасываем всё, что осталось в буфере.
        flush();
    }
}
