package benchmarks.profilers;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.runner.IterationType;
import java.util.Collection;
import java.util.Collections;

public class JaCoCoProfiler implements InternalProfiler {
    @Override
    public String getDescription() {
        return "My custom listener-like profiler";
    }

    @Override
    public void beforeIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams) {
        IterationType type = iterationParams.getType();

        if (type == IterationType.MEASUREMENT) {
            iterationCounter++;
//            System.out.printf("Measurement iteration %d of %d%n", measureCounter, total);

            // Example: dump coverage here
            // CoverageUtils.dumpCoverage(benchParams.getBenchmark() + "_iter" + measureCounter);
        }
    }

    @Override
    public Collection<? extends Result<?>> afterIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams,
                                                          IterationResult result) {
        System.out.println("Iteration finished for " + benchmarkParams.getBenchmark());

        String benchmarkFqn = benchmarkParams.getBenchmark();

        IterationType type = iterationParams.getType();

        if (type == IterationType.MEASUREMENT && iterationCounter == iterationParams.getCount()) {
            int lastDot = benchmarkFqn.lastIndexOf('.');
            String className = benchmarkFqn.substring(0, lastDot);
            String methodName = benchmarkFqn.substring(lastDot + 1);

            JaCoCoCoverageMatrix.updateCoverageMatrix(methodName, className, "reports-coverage/jmh/", "build/jacoco/bench.exec", "build/classes/java/main");
        }

        return Collections.emptyList();
    }

    private int iterationCounter = 0;
}

