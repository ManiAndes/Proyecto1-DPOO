package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.tiquetes.TiqueteMultipleEntrada;
import dpoo.proyecto.tiquetes.TiqueteMultipleEvento;
import dpoo.proyecto.usuarios.Organizador;

public class OrganizerPanel extends JPanel {

    private final MasterTicket sistema;
    private final Organizador organizador;
    private final Runnable onLogout;
    private final Runnable onChange;

    public OrganizerPanel(MasterTicket sistema, Organizador organizador, Runnable onLogout, Runnable onChange) {
        super(new BorderLayout());
        this.sistema = sistema;
        this.organizador = organizador;
        this.onLogout = onLogout;
        this.onChange = onChange;

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Organizador: " + organizador.getLogin()), BorderLayout.WEST);
        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> onLogout.run());
        header.add(logout, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Venue", buildVenueTab());
        tabs.addTab("Evento", buildEventoTab());
        tabs.addTab("Localidad", buildLocalidadTab());
        tabs.addTab("Paquetes", buildPaquetesTab());
        tabs.addTab("Mis eventos", buildMisEventosTab());
        tabs.addTab("Modo cliente", new UserPanel(sistema, organizador, () -> {
            // Salir del modo cliente vuelve al login general.
            onLogout.run();
        }, onChange));
        return tabs;
    }

    private JPanel buildVenueTab() {
        JTextField nombre = new JTextField();
        JSpinner capacidad = new JSpinner(new SpinnerNumberModel(100, 10, 100000, 10));
        JTextField ubicacion = new JTextField();
        JButton crear = new JButton("Sugerir venue");
        crear.addActionListener(e -> {
            String nom = nombre.getText().trim().toUpperCase();
            String ubic = ubicacion.getText().trim();
            int cap = ((Number) capacidad.getValue()).intValue();
            if (nom.isEmpty() || ubic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete nombre y ubicación.");
                return;
            }
            if (sistema.getVenues().containsKey(nom) || sistema.getVenuesPendientes().containsKey(nom)) {
                JOptionPane.showMessageDialog(this, "Ya existe o está pendiente un venue con ese nombre.");
                return;
            }
            Venue v = new Venue();
            v.setNombre(nom);
            v.setCapacidad(cap);
            v.setUbicacion(ubic);
            v.setOrganizador(organizador);
            sistema.proponerVenue(v);
            JOptionPane.showMessageDialog(this, "Venue sugerido. Pendiente de aprobación.");
            onChange.run();
        });
        return UIUtils.formPanel("Sugerir Venue", crear,
                new UIUtils.LabeledField("Nombre", nombre),
                new UIUtils.LabeledField("Capacidad", capacidad),
                new UIUtils.LabeledField("Ubicación", ubicacion));
    }

    private JPanel buildEventoTab() {
        JTextField nombre = new JTextField();
        JTextField tipo = new JTextField();
        JTextField tipoTiq = new JTextField();
        JTextField fecha = new JTextField();
        JComboBox<String> venues = new JComboBox<>();
        refreshVenues(venues);
        JButton crear = new JButton("Crear evento");
        crear.addActionListener(e -> {
            String nom = nombre.getText().trim().toUpperCase();
            String t = tipo.getText().trim();
            String tt = tipoTiq.getText().trim();
            String f = fecha.getText().trim();
            String venueNom = (String) venues.getSelectedItem();
            if (nom.isEmpty() || venueNom == null) {
                JOptionPane.showMessageDialog(this, "Complete nombre y venue.");
                return;
            }
            if (sistema.getEventos().containsKey(nom)) {
                JOptionPane.showMessageDialog(this, "Ya existe un evento con ese nombre.");
                return;
            }
            Venue v = sistema.getVenues().get(venueNom);
            if (v == null || !v.isAprobado()) {
                JOptionPane.showMessageDialog(this, "El venue no está aprobado.");
                return;
            }
            Evento ev = new Evento(nom, t, tt, 0, v, f);
            ev.setOrganizador(organizador);
            ev.setVenue(v);
            sistema.getEventos().put(nom, ev);
            organizador.addEvento(ev);
            JOptionPane.showMessageDialog(this, "Evento creado. Agregue localidades para emitir tiquetes.");
            onChange.run();
        });
        JPanel form = UIUtils.formPanel("Crear Evento", crear,
                new UIUtils.LabeledField("Nombre", nombre),
                new UIUtils.LabeledField("Tipo", tipo),
                new UIUtils.LabeledField("Tipo Tiquetes", tipoTiq),
                new UIUtils.LabeledField("Fecha (texto)", fecha),
                new UIUtils.LabeledField("Venue aprobado", venues));
        JButton refrescar = new JButton("Refrescar venues");
        refrescar.addActionListener(e -> refreshVenues(venues));
        form.add(refrescar, BorderLayout.NORTH);
        return form;
    }

