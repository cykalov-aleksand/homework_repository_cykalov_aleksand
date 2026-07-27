package jmh.test_assigment_methods;

import org.example.AssignmentMethods;
import org.example.module_hw02_modified.ArraySimulator;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)          // среднее время на один вызов
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ModuleBenchMarkAssignmentMethods {
    private org.example.ArraySimulator arraySimulator;
    private ArraySimulator arraySimulator1;
    private AssignmentMethods assignmentMethods;
    private AssignmentMethods assignmentMethods1;
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
        assignmentMethods = new AssignmentMethods();
        assignmentMethods1 = new AssignmentMethods();
    }

    @Benchmark
    public Integer[] testMethodAssignmentMethodsReplaceArray() {
        return assignmentMethods.replacingArrayElement(arraySimulator.simulatorArrayInteger(100_000, -100, 100), 5, 7);
    }

    @Benchmark
    public Integer[] testMethodAssignmentMethodsReplaceArrayModified() {
        return assignmentMethods1.replacingArrayElement(arraySimulator1.simulatorArrayInteger(100_000, -100, 100), 5, 7);
    }

    @Benchmark
    public List<Integer> testMethodAssignmentConvertArrayList() {
        return assignmentMethods.convertArrayList(arraySimulator.simulatorArrayInteger(100_000, -100, 100));
    }

    @Benchmark
    public List<Integer> testMethodAssignmentConvertArrayListModified() {
        return assignmentMethods1.convertArrayList(arraySimulator1.simulatorArrayInteger(100_000, -100, 100));
    }

    @Benchmark
    public Map<String, Integer> testMethodAssignmentConvertListUniqueWordsSorted() {
        return assignmentMethods.listUniqueWordsSorted(arraySimulator.simulatorArrayString(string));
    }

    @Benchmark
    public Map<String, Integer> testMethodAssignmentListUniqueWordsSortedModified() {
        return assignmentMethods1.listUniqueWordsSorted(arraySimulator1.simulatorArrayString(string));
    }
}
