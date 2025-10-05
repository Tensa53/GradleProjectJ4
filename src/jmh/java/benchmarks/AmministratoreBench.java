package benchmarks;

import org.example.utente.personale.Amministratore;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class AmministratoreBench {

    @State(Scope.Thread)
    public static class MyState {

        @Setup(Level.Invocation)
        public void doSetup() {
            amministratore = new Amministratore("John", "Doe", "HR");
        }

        public Amministratore amministratore;
    }

    @Benchmark
    public String benchGetName(MyState myState) {
        return myState.amministratore.getName();
    }

    @Benchmark
    public String benchGetSurname(MyState myState) {
        return myState.amministratore.getSurname();
    }

    @Benchmark
    public String benchGetDepartment(MyState myState) {
        return myState.amministratore.getDepartment();
    }

    @Benchmark
    public void benchSetName(MyState myState) {
        myState.amministratore.setName("Jane");
    }

    @Benchmark
    public void benchSetSurname(MyState myState) {
        myState.amministratore.setSurname("Foe");
    }

    @Benchmark
    public void benchSetDepartment(MyState myState) {
        myState.amministratore.setDepartment("IT");
    }

}
