package org.example.time;

public class ResultReflection {
        private final double averageTimeMs;
        private final double setupTimeMs;

        ResultReflection(double averageTimeMs, double setupTimeMs) {
            this.averageTimeMs = averageTimeMs;
            this.setupTimeMs = setupTimeMs;
        }

    public double getAverageTimeMs() {
        return averageTimeMs;
    }

    public double getSetupTimeMs() {
        return setupTimeMs;
    }
}
