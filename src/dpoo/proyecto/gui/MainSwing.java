package dpoo.proyecto.gui;

import javax.swing.SwingUtilities;

import dpoo.proyecto.app.MasterTicket;
import persistencia.CentralPersistencia;

/**
 * Punto de entrada de la aplicación Swing.
 * Carga el estado persistido y lanza la ventana principal.
 */
public class MainSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MasterTicket sistema = new MasterTicket();
            CentralPersistencia cp = new CentralPersistencia();
            cp.loadDefault(sistema);

            MainFrame frame = new MainFrame(sistema, cp);
            frame.setVisible(true);
        });
    }
}
