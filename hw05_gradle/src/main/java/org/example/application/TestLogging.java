package org.example.application;

import org.example.annotations.Log;

public class TestLogging implements TestLoggingInterface {
    @Override
    @Log
    public void calculation(int param) {
        System.out.println(param);
    }

    @Override
    @Log
    public void calculation(int param1, int param2) {
        System.out.println("param1 +param2 = "+(param1+param2));
    }

    @Override
    @Log
    public void calculation(int param1, int param2, String param3) {
        System.out.println("param1 +param2 = "+(param1+param2)+" "+param3);
        method();
        }
    @Log
    private void method (){
        System.out.println("Приватный логированный метод");
    }
    public void methodOne (String param){
        System.out.println(param);
    }
}
