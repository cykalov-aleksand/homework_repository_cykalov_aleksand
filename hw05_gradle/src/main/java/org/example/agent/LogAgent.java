package org.example.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import org.example.anatations.Log;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class LogAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(nameStartsWith("org.example."))
                .transform((builder,
                            typeDescription,
                            classLoader,
                            protectionDomain,
                            module) ->
                        builder.method(isAnnotatedWith(Log.class))
                                .intercept(MethodDelegation.to(LogInterceptor.class)))
                .installOn(inst);
    }

   // public static void agentmain(String agentArgs, Instrumentation inst) {
       // premain(agentArgs, inst);
  //  }
}