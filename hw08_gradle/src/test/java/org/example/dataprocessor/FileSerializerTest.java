package org.example.dataprocessor;

import org.example.dataprocessor.serializer.FileSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class FileSerializerTest {
    private static final Logger logger= LoggerFactory.getLogger(FileSerializerTest.class);
       private FileSerializer serializer;
        private final Map<String, Double> testData = Map.of("temp", 23.5, "pressure", 760.0);

        @TempDir
        Path tempDir;

        @BeforeEach
        void setUp() {
            logger.info("Тестовый каталог: {}", tempDir);
        }

        @Test
        void shouldSerializeDataToFile() throws IOException {
            logger.info("Тест проверки успешной записи данных");
           File outputFile = tempDir.resolve("output.json").toFile();
            serializer = new FileSerializer(outputFile.getAbsolutePath());
            serializer.serialize(testData);
            assertThat(outputFile).exists().isFile();
            Map<String, Double> loaded = new JsonMapper().readValue(outputFile, new TypeReference<Map<String, Double>>() {});
            assertThat(loaded).isEqualTo(testData);
        }

         @Test
        void shouldThrowExceptionOnWriteError() throws IOException {
            logger.info("Тест на проверку выбрасывания исключения при ошибке записи");
            File outputFile = tempDir.resolve("locked.json").toFile();
            // Создаём файл и делаем его только для чтения
            outputFile.createNewFile();
            outputFile.setReadOnly(); // эмулируем ошибку записи
            serializer = new FileSerializer(outputFile.getAbsolutePath());
            assertThatThrownBy(() -> serializer.serialize(testData))
                    .isInstanceOf(FileProcessException.class)
                    .hasMessageContaining("Ошибка при записи JSON в файл")
                    .hasCauseInstanceOf(IOException.class);
        }
    }

