package org.example;

import org.example.anatations.Log;

public interface TestLoggingInterface {
    @Log
    public void calculation(int param);
}
