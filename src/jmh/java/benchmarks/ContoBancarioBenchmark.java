package benchmarks;

import org.example.banca.ContoBancario;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ContoBancarioBenchmark {

    @State(Scope.Thread)
    public static class MyState {

        @Setup(Level.Invocation)
        public void doSetup() {
           contoBancario = new ContoBancario("123", 100);
        }

        public ContoBancario contoBancario;

    }

    @Benchmark
    public void benchVersamento(MyState myState) {
        myState.contoBancario.versamento(50);
    }

    @Benchmark
    public void benchPrelievo_SufficienteSaldo(MyState myState) {
        myState.contoBancario.prelievo(50);
    }

    @Benchmark
    public void benchPrelievo_InsufficienteSaldo(MyState myState) {
        myState.contoBancario.prelievo(150);
    }

    @Benchmark
    public String benchgetId(MyState myState) {
        return myState.contoBancario.getId();
    }

    @Benchmark
    public void benchSetId(MyState myState) {
        myState.contoBancario.setId("456");
    }

    @Benchmark
    public Integer benchGetSaldo(MyState myState) {
        return myState.contoBancario.getSaldo();
    }

    @Benchmark
    public void benchSetSaldo(MyState myState) {
        myState.contoBancario.setSaldo(200);
    }

    @Benchmark
    public void benchSetSaldo2(MyState myState) {
        myState.contoBancario.setSaldo2(200);
    }

}
