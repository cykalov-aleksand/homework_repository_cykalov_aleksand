package org.example;

import net.bytebuddy.implementation.bind.annotation.*;

import java.util.concurrent.Callable;

public class LogInterceptor {
    @RuntimeType
    public static Object intercept(
            @Origin java.lang.reflect.Method method,
            @This Object target,
            @AllArguments Object[] args,
            @SuperCall Callable<?> callable) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("executed method: ").append(method.getName());
        for (int i = 0; i < args.length; i++) {
            sb.append(", param").append(i + 1).append(": ").append(args[i]);
        }
        System.out.println(sb.toString());

        // ЭТО ВАЖНО: callable.call() — это вызов оригинального метода БЕЗ повторного перехвата
        return callable.call();
    }
}