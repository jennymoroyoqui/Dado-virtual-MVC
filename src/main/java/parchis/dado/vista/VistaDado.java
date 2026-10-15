package parchis.dado.vista;

import parchis.dado.controlador.ControlDado;
import parchis.dado.modelo.IModeloDadoLectura;
import parchis.dado.modelo.ModeloDado;
import parchis.dado.modelo.ObservadorDado;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Vista 1 del dado dibuja la cara con Graphics y captura las acciones del usuario
 * Solo recibe la interfaz de LECTURA, asi no puede llamar a lanzar() por error
 * los botones unicamente avisan al controlador actualizar() consulta al modelo y repinta
 */
public class VistaDado extends JPanel implements ObservadorDado {

    private final IModeloDadoLectura modelo;
    private final CaraDado cara = new CaraDado();
    private final JLabel lblEstado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnLanzar = new JButton("Lanzar");
    private final JButton btnNuevoTurno = new JButton("Nuevo turno");

    public VistaDado(IModeloDadoLectura modelo, ControlDado control) {
        this.modelo = modelo;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD, 13f));
        lblEstado.setPreferredSize(new Dimension(360, 22));   // cabe tres seises seguidos turno terminado

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.add(btnLanzar);
        botones.add(btnNuevoTurno);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centro.add(cara);

        add(lblEstado, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        // Los botones solo avisan al controlador
        btnLanzar.addActionListener(e -> control.lanzar());
        btnNuevoTurno.addActionListener(e -> control.reiniciarTurno());

        modelo.agregarObservador(this);
        refrescar(modelo);
    }

    @Override
    public void actualizar(IModeloDadoLectura modelo) {
        // Swing solo se toca desde el hilo de eventos 
        if (SwingUtilities.isEventDispatchThread()) {
            refrescar(modelo);
        } else {
            SwingUtilities.invokeLater(() -> refrescar(modelo));
        }
    }

    //Se llama al cerrar la ventana: la vista deja de recibir avisos
    public void desconectar() {
        modelo.eliminarObservador(this);
    }

    private void refrescar(IModeloDadoLectura m) {
        cara.setValor(m.getValor());
        btnLanzar.setEnabled(m.puedeTirar());
        lblEstado.setText(textoEstado(m));
    }

    private String textoEstado(IModeloDadoLectura m) {
        String turno = "Turno " + m.getTurno() + " · ";
        if (m.getValor() == 0) {
            return turno + "lanza el dado";
        }
        if (m.puedeTirar()) {
            return turno + "¡salió 6! Tira de nuevo (" + m.getSeisSeguidos() + "/"
                    + ModeloDado.MAX_SEISES_SEGUIDOS + ")";
        }
        if (m.getSeisSeguidos() >= ModeloDado.MAX_SEISES_SEGUIDOS) {
            return turno + "tres seises seguidos: turno terminado";
        }
        return turno + "turno terminado";
    }

    // ---------- Dibujo del dado con Graphics ----------
    private static class CaraDado extends JPanel {

        private static final int TAM = 130;
        private int valor;

        CaraDado() {
            setPreferredSize(new Dimension(TAM + 8, TAM + 8));
            setOpaque(false);
        }

        void setValor(int valor) {
            this.valor = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (getWidth() - TAM) / 2;
            int y = (getHeight() - TAM) / 2;

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fill(new RoundRectangle2D.Double(x + 3, y + 4, TAM, TAM, 26, 26));
            g2.setColor(new Color(0xFAFAF5));
            g2.fill(new RoundRectangle2D.Double(x, y, TAM, TAM, 26, 26));
            g2.setColor(new Color(0x162238));
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new RoundRectangle2D.Double(x, y, TAM, TAM, 26, 26));

            if (valor == 0) {
                g2.setFont(getFont().deriveFont(Font.BOLD, 48f));
                FontMetrics fm = g2.getFontMetrics();
                String s = "?";
                g2.setColor(new Color(0x9AA3AF));
                g2.drawString(s, x + (TAM - fm.stringWidth(s)) / 2, y + (TAM + fm.getAscent() - fm.getDescent()) / 2);
            } else {
                dibujarPuntos(g2, x, y);
            }
            g2.dispose();
        }

        private void dibujarPuntos(Graphics2D g2, int x, int y) {
            int d = TAM / 6;                       
            int a = TAM / 4 - d / 2;             
            int b = TAM / 2 - d / 2;
            int c = TAM * 3 / 4 - d / 2;
            // el 6 es el valor que da otro turno se pinta en rojo
            g2.setColor(valor == 6 ? new Color(0xC8392B) : new Color(0x162238));

            if (valor == 1 || valor == 3 || valor == 5) punto(g2, x + b, y + b, d);     // centro
            if (valor >= 2) { punto(g2, x + a, y + a, d); punto(g2, x + c, y + c, d); } // diagonal 
            if (valor >= 4) { punto(g2, x + c, y + a, d); punto(g2, x + a, y + c, d); } // diagonal 
            if (valor == 6) { punto(g2, x + a, y + b, d); punto(g2, x + c, y + b, d); } // laterales
        }

        private void punto(Graphics2D g2, int px, int py, int d) {
            g2.fillOval(px, py, d, d);
        }
    }
}
