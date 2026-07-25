package jmh.overall_performance;

import org.example.Main;
import org.example.module_hw02_modified.MainModified;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)          // среднее время на один вызов
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ReflectionBenchmark {

    private Method mainMethod;
    private Object[] args;

    @Setup
    public void setup() throws Exception {
        File jarFile = new File("hw02_gradle.jar");
        URL jarUrl = jarFile.toURI().toURL();
        ClassLoader loader = new URLClassLoader(new URL[]{jarUrl});

        Class<?> clazz = loader.loadClass("org.example.Main");
        mainMethod = clazz.getMethod("main", String[].class);

        // Один аргумент: String[]
        args = new Object[]{new String[]{}};
    }

    @Benchmark
    public void directCall() {
        Main.main(new String[]{});
    }

    @Benchmark
    public void directCallModified() {
        MainModified.main(new String[]{});
    }

    @Benchmark
    public void reflectionCall() throws InvocationTargetException, IllegalAccessException {
        mainMethod.invoke(null, args);
    }

}