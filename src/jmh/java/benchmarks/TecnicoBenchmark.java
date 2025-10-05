package benchmarks;

import org.example.utente.personale.Tecnico;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TecnicoBenchmark {

    @State(Scope.Thread)
    public static class MyState {

        @Setup(Level.Invocation)
        public void doSetup() {
            tecnico = new Tecnico("John", "Doe", "Engineer",1);
        }

        public Tecnico tecnico;
    }

    @Benchmark
    public String benchGetName(MyState myState) {
        return myState.tecnico.getName();
    }

    @Benchmark
    public String benchGetSurname(MyState myState) {
        return myState.tecnico.getSurname();
    }

    @Benchmark
    public String benchGetProfession(MyState myState) {
        return myState.tecnico.getProfession();
    }

    @Benchmark
    public Integer benchGetCode(MyState myState) {
        return myState.tecnico.getCode();
    }

    @Benchmark
    public void benchSetName(MyState myState) {
        myState.tecnico.setName("Jane");
    }

    @Benchmark
    public void benchSetSurname(MyState myState) {
        myState.tecnico.setSurname("Smith");
    }

    @Benchmark
    public void benchSetProfession(MyState myState) {
        myState.tecnico.setProfession("Engineer");
    }

    @Benchmark
    public void benchSetCode(MyState myState) {
        myState.tecnico.setCode(1);
    }

}
