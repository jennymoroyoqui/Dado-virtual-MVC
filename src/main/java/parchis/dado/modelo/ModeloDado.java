package parchis.dado.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Guarda el estado y aplica la regla del Parchís con 6 se vuelve a tirar,
 * máximo tres veces seguidas
 *
 */
public class ModeloDado implements IModeloDado, IModeloDadoLectura {

    public static final int MAX_SEISES_SEGUIDOS = 3;

    private final Random random;
    private final List<ObservadorDado> observadores = new ArrayList<>();
    private final List<Tiro> historial = new ArrayList<>();

    private int valor;
    private int seisSeguidos;
    private boolean puedeTirar = true;
    private int turno = 1;

    public ModeloDado() {
        this(new Random());
    }

    public ModeloDado(Random random) {
        this.random = random;
    }

    @Override
    public void lanzar() {
        if (!puedeTirar) {
            return;
        }
        valor = random.nextInt(6) + 1;
        seisSeguidos = (valor == 6) ? seisSeguidos + 1 : 0;
        puedeTirar = valor == 6 && seisSeguidos < MAX_SEISES_SEGUIDOS;
        historial.add(new Tiro(turno, valor));
        notificar();
    }

    @Override
    public void reiniciarTurno() {
        turno++;
        valor = 0;
        seisSeguidos = 0;
        puedeTirar = true;
        notificar();
    }

    @Override
    public int getValor() {
        return valor;
    }

    @Override
    public int getSeisSeguidos() {
        return seisSeguidos;
    }

    @Override
    public boolean puedeTirar() {
        return puedeTirar;
    }

    @Override
    public int getTurno() {
        return turno;
    }

    @Override
    public List<Tiro> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historial));
    }

    // ---------- Observer ----------
    @Override
    public void agregarObservador(ObservadorDado o) {
        if (o != null && !observadores.contains(o)) {
            observadores.add(o);
        }
    }

    @Override
    public void eliminarObservador(ObservadorDado o) {
        observadores.remove(o);
    }

    protected void notificar() {
        for (ObservadorDado o : new ArrayList<>(observadores)) {
            o.actualizar(this);
        }
    }
}