    private JPanel buildLocalidadTab() {
        JTextField evento = new JTextField();
        JTextField nombreLoc = new JTextField();
        JTextField precio = new JTextField();
        JComboBox<String> numerada = new JComboBox<>(new String[] { "No", "Sí" });
        JSpinner cantidad = new JSpinner(new SpinnerNumberModel(10, 1, 5000, 1));
        JButton crear = new JButton("Crear localidad + emitir tiquetes");
        crear.addActionListener(e -> {
            String evNom = evento.getText().trim().toUpperCase();
            Evento ev = sistema.getEventos().get(evNom);
            if (ev == null || !organizador.getEventos().contains(ev)) {
                JOptionPane.showMessageDialog(this, "Evento no encontrado o no te pertenece.");
                return;
            }
            String locNom = nombreLoc.getText().trim().toUpperCase();
            double valor;
            try {
                valor = Double.parseDouble(precio.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.");
                return;
            }
            boolean esNum = "Sí".equals(numerada.getSelectedItem());
            int cant = ((Number) cantidad.getValue()).intValue();

            Localidad loc = new Localidad(locNom, valor, esNum, ev);
            ev.addLocalidad(loc);
            double emision = sistema.getCostoPorEmision();
            int maxPorTx = 4;
            for (int i = 1; i <= cant; i++) {
                dpoo.proyecto.tiquetes.Tiquete t;
                if (esNum) {
                    t = new dpoo.proyecto.tiquetes.TiqueteNumerado(valor, emision, ev.getFecha(), "20:00", maxPorTx,
                            "NUMERADO", i);
                } else {
                    t = new TiqueteGeneral(valor, emision, ev.getFecha(), "20:00", maxPorTx, "GENERAL");
                }
                t.setEvento(ev);
                t.setLocalidad(loc.getNombreLocalidad());
                t.setId(sistema.siguienteIdTiquete());
                loc.addTiquete(t);
                ev.addTiquete(t);
                sistema.registrarTiquete(t);
            }
            ev.setCantidadTiquetesDisponibles(ev.getCantidadTiquetesDisponibles() + cant);
            JOptionPane.showMessageDialog(this, "Localidad creada y tiquetes emitidos.");
            onChange.run();
        });

        return UIUtils.formPanel("Crear Localidad", crear,
                new UIUtils.LabeledField("Evento (nombre)", evento),
                new UIUtils.LabeledField("Nombre Localidad", nombreLoc),
                new UIUtils.LabeledField("Precio", precio),
                new UIUtils.LabeledField("Numerada", numerada),
                new UIUtils.LabeledField("Cantidad tiquetes", cantidad));
    }

    private JPanel buildPaquetesTab() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(buildPaqueteEntradas());
        panel.add(buildPaqueteEventos());
        panel.add(buildPaqueteDeluxe());
        return panel;
    }

