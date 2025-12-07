package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.EncodeHintType;

import java.util.EnumMap;
import java.util.Map;

import dpoo.proyecto.tiquetes.Tiquete;

public class TicketPrintDialog extends JDialog {

    private static final DateTimeFormatter PRINT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean imprimir(java.awt.Component parent, Tiquete tiquete) {
        try {
            // Validar dependencias de ZXing antes de abrir el diálogo, para dar un error claro.
            Class.forName("com.google.zxing.MultiFormatWriter");
            TicketPrintDialog dialog = new TicketPrintDialog(parent, tiquete);
            dialog.setVisible(true);
            return dialog.impreso;
        } catch (Throwable t) {
            javax.swing.JOptionPane.showMessageDialog(parent,
                    "No se pudo imprimir el tiquete: " + t.getMessage()
                            + "\nAsegúrate de ejecutar con lib/zxing-core-3.5.3.jar en el classpath (ej: java -cp \"bin:lib/*\" ...).");
            return false;
        }
    }

    private boolean impreso = false;

    private TicketPrintDialog(java.awt.Component parent, Tiquete tiquete) {
        super(SwingUtilities.getWindowAncestor(parent), "Tiquete " + tiquete.getId(), ModalityType.APPLICATION_MODAL);
        setSize(520, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        String evento = tiquete.getEvento() != null ? tiquete.getEvento().getNombre() : "N/A";
        String fechaEvento = tiquete.getEvento() != null ? tiquete.getEvento().getFecha() : "N/A";
        String fechaImpresion = PRINT_FORMAT.format(LocalDateTime.now());
        String payload = evento + "|" + tiquete.getId() + "|" + fechaEvento + "|" + fechaImpresion;

        BufferedImage qr = generarQR(payload, 280);

        JPanel info = new JPanel(new java.awt.GridLayout(0, 1));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.add(new JLabel("Evento: " + evento));
        info.add(new JLabel("Tiquete ID: " + tiquete.getId()));
        info.add(new JLabel("Fecha evento: " + fechaEvento));
        info.add(new JLabel("Fecha impresión: " + fechaImpresion));
        JLabel qrLabel = new JLabel(qr != null ? new javax.swing.ImageIcon(qr) : new JLabel("QR no disponible").getIcon());
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel center = new JPanel(new BorderLayout());
        center.add(info, BorderLayout.NORTH);
        center.add(qrLabel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardar = new JButton("Guardar PNG");
        JButton cerrar = new JButton("Cerrar");
        actions.add(guardar);
        actions.add(cerrar);
        add(actions, BorderLayout.SOUTH);

        guardar.addActionListener(e -> {
            if (qr == null) {
                JOptionPane.showMessageDialog(this, "No se pudo generar el código QR.");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("tiquete_" + tiquete.getId() + ".png"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    ImageIO.write(qr, "png", chooser.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Guardado en " + chooser.getSelectedFile().getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error guardando: " + ex.getMessage());
                }
            }
        });

        cerrar.addActionListener(e -> dispose());

        tiquete.marcarImpreso(fechaImpresion);
        this.impreso = true;
    }

    private BufferedImage generarQR(String data, int size) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size, size);
            g.setColor(Color.BLACK);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (matrix.get(x, y)) {
                        g.fillRect(x, y, 1, 1);
                    }
                }
            }
            g.dispose();
            return image;
        } catch (Exception e) {
            return null;
        }
    }
}
