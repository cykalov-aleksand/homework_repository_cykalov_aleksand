package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import org.example.anatations.Log;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;

public class LogAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                // Ищем классы, у которых есть хотя бы один метод с @Log
                .type(any())
                .transform((builder, typeDescription, classLoader, protectionDomain, module) ->
                        // Перехватываем только методы с @Log
                        builder.method(isAnnotatedWith(Log.class))
                                .intercept(MethodDelegation.to(LogInterceptor.class))
                )
                .installOn(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }
}