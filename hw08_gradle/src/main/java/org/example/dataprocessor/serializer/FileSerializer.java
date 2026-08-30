package org.example.dataprocessor.serializer;

import org.example.dataprocessor.FileProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class FileSerializer implements Serializer {
    private static final Logger logger=LoggerFactory.getLogger(FileSerializer.class);
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
            String msg="Не удалось создать директорию: " + parentDir.getAbsolutePath();
            logger.error(msg);
            throw new FileProcessException(msg);
        }

        try (OutputStream os = new FileOutputStream(file)) {
            mapper.writeValue(os, data);
        } catch (IOException e) {
            String msg="Ошибка при записи JSON в файл: " + fileName;
            logger.error(msg,e);
            throw new FileProcessException(msg, e);
        }
    }
}