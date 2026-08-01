package org.example.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class LogInterceptor {
    private static final Logger logger= LoggerFactory.getLogger(LogInterceptor.class);
    @RuntimeType
    public static Object intercept(
            @Origin java.lang.reflect.Method method,
            @This Object target,
            @AllArguments Object[] args,
            @SuperCall Callable<?> callable) throws Exception {
        outPrintEnglish(target, method,args);
       // outPrintLogger(target, method,args);
        return callable.call();
           }

    private static void outPrintLogger(Object target, Method method,Object[]args){
        StringBuilder sb = new StringBuilder();
        sb.append("В классе - ").append(target.getClass()).
                append(" выполнен метод: ").append(method.getName());
        for (int i = 0; i < args.length; i++) {
            sb.append(", param").append(i + 1).append(": ").append(args[i]);
        }
        logger.info(sb.toString());
    }
    private static void outPrintEnglish(Object target, Method method,Object[]args){
        StringBuilder sb = new StringBuilder();
        sb.append("In the class - ").append(target.getClass()).
                append(" executed method: ").append(method.getName());
        for (int i = 0; i < args.length; i++) {
            sb.append(", param").append(i + 1).append(": ").append(args[i]);
        }
        System.out.println(sb.toString());
    }
}