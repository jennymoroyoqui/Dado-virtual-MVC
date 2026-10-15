package parchis.dado.vista;

import parchis.dado.modelo.IModeloDadoLectura;
import parchis.dado.modelo.ObservadorDado;
import parchis.dado.modelo.Tiro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

//Historial de tiros agrupado por turno
public class VistaHistorial extends JPanel implements ObservadorDado {

    private final IModeloDadoLectura modelo;
    private final JTextArea txtHistorial = new JTextArea(10, 24);
    private final JLabel lblResumen = new JLabel(" ", SwingConstants.CENTER);

    public VistaHistorial(IModeloDadoLectura modelo) {
        this.modelo = modelo;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        txtHistorial.setEditable(false);
        txtHistorial.setLineWrap(true);
        txtHistorial.setWrapStyleWord(true);
        txtHistorial.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        lblResumen.setFont(lblResumen.getFont().deriveFont(Font.BOLD, 13f));

        add(lblResumen, BorderLayout.NORTH);
        add(new JScrollPane(txtHistorial), BorderLayout.CENTER);

        modelo.agregarObservador(this);
        refrescar(modelo);
    }

    @Override
    public void actualizar(IModeloDadoLectura modelo) {
        if (SwingUtilities.isEventDispatchThread()) {
            refrescar(modelo);
        } else {
            SwingUtilities.invokeLater(() -> refrescar(modelo));
        }
    }

    public void desconectar() {
        modelo.eliminarObservador(this);
    }

    private void refrescar(IModeloDadoLectura m) {
        List<Tiro> tiros = m.getHistorial();
        lblResumen.setText("Historial · " + tiros.size() + (tiros.size() == 1 ? " tiro" : " tiros"));
        txtHistorial.setText(construirTexto(tiros));
        txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
    }

    private String construirTexto(List<Tiro> tiros) {
        if (tiros.isEmpty()) {
            return "Aun no hay tiros.";
        }
        StringBuilder sb = new StringBuilder();
        int turnoActual = -1;
        for (Tiro t : tiros) {
            if (t.turno() != turnoActual) {
                if (turnoActual != -1) {
                    sb.append('\n');
                }
                turnoActual = t.turno();
                sb.append(String.format("Turno %d: ", turnoActual));
            } else {
                sb.append(" · ");
            }
            sb.append(t.valor());
        }
        return sb.toString();
    }
}
