package org.example;
import org.example.dataprocessor.processor.ProcessorAggregator;
import org.example.dataprocessor.serializer.FileSerializer;
import org.example.model.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
           // Пути к файлам
            Path dataDir = Paths.get("hw08_gradle/data");
            Path inputPath = dataDir.resolve("input-test.json");
            Path outputPath = dataDir.resolve("output-test.json");
            Files.createDirectories(dataDir);
            JsonMapper writerMapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT)
                    .build();
            writerMapper.writeValue(inputPath.toFile(), createTestMeasurements());
            logger.info("Исходные данные записаны: {}", inputPath.toAbsolutePath());
            JsonMapper readerMapper = JsonMapper.builder().build();
            List<Measurement> loaded = readerMapper.readValue(
                    inputPath.toFile(),
                    readerMapper.getTypeFactory().constructCollectionType(List.class, Measurement.class)
            );
            ProcessorAggregator processor = new ProcessorAggregator();
            Map<String, Double> aggregated = processor.process(loaded);
            FileSerializer serializer = new FileSerializer(outputPath.toAbsolutePath().toString());
            serializer.serialize(aggregated);
            logger.info("Результат агрегации: {}", aggregated);
            logger.info("Результат записан: {}", outputPath.toAbsolutePath());

        } catch (IOException e) {
            logger.error("Ошибка при работе с файлами: {}", e.getMessage());
        }
    }
    private static List<Measurement> createTestMeasurements() {
        return List.of(
                new Measurement("val1", 10.0),
                new Measurement("val1", 22.0),
                new Measurement("val2", 12.0),
                new Measurement("val2", 22.0),
                new Measurement("val3", 14.0),
                new Measurement("val3", 14.0),
                new Measurement("val3", 113.0),
                new Measurement("val1", 22.0),
                new Measurement("val2", 0.0)
        );
    }
}