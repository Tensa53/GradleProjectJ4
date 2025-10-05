package benchmarks;

import org.example.banca.ContoBancario;
import org.example.utente.Utente;
import org.mockito.Mockito;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class UtenteBenchmark {

    @State(Scope.Thread)
    public static class MyState {

        @Setup(Level.Invocation)
        public void doSetup() {
            ContoBancario mockContoBancario = Mockito.mock(ContoBancario.class);
            utente = new Utente("John", "Doe", "123", "via mazzini", mockContoBancario);
        }

        public Utente utente;
    }

    @Benchmark
    public String benchGetName(MyState myState) {
        return myState.utente.getName();
    }

    @Benchmark
    public String benchGetSurname(MyState myState) {
        return myState.utente.getSurname();
    }

    @Benchmark
    public String benchGetTelephone(MyState myState) {
        return myState.utente.getTelephone();
    }

    @Benchmark
    public String benchGetAddress(MyState myState) {
        return myState.utente.getAddress(1);
    }

    @Benchmark
    public ContoBancario benchGetContoBancario(MyState myState) {
        return myState.utente.getContoBancario();
    }

    @Benchmark
    public void benchSetName(MyState myState) {
        myState.utente.setName("John");
    }

    @Benchmark
    public void benchSetSurname(MyState myState) {
        myState.utente.setSurname("Smith");
    }

    @Benchmark
    public void benchSetConto(MyState myState) {
        ContoBancario nuovoConto = Mockito.mock(ContoBancario.class);
        myState.utente.setContoBancario(nuovoConto);
    }

}
