package example.api;



public interface SensorDataProcessingFlow {
    void startProcessing();

    void stopProcessing();

    void bindProcessor(String roomPattern, SensorDataProcessor processor);
}
