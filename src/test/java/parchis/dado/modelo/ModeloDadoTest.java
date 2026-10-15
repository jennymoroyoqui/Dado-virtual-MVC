package parchis.dado.modelo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las reglas del dado sin abrir ninguna ventana
 * el modelo no conoce la biblioteca grafica
 */
class ModeloDadoTest {

    //Devuelve valores 1 al 6 en el orden indicado
    private static Random secuencia(int... valores) {
        return new Random() {
            private int i = 0;

            @Override
            public int nextInt(int limite) {
                return valores[i++] - 1;   // lanzar() hace nextInt(6) + 1
            }
        };
    }

    @Test
    void estadoInicial() {
        ModeloDado m = new ModeloDado(secuencia(3));
        assertEquals(0, m.getValor());
        assertEquals(0, m.getSeisSeguidos());
        assertTrue(m.puedeTirar());
        assertTrue(m.getHistorial().isEmpty());
    }

    @Test
    void valorSiempreEntreUnoYSeis() {
        ModeloDado m = new ModeloDado();
        for (int i = 0; i < 500; i++) {
            m.reiniciarTurno();
            m.lanzar();
            assertTrue(m.getValor() >= 1 && m.getValor() <= 6);
        }
    }

    @Test
    void sinSeisElTurnoTermina() {
        ModeloDado m = new ModeloDado(secuencia(4, 2));
        m.lanzar();
        assertEquals(4, m.getValor());
        assertFalse(m.puedeTirar());
        m.lanzar();                       
        assertEquals(4, m.getValor());
        assertEquals(1, m.getHistorial().size());
    }

    @Test
    void conSeisVuelvesATirar() {
        ModeloDado m = new ModeloDado(secuencia(6, 3));
        m.lanzar();
        assertEquals(1, m.getSeisSeguidos());
        assertTrue(m.puedeTirar());
        m.lanzar();
        assertEquals(3, m.getValor());
        assertEquals(0, m.getSeisSeguidos());
        assertFalse(m.puedeTirar());
    }

    @Test
    void maximoTresSeisesSeguidos() {
        ModeloDado m = new ModeloDado(secuencia(6, 6, 6, 2));
        m.lanzar();
        m.lanzar();
        assertTrue(m.puedeTirar());         
        m.lanzar();
        assertEquals(3, m.getSeisSeguidos());
        assertFalse(m.puedeTirar());        
        m.lanzar();                         
        assertEquals(6, m.getValor());
        assertEquals(3, m.getHistorial().size());
    }

    @Test
    void reiniciarTurnoRestauraElEstado() {
        ModeloDado m = new ModeloDado(secuencia(6, 6, 6));
        m.lanzar();
        m.lanzar();
        m.lanzar();
        assertFalse(m.puedeTirar());
        m.reiniciarTurno();
        assertTrue(m.puedeTirar());
        assertEquals(0, m.getSeisSeguidos());
        assertEquals(0, m.getValor());
        assertEquals(2, m.getTurno());
        assertEquals(3, m.getHistorial().size());  
    }

    @Test
    void historialRegistraTurnoYValor() {
        ModeloDado m = new ModeloDado(secuencia(6, 2, 5));
        m.lanzar();
        m.lanzar();
        m.reiniciarTurno();
        m.lanzar();
        assertEquals(List.of(new Tiro(1, 6), new Tiro(1, 2), new Tiro(2, 5)), m.getHistorial());
    }

    @Test
    void elHistorialExpuestoNoSePuedeModificar() {
        ModeloDado m = new ModeloDado(secuencia(1));
        m.lanzar();
        assertThrows(UnsupportedOperationException.class, () -> m.getHistorial().clear());
    }

    @Test
    void notificaATodosLosObservadoresEnCadaCambio() {
        ModeloDado m = new ModeloDado(secuencia(6, 3));
        List<String> avisos = new ArrayList<>();
        m.agregarObservador(modelo -> avisos.add("A" + modelo.getValor()));
        m.agregarObservador(modelo -> avisos.add("B" + modelo.getValor()));
        m.lanzar();
        m.lanzar();
        m.reiniciarTurno();
        assertEquals(List.of("A6", "B6", "A3", "B3", "A0", "B0"), avisos);
    }

    @Test
    void unaTiradaIgnoradaNoNotifica() {
        ModeloDado m = new ModeloDado(secuencia(2));
        m.lanzar();
        List<Integer> avisos = new ArrayList<>();
        m.agregarObservador(modelo -> avisos.add(modelo.getValor()));
        m.lanzar();                         // no puede tirar sin un cambio de estado
        assertTrue(avisos.isEmpty());
    }

    @Test
    void observadorEliminadoYaNoRecibeAvisos() {
        ModeloDado m = new ModeloDado(secuencia(2));
        List<Integer> avisos = new ArrayList<>();
        ObservadorDado o = modelo -> avisos.add(modelo.getValor());
        m.agregarObservador(o);
        m.eliminarObservador(o);
        m.lanzar();
        assertTrue(avisos.isEmpty());
    }

    @Test
    void unObservadorPuedeDarseDeBajaDuranteElAviso() {
        ModeloDado m = new ModeloDado(secuencia(2));
        ObservadorDado[] propio = new ObservadorDado[1];
        propio[0] = modelo -> modelo.eliminarObservador(propio[0]);
        m.agregarObservador(propio[0]);
        assertDoesNotThrow(m::lanzar);     
    }
}
