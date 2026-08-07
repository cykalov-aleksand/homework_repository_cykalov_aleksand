package org.example.agent;


import java.lang.instrument.Instrumentation;

public class AgentAsm {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("=== LoggingAgent: premain вызван ===");
        inst.addTransformer(new MyClassTransformer(), true);
    }
}
