package example.api;


import example.api.model.SensorData;

public interface SensorsDataServer {
    void onReceive(SensorData sensorData);
}
