package dpoo.proyecto.gui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.marketplace.OfertaReventa;
import dpoo.proyecto.marketplace.RegistroReventa;
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
    private DefaultListModel<String> eventosModel = new DefaultListModel<>();
    private DefaultListModel<String> ofertasModel = new DefaultListModel<>();
    private DefaultListModel<String> logModel = new DefaultListModel<>();

    private JList<String> solicitudesOrgList;
    private JList<String> venuesPendList;
    private JList<String> reembolsosList;
    private JList<String> eventosList;
    private JList<String> ofertasList;
    private JList<String> logList;

    private JTextArea finanzasArea;
    private JTextField costoEmisionField;

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
        tabs.addTab("Eventos", buildEventosPanel());
        tabs.addTab("Finanzas", buildFinanzasPanel());
        tabs.addTab("Costo Emisión", buildCostoEmisionPanel());
        tabs.addTab("Organizadores", buildOrganizadoresPanel());
        tabs.addTab("Venues", buildVenuesPanel());
        tabs.addTab("Reembolsos", buildReembolsosPanel());
        tabs.addTab("Marketplace", buildMarketplacePanel());
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

    private JPanel buildEventosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        eventosList = new JList<>(eventosModel);
        panel.add(new JScrollPane(eventosList), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelar = new JButton("Cancelar evento");
        cancelar.addActionListener(e -> cancelarEvento());
        JButton setCargo = new JButton("Actualizar cargo servicio");
        setCargo.addActionListener(e -> actualizarCargoEvento());
        actions.add(cancelar);
        actions.add(setCargo);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFinanzasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton porFecha = new JButton("Por fecha");
        porFecha.addActionListener(e -> mostrarFinanzas("fecha"));
        JButton porEvento = new JButton("Por evento");
        porEvento.addActionListener(e -> mostrarFinanzas("evento"));
        JButton porOrg = new JButton("Por organizador");
        porOrg.addActionListener(e -> mostrarFinanzas("organizador"));
        buttons.add(porFecha);
        buttons.add(porEvento);
        buttons.add(porOrg);
        panel.add(buttons, BorderLayout.NORTH);
        finanzasArea = new JTextArea();
        finanzasArea.setEditable(false);
        panel.add(new JScrollPane(finanzasArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCostoEmisionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Costo global por emisión:"));
        costoEmisionField = new JTextField(String.valueOf(sistema.getCostoPorEmision()), 10);
        panel.add(costoEmisionField);
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> guardarCostoEmision());
        panel.add(guardar);
        return panel;
    }

    private JPanel buildOrganizadoresPanel() {
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
        return orgPanel;
    }

    private JPanel buildVenuesPanel() {
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
        return venuePanel;
    }

    private JPanel buildReembolsosPanel() {
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
        return reembolsoPanel;
    }

    private JPanel buildMarketplacePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        ofertasList = new JList<>(ofertasModel);
        logList = new JList<>(logModel);
        panel.add(new JScrollPane(ofertasList), BorderLayout.CENTER);
        panel.add(new JScrollPane(logList), BorderLayout.EAST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton eliminar = new JButton("Eliminar oferta");
        eliminar.addActionListener(e -> eliminarOfertaAdmin());
        JButton refrescar = new JButton("Refrescar");
        refrescar.addActionListener(e -> cargarMarketplace());
        actions.add(eliminar);
        actions.add(refrescar);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
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

        eventosModel.clear();
        for (Evento e : sistema.getEventos().values()) {
            eventosModel.addElement(e.getNombre() + " - " + e.getFecha() + (e.isCancelado() ? " (CANCELADO)" : ""));
        }

        cargarMarketplace();
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
        String tipoStr = JOptionPane.showInputDialog(this, "Tipo de reembolso (1=sin emisión, 2=precio base, 3=total)", "3");
        int tipo = 3;
        try { tipo = Integer.parseInt(tipoStr); } catch (Exception ignore) {}
        boolean ok = admin.aprobarReembolso(sistema, id, tipo);
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

    private void cancelarEvento() {
        String selected = eventosList.getSelectedValue();
        if (selected == null) return;
        String nombre = selected.split(" - ")[0];
        Evento ev = sistema.getEventos().get(nombre.toUpperCase());
        if (ev == null) {
            JOptionPane.showMessageDialog(this, "Evento no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tipoStr = JOptionPane.showInputDialog(this, "Tipo de reembolso: 1=sin emisión, 2=base, 3=total", "2");
        int tipo = 2;
        try { tipo = Integer.parseInt(tipoStr); } catch (Exception ignore) {}
        admin.cancelarEvento(ev, tipo, sistema);
        persistencia.saveDefault(sistema);
        refresh();
        JOptionPane.showMessageDialog(this, "Evento cancelado y reembolsos procesados.", "OK",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarCargoEvento() {
        String selected = eventosList.getSelectedValue();
        if (selected == null) return;
        String nombre = selected.split(" - ")[0];
        Evento ev = sistema.getEventos().get(nombre.toUpperCase());
        if (ev == null) {
            JOptionPane.showMessageDialog(this, "Evento no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String cargoStr = JOptionPane.showInputDialog(this, "Nuevo cargo porcentual (ej 0.1)", String.valueOf(ev.getCargoPorcentualServicio()));
        try {
            double cargo = Double.parseDouble(cargoStr);
            ev.setCargoPorcentualServicio(cargo);
            persistencia.saveDefault(sistema);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarFinanzas(String tipo) {
        Map<String, Double> resultado = null;
        if ("fecha".equals(tipo)) {
            resultado = admin.verFinanzasPorFecha(sistema);
        } else if ("evento".equals(tipo)) {
            resultado = admin.verFinanzasPorEvento(sistema);
        } else if ("organizador".equals(tipo)) {
            resultado = admin.verFinanzasPorOrganizador(sistema);
        }
        if (resultado == null || resultado.isEmpty()) {
            finanzasArea.setText("Sin datos.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        double total = 0.0;
        for (Map.Entry<String, Double> entry : resultado.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            total += entry.getValue();
        }
        sb.append("TOTAL: ").append(total);
        finanzasArea.setText(sb.toString());
    }

    private void guardarCostoEmision() {
        try {
            double valor = Double.parseDouble(costoEmisionField.getText().trim());
            sistema.setCostoPorEmision(valor);
            persistencia.saveDefault(sistema);
            JOptionPane.showMessageDialog(this, "Costo actualizado.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarMarketplace() {
        ofertasModel.clear();
        logModel.clear();
        MarketplaceReventa mp = sistema.getMarketplaceReventa();
        if (mp == null) return;
        for (OfertaReventa o : mp.listarTodasLasOfertas()) {
            ofertasModel.addElement(o.descripcionBasica());
        }
        for (RegistroReventa r : mp.getRegistros()) {
            logModel.addElement(r.formatear());
        }
    }

    private void eliminarOfertaAdmin() {
        String selected = ofertasList.getSelectedValue();
        if (selected == null) return;
        int id = extraerId(selected);
        try {
            boolean ok = sistema.getMarketplaceReventa().eliminarOfertaComoAdmin(id, admin, "Eliminada por admin");
            if (ok) {
                persistencia.saveDefault(sistema);
                cargarMarketplace();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la oferta.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
