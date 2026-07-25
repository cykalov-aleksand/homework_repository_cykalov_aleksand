package test_jmh;

import org.example.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)          // среднее время на один вызов
@OutputTimeUnit(TimeUnit.MILLISECONDS)  // лучше в наносекундах для мелких операций
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
        args = new Object[]{ new String[]{} };
    }

    @Benchmark
    public void directCall() {
       Main.main(new String[]{});
    }

     @Benchmark
    public void reflectionCall() throws InvocationTargetException, IllegalAccessException {
        mainMethod.invoke(null, args);
    }

    // Точка входа для запуска JMH (можно вынести в отдельный класс)
    public static void main(String[] args) throws RunnerException {
        var opt = new OptionsBuilder()
                .forks(1)                 // 3 отдельных процесса
                // .warmupIterations(2)     // 5 прогревочных итераций в каждом форке
                .measurementIterations(20) // 20 измерительных итераций
                .build();
        new Runner(opt).run();
    }
}
