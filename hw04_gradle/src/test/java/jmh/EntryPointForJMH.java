package jmh;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class EntryPointForJMH {
    public static void main(String[] args) throws RunnerException {
        var opt = new OptionsBuilder()
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(2)
                .build();
        new Runner(opt).run();
    }
}
