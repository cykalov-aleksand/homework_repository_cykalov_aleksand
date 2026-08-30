package org.example.dataprocessor.loader;

import org.example.dataprocessor.FileProcessException;
import org.example.model.Measurement;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class ResourcesFileLoader implements Loader {
private final String fileName;
private final JsonMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.mapper=JsonMapper.builder().build();
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        //  File file=new File(fileName);
        System.out.println("Ищу файл: " + fileName);
        System.out.println("Путь: " + getClass().getClassLoader().getResource(fileName));
        // if(file.exists()||!file.isFile()){
        //     throw new RuntimeException("Файл не найден: "+fileName);
        //             }
        try (InputStream file = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (file == null) {
                throw new FileProcessException("Файл не найден в resources: " + fileName);
            }
            List<Measurement> result = mapper.readValue(file, new TypeReference<>() {
            });
            return result != null ? result : Collections.emptyList();
        } catch (IOException e) {
            throw new FileProcessException("Ошибка при чтении JSON из файла " + fileName, e);
        }
    }}
