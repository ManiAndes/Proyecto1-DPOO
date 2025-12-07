package dpoo.proyecto.gui;

import javax.swing.SwingUtilities;

public class AppSwingMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MasterTicketUI ui = new MasterTicketUI();
            ui.setVisible(true);
        });
    }
}
