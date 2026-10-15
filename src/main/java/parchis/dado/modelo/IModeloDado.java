package parchis.dado.modelo;

/**
 * Lo que el controlador puede pedir
 * El controlador solo modifica no necesita consultar ni suscribirse
 */
public interface IModeloDado {

    void lanzar();

    void reiniciarTurno();
}
