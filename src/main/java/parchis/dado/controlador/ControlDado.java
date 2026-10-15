package parchis.dado.controlador;

import parchis.dado.modelo.IModeloDado;

/**
 * Controlador del dado traduce cada accion del usuario en una operación del
 * modelo, no dibuja no tiene componentes gráficos y solo conoce IModeloDado
 */
public class ControlDado {

    private final IModeloDado modelo;

    public ControlDado(IModeloDado modelo) {
        this.modelo = modelo;
    }

    /** Clic en «Lanzar». */
    public void lanzar() {
        modelo.lanzar();
    }

    /** Clic en «Nuevo turno». */
    public void reiniciarTurno() {
        modelo.reiniciarTurno();
    }
}
