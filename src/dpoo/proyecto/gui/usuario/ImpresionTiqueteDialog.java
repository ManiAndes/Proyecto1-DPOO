package dpoo.proyecto.gui.usuario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.google.zxing.WriterException;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.gui.util.QrUtil;
import dpoo.proyecto.tiquetes.Tiquete;
import persistencia.CentralPersistencia;

/**
 * Ventana que representa la impresión de un tiquete con QR.
 * Marca el tiquete como impreso la primera vez y bloquea reimpresiones.
 */
public class ImpresionTiqueteDialog extends JDialog {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final Tiquete tiquete;

    public ImpresionTiqueteDialog(JFrame owner, MasterTicket sistema, CentralPersistencia persistencia, Tiquete tiquete) {
        super(owner, "Imprimir tiquete #" + (tiquete != null ? tiquete.getId() : ""), true);
        this.sistema = sistema;
        this.persistencia = persistencia;
        this.tiquete = tiquete;

        if (tiquete == null) {
            dispose();
            return;
        }

        if (tiquete.isImpreso()) {
            JOptionPane.showMessageDialog(owner, "Este tiquete ya fue impreso y no puede reimprimirse.", "Impresión bloqueada",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        marcarImpresion();
        buildUI();
    }

    private void marcarImpresion() {
        String fechaImp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        tiquete.setImpreso(true);
        tiquete.setFechaImpresion(fechaImp);
        tiquete.setTransferible(false);
        tiquete.setEstado("IMPRESO");
        if (persistencia != null && sistema != null) {
            persistencia.saveDefault(sistema);
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setText(descripcionTiquete());
        content.add(info, BorderLayout.CENTER);

        JLabel qrLabel = new JLabel("Generando QR...");
        qrLabel.setHorizontalAlignment(JLabel.CENTER);
        qrLabel.setPreferredSize(new Dimension(260, 260));
        content.add(qrLabel, BorderLayout.EAST);

        add(content, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        south.add(cerrar);
        add(south, BorderLayout.SOUTH);

        try {
            String payload = qrPayload();
            BufferedImage qr = QrUtil.generarQR(payload, 240);
            qrLabel.setIcon(new ImageIcon(qr));
            qrLabel.setText("");
        } catch (WriterException ex) {
            qrLabel.setText("No se pudo generar el QR");
        }

        pack();
        setLocationRelativeTo(getOwner());
    }

    private String descripcionTiquete() {
        Evento ev = tiquete.getEvento();
        String nombreEvento = ev != null ? ev.getNombre() : "N/A";
        String fechaEvento = ev != null ? ev.getFecha() : "N/A";
        return new StringBuilder()
                .append("Evento: ").append(nombreEvento).append("\n")
                .append("ID Tiquete: ").append(tiquete.getId()).append("\n")
                .append("Fecha Evento: ").append(fechaEvento).append("\n")
                .append("Fecha Impresión: ").append(tiquete.getFechaImpresion()).append("\n")
                .append("Localidad: ").append(tiquete.getLocalidad() != null ? tiquete.getLocalidad() : "N/A")
                .toString();
    }

    private String qrPayload() {
        Evento ev = tiquete.getEvento();
        String nombreEvento = ev != null ? ev.getNombre() : "N/A";
        String fechaEvento = ev != null ? ev.getFecha() : "N/A";
        return nombreEvento + "|" + tiquete.getId() + "|" + fechaEvento + "|" + tiquete.getFechaImpresion();
    }
}
