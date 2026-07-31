package org.example.agent;

import net.bytebuddy.implementation.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class LogInterceptor {
    private static final Logger logger= LoggerFactory.getLogger(LogInterceptor.class);
    @RuntimeType
    public static Object intercept(
            @Origin java.lang.reflect.Method method,
            @This Object target,
            @AllArguments Object[] args,
            @SuperCall Callable<?> callable) throws Exception {

       StringBuilder sb = new StringBuilder();
        sb.append("В классе - ").append(target.getClass()).
                append(" выполнен метод: ").append(method.getName());
        for (int i = 0; i < args.length; i++) {
            sb.append(", param").append(i + 1).append(": ").append(args[i]);
        }
        logger.info(sb.toString());
        Object object=callable.call();
        logger.info(" Log после выполнения метода\n");
        return object;
    }
}