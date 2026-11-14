package org.example.api;

import org.example.api.model.SensorData;

public interface SensorsDataServer {
    void onReceive(SensorData sensorData);
}
