package example.lib;



import example.api.model.SensorData;

import java.util.List;

public interface SensorDataBufferedWriter {
    void writeBufferedData(List<SensorData> bufferedData);
}
