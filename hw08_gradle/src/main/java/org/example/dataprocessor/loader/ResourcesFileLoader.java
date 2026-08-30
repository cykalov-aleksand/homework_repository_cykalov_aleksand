package org.example.dataprocessor.loader;

import org.example.dataprocessor.FileProcessException;
import org.example.model.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class ResourcesFileLoader implements Loader {
    private static final Logger logger = LoggerFactory.getLogger(ResourcesFileLoader.class);
    private final String fileName;
    private final JsonMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.mapper = JsonMapper.builder().build();
    }

    @Override
    public List<Measurement> load() {
        logger.info("Загрузка данных из файла: {}", fileName);
        logger.debug("Ищу файл: {}", fileName);
        logger.debug("Путь: {}", getClass().getClassLoader().getResource(fileName));

        try (InputStream file = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (file == null) {
                String msg = "Файл не найден в resources: " + fileName;
                logger.error("Файл не найден в resources: {}", fileName);
                throw new FileProcessException(msg);
            }

            List<Measurement> result = mapper.readValue(file, new TypeReference<>() {
            });
            return result != null ? result : Collections.emptyList();

        } catch (FileProcessException e) {
            throw e;
        } catch (JacksonException e) {
            String msg = "Ошибка при чтении JSON из файла: " + fileName;
            logger.error(msg, e);
            throw new FileProcessException(msg, e);
        } catch (Exception e) {
            logger.error("Неизвестная ошибка при загрузке файла: {}", fileName, e);
            throw new FileProcessException("Неизвестная ошибка при загрузке: " + fileName, e);
        }
    }
}