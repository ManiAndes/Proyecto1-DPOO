package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.marketplace.OfertaReventa;
import dpoo.proyecto.marketplace.RegistroReventa;
import dpoo.proyecto.usuarios.Administrador;

public class AdminPanel extends JPanel {

    private final MasterTicket sistema;
    private final Administrador admin;
    private final Runnable onLogout;
    private final Runnable onChange;

    public AdminPanel(MasterTicket sistema, Administrador admin, Runnable onLogout, Runnable onChange) {
        super(new BorderLayout());
        this.sistema = sistema;
        this.admin = admin;
        this.onLogout = onLogout;
        this.onChange = onChange;

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Administrador: " + admin.getLogin()), BorderLayout.WEST);
        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> onLogout.run());
        header.add(logout, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Configuración", buildConfigTab());
        tabs.addTab("Eventos", buildEventosTab());
        tabs.addTab("Venues", buildVenuesTab());
        tabs.addTab("Reembolsos", buildReembolsosTab());
        tabs.addTab("Organizadores", buildOrganizadoresTab());
        tabs.addTab("Marketplace", buildMarketplaceTab());
        tabs.addTab("Finanzas", buildFinanzasTab());
        return tabs;
    }

    private JPanel buildConfigTab() {
        JTextField costo = new JTextField(String.valueOf(sistema.getCostoPorEmision()));
        JButton guardar = new JButton("Guardar costo emision");
        guardar.addActionListener(e -> {
            try {
                double val = Double.parseDouble(costo.getText());
                sistema.setCostoPorEmision(val);
                JOptionPane.showMessageDialog(this, "Costo por emisión actualizado.");
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido.");
            }
        });
        return UIUtils.formPanel("Configuración global", guardar, new UIUtils.LabeledField("Costo por emisión", costo));
    }

    private JPanel buildEventosTab() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<Evento> model = new DefaultListModel<>();
        sistema.getEventos().values().forEach(model::addElement);
        JList<Evento> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton cancelar = new JButton("Cancelar evento");
        JButton costo = new JButton("Definir cargo % servicio");

