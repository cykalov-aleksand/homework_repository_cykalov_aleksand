package org.example.application;


import org.example.annotations.Log;

public class Demo {
    private final TestLogging testLogging;


    public Demo() {
            this.testLogging = new TestLogging();
        }
    @Log
        public void action () {
            testLogging.calculation(6);
            testLogging.calculation(5,5);
            testLogging.calculation(10,11,"Результат");
            testLogging.methodOne("Не логированный метод");
        }
    }
