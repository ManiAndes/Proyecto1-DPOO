package dpoo.proyecto.gui.organizador;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
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
import javax.swing.border.EmptyBorder;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.gui.usuario.UsuarioDashboardPanel;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.tiquetes.TiqueteMultipleEntrada;
import dpoo.proyecto.tiquetes.TiqueteMultipleEvento;
import dpoo.proyecto.usuarios.Organizador;
import persistencia.CentralPersistencia;

public class OrganizadorDashboardPanel extends JPanel {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final Organizador organizador;
    private final Runnable onLogout;

    private DefaultListModel<String> eventosModel = new DefaultListModel<>();
    private DefaultListModel<String> venuesModel = new DefaultListModel<>();
    private JTextArea eventosArea;

    private JList<String> eventosList;
    private JList<String> venuesList;

    private JTextField nombreVenueField;
    private JTextField ubicacionField;
    private JTextField capacidadField;
    private JTextField nombreEventoField;
    private JTextField fechaEventoField;
    private JTextField tipoEventoField;
    private JTextField tipoTiqueteField;
    private JTextField cargoServicioField;
    private JTextField nombreLocalidadField;
    private JTextField precioLocalidadField;
    private JTextField cantidadTiquetesField;
    private JTextField descuentoField;

    public OrganizadorDashboardPanel(MasterTicket sistema, CentralPersistencia persistencia, Organizador organizador,
            Runnable onLogout) {
        this.sistema = sistema;
        this.persistencia = persistencia;
        this.organizador = organizador;
        this.onLogout = onLogout;
        initUI();
        refresh();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Eventos/Localidades", buildEventosPanel());
        tabs.addTab("Paquetes", buildPaquetesPanel());
        tabs.addTab("Venues", buildVenuesPanel());
        tabs.addTab("Modo Cliente", buildClientePanel());
        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> persistencia.saveDefault(sistema));
        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });
        bottom.add(guardar);
        bottom.add(logout);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildEventosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel lists = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;

        eventosList = new JList<>(eventosModel);
        venuesList = new JList<>(venuesModel);
        gbc.gridx = 0; gbc.gridy = 0;
        lists.add(new JScrollPane(eventosList), gbc);
        gbc.gridx = 1;
        lists.add(new JScrollPane(venuesList), gbc);
        panel.add(lists, BorderLayout.CENTER);

        JPanel forms = new JPanel(new GridBagLayout());
        GridBagConstraints fg = new GridBagConstraints();
        fg.insets = new Insets(4, 4, 4, 4);
        fg.fill = GridBagConstraints.HORIZONTAL;

        fg.gridx = 0; fg.gridy = 0; fg.gridwidth = 2;
        forms.add(new JLabel("Crear evento (requiere venue aprobado)"), fg);
        fg.gridwidth = 1;
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Nombre"), fg);
        fg.gridx = 1; nombreEventoField = new JTextField(12); forms.add(nombreEventoField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Fecha (YYYY-MM-DD)"), fg);
        fg.gridx = 1; fechaEventoField = new JTextField(12); forms.add(fechaEventoField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Tipo evento"), fg);
        fg.gridx = 1; tipoEventoField = new JTextField(12); forms.add(tipoEventoField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Tipo tiquetes"), fg);
        fg.gridx = 1; tipoTiqueteField = new JTextField(12); forms.add(tipoTiqueteField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Cargo servicio (0-1)"), fg);
        fg.gridx = 1; cargoServicioField = new JTextField("0.0", 8); forms.add(cargoServicioField, fg);
        fg.gridy++; fg.gridx = 0; fg.gridwidth = 2;
        JButton crearEvt = new JButton("Crear evento");
        crearEvt.addActionListener(e -> crearEvento());
        forms.add(crearEvt, fg);

        fg.gridy++; fg.gridwidth = 2;
        forms.add(new JLabel("Localidad y emisión de tiquetes"), fg);
        fg.gridwidth = 1;
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Localidad"), fg);
        fg.gridx = 1; nombreLocalidadField = new JTextField(12); forms.add(nombreLocalidadField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Precio"), fg);
        fg.gridx = 1; precioLocalidadField = new JTextField(10); forms.add(precioLocalidadField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Cantidad tiquetes"), fg);
        fg.gridx = 1; cantidadTiquetesField = new JTextField(6); forms.add(cantidadTiquetesField, fg);
        fg.gridy++; fg.gridx = 0; forms.add(new JLabel("Descuento (%)"), fg);
        fg.gridx = 1; descuentoField = new JTextField("0", 6); forms.add(descuentoField, fg);
        fg.gridy++; fg.gridx = 0; fg.gridwidth = 2;
        JButton agregarLoc = new JButton("Agregar localidad + tiquetes");
        agregarLoc.addActionListener(e -> agregarLocalidadYTiquetes());
        forms.add(agregarLoc, fg);

        fg.gridy++; fg.gridwidth = 2;
        JButton aplicarDesc = new JButton("Aplicar descuento localidad");
        aplicarDesc.addActionListener(e -> aplicarDescuento());
        forms.add(aplicarDesc, fg);

        fg.gridy++; fg.gridwidth = 2;
        forms.add(new JLabel("Mis eventos"), fg);
        fg.gridy++;
        eventosArea = new JTextArea(6, 40);
        eventosArea.setEditable(false);
        forms.add(new JScrollPane(eventosArea), fg);

        panel.add(forms, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildVenuesPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints fg = new GridBagConstraints();
        fg.insets = new Insets(4, 4, 4, 4);
        fg.fill = GridBagConstraints.HORIZONTAL;
        fg.gridx = 0; fg.gridy = 0;
        form.add(new JLabel("Proponer nuevo venue"), fg);
        fg.gridy++; form.add(new JLabel("Nombre"), fg);
        fg.gridx = 1; nombreVenueField = new JTextField(14); form.add(nombreVenueField, fg);
        fg.gridx = 0; fg.gridy++; form.add(new JLabel("Ubicación"), fg);
        fg.gridx = 1; ubicacionField = new JTextField(14); form.add(ubicacionField, fg);
        fg.gridx = 0; fg.gridy++; form.add(new JLabel("Capacidad"), fg);
        fg.gridx = 1; capacidadField = new JTextField(8); form.add(capacidadField, fg);
        fg.gridx = 0; fg.gridy++; fg.gridwidth = 2;
        JButton proponer = new JButton("Proponer venue");
        proponer.addActionListener(e -> proponerVenue());
        form.add(proponer, fg);
        return form;
    }

    private JPanel buildPaquetesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Paquetes"), gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        JButton packEntradas = new JButton("Paquete de Entradas");
        packEntradas.addActionListener(e -> crearPaqueteEntradas());
        panel.add(packEntradas, gbc);

        gbc.gridx = 1;
        JButton packEventos = new JButton("Paquete Multi-Evento");
        packEventos.addActionListener(e -> crearPaqueteEventos());
        panel.add(packEventos, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton packDeluxe = new JButton("Paquete Deluxe");
        packDeluxe.addActionListener(e -> crearPaqueteDeluxe());
        panel.add(packDeluxe, gbc);

        return panel;
    }

    private JPanel buildClientePanel() {
        // Reusa el panel de usuario para modo cliente
        JPanel contenedor = new JPanel(new BorderLayout());
        UsuarioDashboardPanel cliente = new UsuarioDashboardPanel(sistema, persistencia, organizador, null);
        contenedor.add(cliente, BorderLayout.CENTER);
        return contenedor;
    }

    public void refresh() {
        eventosModel.clear();
        venuesModel.clear();
        List<Evento> eventosOrg = new ArrayList<>();
        for (Evento e : sistema.getEventos().values()) {
            if (e.getOrganizador() != null && organizador.getLogin().equals(e.getOrganizador().getLogin())) {
                eventosOrg.add(e);
                eventosModel.addElement(e.getNombre() + " - " + e.getFecha() + " (" + e.getCantidadTiquetesDisponibles() + ")");
            }
        }
        for (Map.Entry<String, Venue> entry : sistema.getVenues().entrySet()) {
            Venue v = entry.getValue();
            if (v != null && v.getOrganizador() != null
                    && organizador.getLogin().equals(v.getOrganizador().getLogin())) {
                String estado = v.isAprobado() ? "APROBADO" : "PENDIENTE";
                venuesModel.addElement(entry.getKey() + " (" + estado + ")");
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Evento e : eventosOrg) {
            sb.append(e.getNombre()).append(" - ").append(e.getFecha())
                    .append(" | Localidades: ").append(e.getLocalidades().keySet()).append("\n");
        }
        eventosArea.setText(sb.toString());
    }

    private Evento eventoSeleccionado() {
        String val = eventosList.getSelectedValue();
        if (val == null) return null;
        String nombre = val.split(" - ")[0];
        return sistema.getEventos().get(nombre.toUpperCase());
    }

    private void proponerVenue() {
        String nombre = nombreVenueField.getText().trim();
        String ubicacion = ubicacionField.getText().trim();
        String capStr = capacidadField.getText().trim();
        if (nombre.isEmpty() || ubicacion.isEmpty() || capStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int capacidad;
        try {
            capacidad = Integer.parseInt(capStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidad inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Venue v = new Venue();
        v.setNombre(nombre.toUpperCase());
        v.setUbicacion(ubicacion);
        v.setCapacidad(capacidad);
        v.setOrganizador(organizador);
        v.setAprobado(false);
        sistema.proponerVenue(v);
        persistencia.saveDefault(sistema);
        refresh();
        JOptionPane.showMessageDialog(this, "Venue propuesto. Pendiente de aprobación.", "OK",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void crearEvento() {
        String nombre = nombreEventoField.getText().trim();
        String fecha = fechaEventoField.getText().trim();
        String tipo = tipoEventoField.getText().trim();
        String tipoTiq = tipoTiqueteField.getText().trim();
        double cargo = 0.0;
        try { cargo = Double.parseDouble(cargoServicioField.getText().trim()); } catch (Exception ignore) {}
        if (nombre.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete nombre y fecha.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (sistema.getEventos().containsKey(nombre.toUpperCase())) {
            JOptionPane.showMessageDialog(this, "Ya existe un evento con ese nombre.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Venue venue = null;
        for (Venue v : sistema.getVenues().values()) {
            if (v != null && v.isAprobado() && v.getOrganizador() != null
                    && organizador.getLogin().equals(v.getOrganizador().getLogin())) {
                venue = v;
                break;
            }
        }
        if (venue == null) {
            JOptionPane.showMessageDialog(this, "Necesitas un venue aprobado para crear evento.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Evento evento = new Evento(nombre.toUpperCase(), tipo.toUpperCase(), tipoTiq.toUpperCase(), 0, venue, fecha);
        evento.setOrganizador(organizador);
        evento.setCargoPorcentualServicio(cargo);
        venue.addEvento(evento);
        organizador.addEvento(evento);
        sistema.getEventos().put(evento.getNombre(), evento);
        persistencia.saveDefault(sistema);
        refresh();
        JOptionPane.showMessageDialog(this, "Evento creado.", "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarLocalidadYTiquetes() {
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nombreLoc = nombreLocalidadField.getText().trim();
        String precioStr = precioLocalidadField.getText().trim();
        String cantStr = cantidadTiquetesField.getText().trim();
        if (nombreLoc.isEmpty() || precioStr.isEmpty() || cantStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete localidad, precio y cantidad.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double precio; int cantidad;
        try {
            precio = Double.parseDouble(precioStr);
            cantidad = Integer.parseInt(cantStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o cantidad inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Localidad localidad = evento.getLocalidades().get(nombreLoc.toUpperCase());
        if (localidad == null) {
            localidad = new Localidad(nombreLoc.toUpperCase(), precio, false, evento);
            evento.addLocalidad(localidad);
        } else {
            localidad.setPrecioTiquetes(precio);
        }
        for (int i = 0; i < cantidad; i++) {
            TiqueteGeneral t = new TiqueteGeneral(precio, sistema.getCostoPorEmision(), evento.getFecha(), "20:00", 6,
                    localidad.getNombreLocalidad());
            int id = sistema.siguienteIdTiquete();
            t.setId(id);
            t.setEvento(evento);
            t.setLocalidad(localidad.getNombreLocalidad());
            localidad.addTiquete(t);
            evento.addTiquete(t);
            sistema.registrarTiquete(t);
        }
        evento.setCantidadTiquetesDisponibles(evento.getTiquetes().size());
        persistencia.saveDefault(sistema);
        refresh();
        JOptionPane.showMessageDialog(this, "Localidad/tiquetes añadidos.", "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aplicarDescuento() {
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nombreLoc = nombreLocalidadField.getText().trim();
        if (nombreLoc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Especifique la localidad.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Localidad loc = evento.getLocalidades().get(nombreLoc.toUpperCase());
        if (loc == null) {
            JOptionPane.showMessageDialog(this, "Localidad no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double pct = Double.parseDouble(descuentoField.getText().trim());
            loc.setDescuento(pct);
            persistencia.saveDefault(sistema);
            JOptionPane.showMessageDialog(this, "Descuento aplicado.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Descuento inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPaqueteEntradas() {
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String localidad = JOptionPane.showInputDialog(this, "Localidad asociada");
        if (localidad == null) return;
        Localidad loc = evento.getLocalidades().get(localidad.toUpperCase());
        if (loc == null) {
            JOptionPane.showMessageDialog(this, "Localidad no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int entradas = Integer.parseInt(JOptionPane.showInputDialog(this, "Cantidad de entradas en paquete"));
            double precio = Double.parseDouble(JOptionPane.showInputDialog(this, "Precio total del paquete"));
            int maximo = Integer.parseInt(JOptionPane.showInputDialog(this, "Máximo por transacción"));
            TiqueteMultipleEntrada paquete = new TiqueteMultipleEntrada(precio, sistema.getCostoPorEmision(),
                    evento.getFecha(), "20:00", maximo, "PAQUETE_ENTRADAS", evento.getNombre(), localidad.toUpperCase(), entradas);
            completarRegistroTiquete(paquete, evento, loc);
            persistencia.saveDefault(sistema);
            refresh();
            JOptionPane.showMessageDialog(this, "Paquete de entradas creado.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPaqueteEventos() {
        String countStr = JOptionPane.showInputDialog(this, "¿Cuántos eventos componen el paquete?");
        if (countStr == null) return;
        int cantidad;
        try { cantidad = Integer.parseInt(countStr); } catch (Exception e) { return; }
        if (cantidad < 1) return;
        List<String> eventosIncluidos = new ArrayList<>();
        Evento eventoPrincipal = null;
        for (int i = 0; i < cantidad; i++) {
            String nombre = JOptionPane.showInputDialog(this, "Evento #" + (i + 1));
            if (nombre == null) return;
            Evento ev = sistema.getEventos().get(nombre.toUpperCase());
            if (ev == null || ev.getOrganizador() == null
                    || !organizador.getLogin().equals(ev.getOrganizador().getLogin())) {
                JOptionPane.showMessageDialog(this, "Evento inválido o no propio: " + nombre, "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            eventosIncluidos.add(ev.getNombre());
            if (eventoPrincipal == null) eventoPrincipal = ev;
        }
        try {
            double precio = Double.parseDouble(JOptionPane.showInputDialog(this, "Precio total del paquete"));
            int maximo = Integer.parseInt(JOptionPane.showInputDialog(this, "Máximo por transacción"));
            TiqueteMultipleEvento paquete = new TiqueteMultipleEvento(precio, sistema.getCostoPorEmision(),
                    eventoPrincipal.getFecha(), "20:00", maximo, "PAQUETE_EVENTOS", eventosIncluidos);
            Localidad loc = obtenerOCrearLocalidadEspecial(eventoPrincipal, "PAQUETES");
            completarRegistroTiquete(paquete, eventoPrincipal, loc);
            persistencia.saveDefault(sistema);
            refresh();
            JOptionPane.showMessageDialog(this, "Paquete multi-evento creado.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPaqueteDeluxe() {
        Evento evento = eventoSeleccionado();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            double precio = Double.parseDouble(JOptionPane.showInputDialog(this, "Precio del Deluxe"));
            int maximo = Integer.parseInt(JOptionPane.showInputDialog(this, "Máximo por transacción"));
            int benef = Integer.parseInt(JOptionPane.showInputDialog(this, "Número de beneficios"));
            List<String> beneficios = new ArrayList<>();
            for (int i = 0; i < benef; i++) {
                beneficios.add(JOptionPane.showInputDialog(this, "Beneficio #" + (i + 1)));
            }
            int cortCount = Integer.parseInt(JOptionPane.showInputDialog(this, "Número de cortesías"));
            List<String> cortesias = new ArrayList<>();
            for (int i = 0; i < cortCount; i++) {
                cortesias.add(JOptionPane.showInputDialog(this, "Cortesía #" + (i + 1)));
            }
            PaqueteDeluxe paquete = new PaqueteDeluxe(precio, sistema.getCostoPorEmision(),
                    evento.getFecha(), "20:00", maximo, "PAQUETE_DELUXE", "Deluxe " + evento.getNombre(),
                    beneficios, cortesias);
            Localidad loc = obtenerOCrearLocalidadEspecial(evento, "DELUXE");
            completarRegistroTiquete(paquete, evento, loc);
            persistencia.saveDefault(sistema);
            refresh();
            JOptionPane.showMessageDialog(this, "Paquete Deluxe creado.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completarRegistroTiquete(Tiquete tiquete, Evento evento, Localidad localidad) {
        if (tiquete == null || evento == null || localidad == null) return;
        tiquete.setEvento(evento);
        tiquete.setLocalidad(localidad.getNombreLocalidad());
        tiquete.setId(sistema.siguienteIdTiquete());
        localidad.addTiquete(tiquete);
        evento.addTiquete(tiquete);
        evento.setCantidadTiquetesDisponibles(evento.getCantidadTiquetesDisponibles() + 1);
        sistema.registrarTiquete(tiquete);
    }

    private Localidad obtenerOCrearLocalidadEspecial(Evento evento, String nombreBase) {
        String nombre = (nombreBase + "_" + evento.getNombre()).toUpperCase();
        Localidad localidad = evento.getLocalidades().get(nombre);
        if (localidad == null) {
            localidad = new Localidad(nombre, 0, false, evento);
            evento.addLocalidad(localidad);
        }
        return localidad;
    }
}