    private JPanel buildPaqueteEntradas() {
        JTextField evento = new JTextField();
        JTextField localidad = new JTextField();
        JTextField precio = new JTextField();
        JSpinner entradas = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        JSpinner max = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        JButton crear = new JButton("Crear paquete entradas");
        crear.addActionListener(e -> {
            Evento ev = sistema.getEventos().get(evento.getText().trim().toUpperCase());
            if (ev == null || !organizador.getEventos().contains(ev)) {
                JOptionPane.showMessageDialog(this, "Evento inválido.");
                return;
            }
            Localidad loc = ev.getLocalidades().get(localidad.getText().trim().toUpperCase());
            if (loc == null) {
                JOptionPane.showMessageDialog(this, "Localidad no encontrada.");
                return;
            }
            double precioTotal;
            try {
                precioTotal = Double.parseDouble(precio.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.");
                return;
            }
            int entradasIncl = ((Number) entradas.getValue()).intValue();
            int maxTx = ((Number) max.getValue()).intValue();
            TiqueteMultipleEntrada paquete = new TiqueteMultipleEntrada(precioTotal, sistema.getCostoPorEmision(),
                    ev.getFecha(), "20:00", maxTx, "PAQUETE_ENTRADAS", ev.getNombre(), loc.getNombreLocalidad(),
                    entradasIncl);
            registrarTiqueteEspecial(paquete, ev, loc);
            JOptionPane.showMessageDialog(this, "Paquete de entradas creado.");
            onChange.run();
        });
        return UIUtils.formPanel("Paquete Entradas", crear,
                new UIUtils.LabeledField("Evento", evento),
                new UIUtils.LabeledField("Localidad", localidad),
                new UIUtils.LabeledField("Precio total", precio),
                new UIUtils.LabeledField("Entradas incluidas", entradas),
                new UIUtils.LabeledField("Máximo por transacción", max));
    }

    private JPanel buildPaqueteEventos() {
        JTextField eventos = new JTextField();
        JTextField precio = new JTextField();
        JSpinner max = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        JButton crear = new JButton("Crear paquete multi-evento");
        crear.addActionListener(e -> {
            String[] nombres = eventos.getText().toUpperCase().split(",");
            List<String> lista = new ArrayList<>();
            Evento principal = null;
            for (String n : nombres) {
                String nom = n.trim();
                if (nom.isEmpty()) continue;
                Evento ev = sistema.getEventos().get(nom);
                if (ev == null || !organizador.getEventos().contains(ev)) {
                    JOptionPane.showMessageDialog(this, "Evento " + nom + " no válido.");
                    return;
                }
                lista.add(ev.getNombre());
                if (principal == null) principal = ev;
            }
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese al menos un evento.");
                return;
            }
            double precioTotal;
            try {
                precioTotal = Double.parseDouble(precio.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.");
                return;
            }
            int maxTx = ((Number) max.getValue()).intValue();
            TiqueteMultipleEvento paquete = new TiqueteMultipleEvento(precioTotal, sistema.getCostoPorEmision(),
                    principal.getFecha(), "20:00", maxTx, "PAQUETE_EVENTOS", lista);
            Localidad loc = obtenerOCrearLocalidadEspecial(principal, "PAQUETES");
            registrarTiqueteEspecial(paquete, principal, loc);
            JOptionPane.showMessageDialog(this, "Paquete multi-evento creado.");
            onChange.run();
        });
        return UIUtils.formPanel("Paquete Multi-Evento", crear,
                new UIUtils.LabeledField("Eventos (coma separada)", eventos),
                new UIUtils.LabeledField("Precio total", precio),
                new UIUtils.LabeledField("Máx por transacción", max));
    }

    private JPanel buildPaqueteDeluxe() {
        JTextField evento = new JTextField();
        JTextField precio = new JTextField();
        JSpinner max = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        JTextField beneficios = new JTextField();
        JTextField cortesias = new JTextField();
        JButton crear = new JButton("Crear paquete Deluxe");
        crear.addActionListener(e -> {
            Evento ev = sistema.getEventos().get(evento.getText().trim().toUpperCase());
            if (ev == null || !organizador.getEventos().contains(ev)) {
                JOptionPane.showMessageDialog(this, "Evento no válido.");
                return;
            }
            double precioTotal;
            try {
                precioTotal = Double.parseDouble(precio.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.");
                return;
            }
            int maxTx = ((Number) max.getValue()).intValue();
            List<String> bens = splitList(beneficios.getText());
            List<String> cors = splitList(cortesias.getText());
            PaqueteDeluxe paquete = new PaqueteDeluxe(precioTotal, sistema.getCostoPorEmision(), ev.getFecha(), "20:00",
                    maxTx, "PAQUETE_DELUXE", "Deluxe " + ev.getNombre(), bens, cors);
            Localidad loc = obtenerOCrearLocalidadEspecial(ev, "DELUXE");
            registrarTiqueteEspecial(paquete, ev, loc);
            JOptionPane.showMessageDialog(this, "Paquete Deluxe creado.");
            onChange.run();
        });
        return UIUtils.formPanel("Paquete Deluxe", crear,
                new UIUtils.LabeledField("Evento", evento),
                new UIUtils.LabeledField("Precio", precio),
                new UIUtils.LabeledField("Máx por transacción", max),
                new UIUtils.LabeledField("Beneficios (coma)", beneficios),
                new UIUtils.LabeledField("Cortesías (coma)", cortesias));
    }

    private JPanel buildMisEventosTab() {
        JPanel panel = new JPanel(new BorderLayout());
        StringBuilder sb = new StringBuilder();
        for (Evento e : organizador.getEventos()) {
            sb.append(e.getNombre()).append(" - ").append(e.getFecha()).append("\n");
        }
        javax.swing.JTextArea area = new javax.swing.JTextArea(sb.toString());
        area.setEditable(false);
        panel.add(UIUtils.scroll(area), BorderLayout.CENTER);
        return panel;
    }

    private void refreshVenues(JComboBox<String> box) {
        box.removeAllItems();
        for (Map.Entry<String, Venue> entry : sistema.getVenues().entrySet()) {
            if (entry.getValue().isAprobado() && entry.getValue().getOrganizador() == organizador) {
                box.addItem(entry.getKey());
            }
        }
    }

    private void registrarTiqueteEspecial(dpoo.proyecto.tiquetes.Tiquete tiquete, Evento evento, Localidad loc) {
        tiquete.setEvento(evento);
        tiquete.setLocalidad(loc.getNombreLocalidad());
        tiquete.setId(sistema.siguienteIdTiquete());
        loc.addTiquete(tiquete);
        evento.addTiquete(tiquete);
        evento.setCantidadTiquetesDisponibles(evento.getCantidadTiquetesDisponibles() + 1);
        sistema.registrarTiquete(tiquete);
    }

    private Localidad obtenerOCrearLocalidadEspecial(Evento evento, String base) {
        String nombre = (base + "_" + evento.getNombre()).toUpperCase();
        Localidad loc = evento.getLocalidades().get(nombre);
        if (loc == null) {
            loc = new Localidad(nombre, 0, false, evento);
            evento.addLocalidad(loc);
        }
        return loc;
    }

    private List<String> splitList(String text) {
        List<String> list = new ArrayList<>();
        for (String part : text.split(",")) {
            if (!part.trim().isEmpty()) list.add(part.trim());
        }
        return list;
    }
}
