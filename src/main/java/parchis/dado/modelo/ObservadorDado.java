package parchis.dado.modelo;

/**
 * Observer propio del dado las vistas se suscriben al modelo mediante esta
 * interfaz; 
 * el modelo nunca conoce una vista concreta, solo a quien implemente ObservadorDado
 */
public interface ObservadorDado {

    void actualizar(IModeloDadoLectura modelo);
}