        cancelar.addActionListener(e -> {
            Evento ev = list.getSelectedValue();
            if (ev == null) return;
            String[] options = { "1 - Reembolsar sin emision", "2 - Reembolsar precio base", "3 - Reembolso completo" };
            String sel = (String) JOptionPane.showInputDialog(this, "Tipo de reembolso", "Cancelar",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
            if (sel == null) return;
            int tipo = sel.startsWith("1") ? 1 : sel.startsWith("3") ? 3 : 2;
            admin.cancelarEvento(ev, tipo, sistema);
            JOptionPane.showMessageDialog(this, "Evento cancelado y reembolsos procesados.");
            onChange.run();
        });

        costo.addActionListener(e -> {
            Evento ev = list.getSelectedValue();
            if (ev == null) return;
            String val = JOptionPane.showInputDialog(this, "Nuevo cargo porcentual (ej 0.1):",
                    ev.getCargoPorcentualServicio());
            if (val == null) return;
            try {
                double d = Double.parseDouble(val);
                ev.setCargoPorcentualServicio(d);
                JOptionPane.showMessageDialog(this, "Actualizado.");
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido.");
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(costo);
        buttons.add(cancelar);
        panel.add(UIUtils.scroll(list), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildVenuesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        sistema.getVenuesPendientes().keySet().forEach(model::addElement);
        JList<String> list = new JList<>(model);
        JButton aprobar = new JButton("Aprobar");
        JButton rechazar = new JButton("Rechazar");
        aprobar.addActionListener(e -> {
            String v = list.getSelectedValue();
            if (v == null) return;
            boolean ok = admin.aprobarVenue(sistema, v);
            JOptionPane.showMessageDialog(this, ok ? "Aprobado" : "No se pudo aprobar");
            refreshVenues(model);
            onChange.run();
        });
        rechazar.addActionListener(e -> {
            String v = list.getSelectedValue();
            if (v == null) return;
            boolean ok = admin.rechazarVenue(sistema, v);
            JOptionPane.showMessageDialog(this, ok ? "Rechazado" : "No se pudo rechazar");
            refreshVenues(model);
            onChange.run();
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(aprobar);
        buttons.add(rechazar);
        panel.add(UIUtils.scroll(list), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReembolsosTab() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<SolicitudReembolso> model = new DefaultListModel<>();
        for (SolicitudReembolso sr : sistema.listarSolicitudesPendientes()) {
            model.addElement(sr);
        }
        JList<SolicitudReembolso> list = new JList<>(model);
        JButton aprobar = new JButton("Aprobar");
        JButton rechazar = new JButton("Rechazar");
        JComboBox<String> tipo = new JComboBox<>(new String[] { "1 - sin emision", "2 - base", "3 - total" });

        aprobar.addActionListener(e -> {
            SolicitudReembolso sr = list.getSelectedValue();
            if (sr == null) return;
            int t = tipo.getSelectedIndex() == 0 ? 1 : tipo.getSelectedIndex() == 2 ? 3 : 2;
            boolean ok = admin.aprobarReembolso(sistema, sr.getId(), t);
            JOptionPane.showMessageDialog(this, ok ? "Solicitud aprobada" : "No se pudo aprobar");
            refreshReembolsos(model);
            onChange.run();
        });
        rechazar.addActionListener(e -> {
            SolicitudReembolso sr = list.getSelectedValue();
            if (sr == null) return;
            boolean ok = admin.rechazarReembolso(sistema, sr.getId());
            JOptionPane.showMessageDialog(this, ok ? "Solicitud rechazada" : "No se pudo rechazar");
            refreshReembolsos(model);
            onChange.run();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(new JLabel("Tipo:"));
        buttons.add(tipo);
        buttons.add(rechazar);
        buttons.add(aprobar);
        panel.add(UIUtils.scroll(list), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOrganizadoresTab() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        refreshSolicitudesOrg(model);
        JList<String> list = new JList<>(model);
        JButton aprobar = new JButton("Aprobar");
        JButton rechazar = new JButton("Rechazar");
        aprobar.addActionListener(e -> {
            String login = list.getSelectedValue();
            if (login == null) return;
            boolean ok = sistema.aprobarSolicitudOrganizador(login);
            JOptionPane.showMessageDialog(this, ok ? "Organizador aprobado" : "No se pudo aprobar");
            refreshSolicitudesOrg(model);
            onChange.run();
        });
        rechazar.addActionListener(e -> {
            String login = list.getSelectedValue();
            if (login == null) return;
            boolean ok = sistema.rechazarSolicitudOrganizador(login);
            JOptionPane.showMessageDialog(this, ok ? "Solicitud rechazada" : "No se pudo rechazar");
            refreshSolicitudesOrg(model);
            onChange.run();
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(aprobar);
        buttons.add(rechazar);
        panel.add(UIUtils.scroll(list), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMarketplaceTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(buildOfertasAdmin());
        panel.add(buildLogMarketplace());
        return panel;
    }

    private JPanel buildOfertasAdmin() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Ofertas publicadas"));
        DefaultListModel<OfertaReventa> model = new DefaultListModel<>();
        MarketplaceReventa mk = sistema.getMarketplaceReventa();
        mk.listarTodasLasOfertas().forEach(model::addElement);
        JList<OfertaReventa> list = new JList<>(model);
        list.setCellRenderer((l, val, idx, sel, focus) -> {
            JLabel lbl = new JLabel(val.toString());
            if (sel) {
                lbl.setOpaque(true);
                lbl.setBackground(l.getSelectionBackground());
            }
            return lbl;
        });
        JButton eliminar = new JButton("Eliminar oferta");
        eliminar.addActionListener(e -> {
            OfertaReventa o = list.getSelectedValue();
            if (o == null) return;
            String motivo = JOptionPane.showInputDialog(this, "Motivo:");
            try {
                mk.eliminarOfertaComoAdmin(o.getId(), admin, motivo);
                JOptionPane.showMessageDialog(this, "Oferta eliminada.");
                refreshOfertas(model);
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        panel.add(UIUtils.scroll(list), BorderLayout.CENTER);
        panel.add(eliminar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLogMarketplace() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Log marketplace"));
        MarketplaceReventa mk = sistema.getMarketplaceReventa();
        StringBuilder sb = new StringBuilder();
        for (RegistroReventa r : mk.getRegistros()) {
            sb.append(r.formatear()).append("\n");
        }
        javax.swing.JTextArea area = new javax.swing.JTextArea(sb.toString());
        area.setEditable(false);
        panel.add(UIUtils.scroll(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFinanzasTab() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(makeFinanceBlock("Por Fecha", admin.verFinanzasPorFecha(sistema)));
        panel.add(makeFinanceBlock("Por Evento", admin.verFinanzasPorEvento(sistema)));
        panel.add(makeFinanceBlock("Por Organizador", admin.verFinanzasPorOrganizador(sistema)));
        return panel;
    }

    private JPanel makeFinanceBlock(String title, Map<String, Double> data) {
        StringBuilder sb = new StringBuilder();
        double total = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            total += entry.getValue();
        }
        sb.append("TOTAL: ").append(total);
        javax.swing.JTextArea area = new javax.swing.JTextArea(sb.toString());
        area.setEditable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(title));
        panel.add(UIUtils.scroll(area), BorderLayout.CENTER);
        return panel;
    }

    private void refreshVenues(DefaultListModel<String> model) {
        model.clear();
        sistema.getVenuesPendientes().keySet().forEach(model::addElement);
    }

    private void refreshReembolsos(DefaultListModel<SolicitudReembolso> model) {
        model.clear();
        for (SolicitudReembolso sr : sistema.listarSolicitudesPendientes()) {
            model.addElement(sr);
        }
    }

    private void refreshSolicitudesOrg(DefaultListModel<String> model) {
        model.clear();
        for (String key : admin.getSolicitudesOrganizador().keySet()) {
            model.addElement(key);
        }
    }

    private void refreshOfertas(DefaultListModel<OfertaReventa> model) {
        model.clear();
        sistema.getMarketplaceReventa().listarTodasLasOfertas().forEach(model::addElement);
    }
}
