package parchis.dado.modelo;

/**
 * Registro del historial en qué turno se tiro y qué valor salio es inmutable,
 * asi la vista del historial no puede alterar el modelo
 */
public record Tiro(int turno, int valor) {

}
