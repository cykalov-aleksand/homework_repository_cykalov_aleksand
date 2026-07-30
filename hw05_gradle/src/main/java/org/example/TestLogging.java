package org.example;

import org.example.anatations.Log;

public class TestLogging implements TestLoggingInterface{
    @Override
    @Log
    public void calculation(int param) {
        System.out.println(param);
    }
}
