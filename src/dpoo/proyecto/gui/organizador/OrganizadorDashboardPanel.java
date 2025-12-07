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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.usuarios.Organizador;
import persistencia.CentralPersistencia;

public class OrganizadorDashboardPanel extends JPanel {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final Organizador organizador;
    private final Runnable onLogout;

    private DefaultListModel<String> eventosModel = new DefaultListModel<>();
    private DefaultListModel<String> venuesModel = new DefaultListModel<>();

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
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Organizador: " + organizador.getLogin()));
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Eventos
        gbc.gridx = 0;
        gbc.gridy = 0;
        eventosList = new JList<>(eventosModel);
        center.add(new JScrollPane(eventosList), gbc);

        // Venues
        gbc.gridx = 1;
        venuesList = new JList<>(venuesModel);
        center.add(new JScrollPane(venuesList), gbc);

        add(center, BorderLayout.CENTER);

        // Formularios inferiores
        JPanel forms = new JPanel(new GridBagLayout());
        forms.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints fg = new GridBagConstraints();
        fg.insets = new Insets(4, 4, 4, 4);
        fg.fill = GridBagConstraints.HORIZONTAL;
        fg.gridx = 0; fg.gridy = 0;
        fg.gridwidth = 2;
        forms.add(new JLabel("Nuevo evento"), fg);
        fg.gridwidth = 1;
        fg.gridy++;
        forms.add(new JLabel("Nombre"), fg);
        fg.gridx = 1;
        nombreEventoField = new JTextField(14);
        forms.add(nombreEventoField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Fecha (YYYY-MM-DD)"), fg);
        fg.gridx = 1;
        fechaEventoField = new JTextField(12);
        forms.add(fechaEventoField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Tipo evento"), fg);
        fg.gridx = 1;
        tipoEventoField = new JTextField(12);
        forms.add(tipoEventoField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Tipo tiquetes"), fg);
        fg.gridx = 1;
        tipoTiqueteField = new JTextField(12);
        forms.add(tipoTiqueteField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Cargo servicio (0-1)"), fg);
        fg.gridx = 1;
        cargoServicioField = new JTextField(8);
        forms.add(cargoServicioField, fg);
        fg.gridx = 0; fg.gridy++;
        fg.gridwidth = 2;
        JButton crearEventoBtn = new JButton("Crear evento");
        crearEventoBtn.addActionListener(e -> crearEvento());
        forms.add(crearEventoBtn, fg);

        fg.gridy++;
        fg.gridwidth = 2;
        forms.add(new JLabel("Agregar localidad y tiquetes al evento seleccionado"), fg);
        fg.gridwidth = 1;
        fg.gridy++;
        forms.add(new JLabel("Localidad"), fg);
        fg.gridx = 1;
        nombreLocalidadField = new JTextField(12);
        forms.add(nombreLocalidadField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Precio"), fg);
        fg.gridx = 1;
        precioLocalidadField = new JTextField(10);
        forms.add(precioLocalidadField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Cantidad tiquetes"), fg);
        fg.gridx = 1;
        cantidadTiquetesField = new JTextField(6);
        forms.add(cantidadTiquetesField, fg);
        fg.gridx = 0; fg.gridy++;
        fg.gridwidth = 2;
        JButton agregarLocBtn = new JButton("Agregar localidad + tiquetes");
        agregarLocBtn.addActionListener(e -> agregarLocalidadYTiquetes());
        forms.add(agregarLocBtn, fg);

        fg.gridy++;
        fg.gridwidth = 2;
        forms.add(new JLabel("Proponer nuevo venue"), fg);
        fg.gridwidth = 1;
        fg.gridy++;
        forms.add(new JLabel("Nombre venue"), fg);
        fg.gridx = 1;
        nombreVenueField = new JTextField(14);
        forms.add(nombreVenueField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Ubicación"), fg);
        fg.gridx = 1;
        ubicacionField = new JTextField(14);
        forms.add(ubicacionField, fg);
        fg.gridx = 0; fg.gridy++;
        forms.add(new JLabel("Capacidad"), fg);
        fg.gridx = 1;
        capacidadField = new JTextField(8);
        forms.add(capacidadField, fg);
        fg.gridx = 0; fg.gridy++;
        fg.gridwidth = 2;
        JButton proponer = new JButton("Proponer venue");
        proponer.addActionListener(e -> proponerVenue());
        forms.add(proponer, fg);

        add(forms, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> persistencia.saveDefault(sistema));
        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });
        bottom.add(guardar);
        bottom.add(logout);
        add(bottom, BorderLayout.PAGE_END);
    }

    public void refresh() {
        eventosModel.clear();
        List<Evento> eventosOrg = new ArrayList<>();
        for (Evento e : sistema.getEventos().values()) {
            if (e.getOrganizador() != null && organizador.getLogin().equals(e.getOrganizador().getLogin())) {
                eventosOrg.add(e);
            }
        }
        for (Evento e : eventosOrg) {
            eventosModel.addElement(e.getNombre() + " - " + e.getFecha());
        }

        venuesModel.clear();
        for (Map.Entry<String, Venue> entry : sistema.getVenues().entrySet()) {
            Venue v = entry.getValue();
            if (v != null && v.getOrganizador() != null
                    && organizador.getLogin().equals(v.getOrganizador().getLogin())) {
                String estado = v.isAprobado() ? "APROBADO" : "PENDIENTE";
                venuesModel.addElement(entry.getKey() + " (" + estado + ")");
            }
        }
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
        v.setNombre(nombre);
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
        try {
            cargo = Double.parseDouble(cargoServicioField.getText().trim());
        } catch (Exception e) {
            // default 0
        }
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
        // si el organizador tiene algún venue aprobado, toma el primero
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
        String eventoSel = eventosList.getSelectedValue();
        if (eventoSel == null) {
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
        double precio;
        int cantidad;
        try {
            precio = Double.parseDouble(precioStr);
            cantidad = Integer.parseInt(cantStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o cantidad inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Evento evento = sistema.getEventos().get(eventoSel.split(" - ")[0].toUpperCase());
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Evento no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
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
}
