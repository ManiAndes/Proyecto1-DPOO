package dpoo.proyecto.gui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.usuarios.Administrador;
import persistencia.CentralPersistencia;

public class AdminDashboardPanel extends JPanel {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final Administrador admin;
    private final Runnable onLogout;

    private DefaultListModel<String> solicitudesOrgModel = new DefaultListModel<>();
    private DefaultListModel<String> venuesPendModel = new DefaultListModel<>();
    private DefaultListModel<String> reembolsosModel = new DefaultListModel<>();

    private JList<String> solicitudesOrgList;
    private JList<String> venuesPendList;
    private JList<String> reembolsosList;

    public AdminDashboardPanel(MasterTicket sistema, CentralPersistencia persistencia, Administrador admin, Runnable onLogout) {
        this.sistema = sistema;
        this.persistencia = persistencia;
        this.admin = admin;
        this.onLogout = onLogout;
        initUI();
        refresh();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Panel Administrador - " + admin.getLogin(), JLabel.CENTER);
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // Organizadores
        JPanel orgPanel = new JPanel(new BorderLayout());
        solicitudesOrgList = new JList<>(solicitudesOrgModel);
        orgPanel.add(new JScrollPane(solicitudesOrgList), BorderLayout.CENTER);
        JPanel orgActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton aprobarOrg = new JButton("Aprobar");
        aprobarOrg.addActionListener(e -> aprobarOrganizador());
        JButton rechazarOrg = new JButton("Rechazar");
        rechazarOrg.addActionListener(e -> rechazarOrganizador());
        orgActions.add(aprobarOrg);
        orgActions.add(rechazarOrg);
        orgPanel.add(orgActions, BorderLayout.SOUTH);
        tabs.addTab("Organizadores", orgPanel);

        // Venues
        JPanel venuePanel = new JPanel(new BorderLayout());
        venuesPendList = new JList<>(venuesPendModel);
        venuePanel.add(new JScrollPane(venuesPendList), BorderLayout.CENTER);
        JPanel venueActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton aprobarVenue = new JButton("Aprobar");
        aprobarVenue.addActionListener(e -> aprobarVenue());
        JButton rechazarVenue = new JButton("Rechazar");
        rechazarVenue.addActionListener(e -> rechazarVenue());
        venueActions.add(aprobarVenue);
        venueActions.add(rechazarVenue);
        venuePanel.add(venueActions, BorderLayout.SOUTH);
        tabs.addTab("Venues", venuePanel);

        // Reembolsos
        JPanel reembolsoPanel = new JPanel(new BorderLayout());
        reembolsosList = new JList<>(reembolsosModel);
        reembolsoPanel.add(new JScrollPane(reembolsosList), BorderLayout.CENTER);
        JPanel reembolsoActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton aprobarRe = new JButton("Aprobar (total)");
        aprobarRe.addActionListener(e -> aprobarReembolso());
        JButton rechazarRe = new JButton("Rechazar");
        rechazarRe.addActionListener(e -> rechazarReembolso());
        reembolsoActions.add(aprobarRe);
        reembolsoActions.add(rechazarRe);
        reembolsoPanel.add(reembolsoActions, BorderLayout.SOUTH);
        tabs.addTab("Reembolsos", reembolsoPanel);

        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardar = new JButton("Guardar estado");
        guardar.addActionListener(e -> persistencia.saveDefault(sistema));
        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });
        south.add(guardar);
        south.add(logout);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        solicitudesOrgModel.clear();
        for (String login : admin.getSolicitudesOrganizador().keySet()) {
            solicitudesOrgModel.addElement(login);
        }

        venuesPendModel.clear();
        for (Map.Entry<String, Venue> entry : sistema.getVenuesPendientes().entrySet()) {
            Venue v = entry.getValue();
            String desc = entry.getKey() + " - " + (v != null ? v.getUbicacion() : "");
            venuesPendModel.addElement(desc);
        }

        reembolsosModel.clear();
        List<SolicitudReembolso> pendientes = sistema.listarSolicitudesPendientes();
        for (SolicitudReembolso s : pendientes) {
            String evento = s.getTiquete() != null && s.getTiquete().getEvento() != null
                    ? s.getTiquete().getEvento().getNombre()
                    : "N/A";
            reembolsosModel.addElement("#" + s.getId() + " - " + evento + " - " + s.getEstado());
        }
    }

    private void aprobarOrganizador() {
        String login = solicitudesOrgList.getSelectedValue();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        boolean ok = sistema.aprobarSolicitudOrganizador(login);
        if (ok) {
            persistencia.saveDefault(sistema);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo aprobar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechazarOrganizador() {
        String login = solicitudesOrgList.getSelectedValue();
        if (login == null) return;
        boolean ok = sistema.rechazarSolicitudOrganizador(login);
        if (ok) {
            persistencia.saveDefault(sistema);
            refresh();
        }
    }

    private void aprobarVenue() {
        String selected = venuesPendList.getSelectedValue();
        if (selected == null) return;
        String name = selected.split(" - ")[0];
        boolean ok = admin.aprobarVenue(sistema, name);
        if (ok) {
            persistencia.saveDefault(sistema);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo aprobar el venue.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechazarVenue() {
        String selected = venuesPendList.getSelectedValue();
        if (selected == null) return;
        String name = selected.split(" - ")[0];
        boolean ok = admin.rechazarVenue(sistema, name);
        if (ok) {
            persistencia.saveDefault(sistema);
            refresh();
        }
    }

    private void aprobarReembolso() {
        String selected = reembolsosList.getSelectedValue();
        if (selected == null) return;
        int id = extraerId(selected);
        boolean ok = admin.aprobarReembolso(sistema, id, 3); // reembolso total
        if (ok) {
            persistencia.saveDefault(sistema);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo aprobar el reembolso.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechazarReembolso() {
        String selected = reembolsosList.getSelectedValue();
        if (selected == null) return;
        int id = extraerId(selected);
        boolean ok = admin.rechazarReembolso(sistema, id);
        if (ok) {
            persistencia.saveDefault(sistema);
            refresh();
        }
    }

    private int extraerId(String s) {
        try {
            String num = s.replaceAll("[^0-9]", "");
            return Integer.parseInt(num);
        } catch (Exception e) {
            return -1;
        }
    }
}
