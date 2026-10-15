package parchis.dado.modelo;

import java.util.List;

/**
 * Es lo que la vista puede consultar Suscribirse no cambia el estado del dado,
 * por eso agregarObservador La vista recibe esta interfaz y nunca ve lanzar
 */
public interface IModeloDadoLectura {

    int getValor();

    int getSeisSeguidos();

    boolean puedeTirar();

    int getTurno();

    List<Tiro> getHistorial();

    void agregarObservador(ObservadorDado o);

    void eliminarObservador(ObservadorDado o);
}
