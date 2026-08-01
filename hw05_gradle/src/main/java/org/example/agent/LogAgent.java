package org.example.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import org.example.annotations.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class LogAgent {
    private static final Logger logger = LoggerFactory.getLogger(LogAgent.class);

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
                .with(new AgentBuilder.Listener() {

                    @Override
                    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
                        logger.debug("Открытие: {}", typeName);
                    }

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
                        logger.debug("Преобразование: {}", typeDescription.getName());
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                        logger.debug("Игнорируется: {}", typeDescription.getName());
                    }

                    @Override
                    public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
                        logger.error("Не удалось преобразовать {} : {}", s, throwable.getMessage());
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
                        logger.debug("Полноценный: {}", typeName);
                    }

                })
                .installOn(inst);
    }
}



