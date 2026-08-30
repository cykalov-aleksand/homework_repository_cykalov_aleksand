package org.example.dataprocessor.serializer;

import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final JsonMapper mapper;
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
        this.mapper=JsonMapper.builder().build();
    }

    @Override
        public void serialize(Map<String, Double> data) {
            // формирует результирующий json и сохраняет его в файл
        File file = new File(fileName);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new RuntimeException("Не удалось создать директорию: " + parentDir.getAbsolutePath());
        }

        try (OutputStream os = new FileOutputStream(file)) {
            mapper.writeValue(os, data);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи JSON в файл: " + fileName, e);
        }
    }
}