package parchis;

import parchis.controlador.ControladorTurno;
import parchis.dado.controlador.ControlDado;
import parchis.dado.modelo.ModeloDado;
import parchis.dado.vista.VistaDado;
import parchis.dado.vista.VistaHistorial;
import parchis.fachada.FachadaJuegoMock;
import parchis.modelo.Color;
import parchis.modelo.*;
import parchis.vista.VistaPartidaGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Fernando y Sofia observan el mismo ModeloPartida y junto a ellos se abre el
 * dado virtual con MVC un solo ModeloDado con dos vistas el dado dibujado y el
 * historial de tiros y un controlador La GUI se crea en el hilo de eventos de
 * Swing
 */
public class MainGUI {

    public static void main(String[] args) {
        boolean demo = args.length > 0 && args[0].equals("--demo");
        SwingUtilities.invokeLater(() -> {
            Jugador fernando = new Jugador("Fernando", Color.VERDE, "avatar_fernando");
            Jugador sofia = new Jugador("Sofia", Color.ROJO, "avatar_sofia");

            Tablero tablero = new Tablero(List.of(Color.VERDE, Color.ROJO));
            FachadaJuegoMock fachada = new FachadaJuegoMock(tablero);
            if (demo) {
                fachada.programarTiradas(new int[]{2, 3});
            }
            ModeloPartida modelo = new ModeloPartida(List.of(fernando, sofia), tablero, fachada);
            ControladorTurno controlador = new ControladorTurno(modelo);

            new VistaPartidaGUI(modelo, controlador, fernando).mostrarEnVentana(30, 20);
            new VistaPartidaGUI(modelo, controlador, sofia).mostrarEnVentana(640, 20);
            mostrarDadoVirtual();

            modelo.iniciarPartida(0);   // Fernando comienza mientras Sofia solo observa
        });
    }

    //Dado virtual con MVC 
    private static void mostrarDadoVirtual() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int x = Math.max(0, Math.min(1250, pantalla.width - 400));
        int y = 20;

        ModeloDado modelo = new ModeloDado();
        ControlDado control = new ControlDado(modelo);

        VistaDado vistaDado = new VistaDado(modelo, control);
        VistaHistorial vistaHistorial = new VistaHistorial(modelo);

        JFrame ventanaDado = crearVentana("Dado virtual (MVC)", vistaDado, x, y);
        JFrame ventanaHistorial = crearVentana("Historial de tiros", vistaHistorial,
                x, y + ventanaDado.getHeight() + 10);

        ventanaDado.addWindowListener(alCerrar(vistaDado::desconectar));
        ventanaHistorial.addWindowListener(alCerrar(vistaHistorial::desconectar));
    }

    private static JFrame crearVentana(String titulo, JPanel contenido, int x, int y) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(contenido);
        frame.pack();
        frame.setLocation(x, y);
        frame.setVisible(true);
        return frame;
    }

    private static WindowAdapter alCerrar(Runnable accion) {
        return new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                accion.run();
            }
        };
    }
}
