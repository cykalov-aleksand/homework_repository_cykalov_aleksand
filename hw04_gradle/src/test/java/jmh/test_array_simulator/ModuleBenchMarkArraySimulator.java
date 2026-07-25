package jmh.test_array_simulator;

import org.example.module_hw02.ArraySimulator;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ModuleBenchMarkArraySimulator {
    private org.example.ArraySimulator arraySimulator;
    private ArraySimulator arraySimulator1;


    private String string;

    @Setup
    public void setup() throws Exception {
        string = """
                Люблю гроозу в начале мая Люблю грозу в начале мая.
                Когда весенний, первый гром,
                Как бы резвяся и играя,
                Грохочет в небе голубом.""";
        arraySimulator = new org.example.ArraySimulator();
        arraySimulator1 = new ArraySimulator();
    }

    @Benchmark
    public Integer[] testMethodArrayInteger() {
        return arraySimulator.simulatorArrayInteger(100_000, -100, 100);
    }

    @Benchmark
    public Integer[] testMethodArrayIntegerModified() {
        return arraySimulator1.simulatorArrayInteger(100_000, -100, 100);
    }

    @Benchmark
    public String[] testMethodArrayString() {
        return arraySimulator.simulatorArrayString(string);
    }

    @Benchmark
    public String[] testMethodArrayStringModified() {
        return arraySimulator1.simulatorArrayString(string);
    }
}
