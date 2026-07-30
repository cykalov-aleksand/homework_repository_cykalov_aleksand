package org.example;


public class Demo {
    private final TestLogging testLogging;


    public Demo() {
            this.testLogging = new TestLogging();
        }

        public void action () {
            testLogging.calculation(6);
        }
    }
