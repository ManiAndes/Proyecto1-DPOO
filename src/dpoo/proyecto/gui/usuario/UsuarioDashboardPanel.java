package dpoo.proyecto.gui.usuario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.marketplace.ContraofertaReventa;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.marketplace.OfertaReventa;
import dpoo.proyecto.marketplace.ResultadoCompraMarketplace;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;
import persistencia.CentralPersistencia;

public class UsuarioDashboardPanel extends JPanel {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final Usuario usuario;
    private final Runnable onLogout;

    private DefaultListModel<Evento> eventosModel = new DefaultListModel<>();
    private DefaultListModel<Tiquete> tiquetesModel = new DefaultListModel<>();
    private DefaultListModel<OfertaReventa> ofertasModel = new DefaultListModel<>();
    private DefaultListModel<OfertaReventa> misOfertasModel = new DefaultListModel<>();

    private JList<Evento> eventosList;
    private JList<Tiquete> tiquetesList;
    private JList<OfertaReventa> ofertasList;
    private JList<OfertaReventa> misOfertasList;
    private JComboBox<String> localidadCombo;
    private JSpinner cantidadSpinner;
    private JLabel saldoLabel;

    public UsuarioDashboardPanel(MasterTicket sistema, CentralPersistencia persistencia, Usuario usuario, Runnable onLogout) {
        this.sistema = sistema;
        this.persistencia = persistencia;
        this.usuario = usuario;
        this.onLogout = onLogout;
        initUI();
        refresh();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        saldoLabel = new JLabel();
        add(saldoLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Eventos", buildEventosTab());
        tabs.addTab("Mis tiquetes", buildTiquetesTab());
        tabs.addTab("Marketplace", buildMarketplaceTab());
        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> persistencia.saveDefault(sistema));
        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });
        south.add(guardar);
        south.add(logout);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildEventosTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        eventosList = new JList<>(eventosModel);
        eventosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventosList.addListSelectionListener(e -> actualizarLocalidades());
        eventosList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Evento) {
                    Evento ev = (Evento) value;
                    int disp = ev.getCantidadTiquetesDisponibles();
                    setText(ev.getNombre() + " - " + ev.getFecha()
                            + " | Disponibles: " + disp
                            + (ev.isCancelado() ? " (CANCELADO)" : ""));
                }
                return c;
            }
        });
        panel.add(new JScrollPane(eventosList), BorderLayout.CENTER);

        JPanel compra = new JPanel(new GridBagLayout());
        compra.setBorder(BorderFactory.createTitledBorder("Compra"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        compra.add(new JLabel("Localidad"), gbc);
        gbc.gridx = 1;
        localidadCombo = new JComboBox<>();
        compra.add(localidadCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        compra.add(new JLabel("Cantidad"), gbc);
        gbc.gridx = 1;
        cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        compra.add(cantidadSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton comprar = new JButton("Comprar");
        comprar.addActionListener(e -> comprarSeleccion());
        compra.add(comprar, gbc);

        panel.add(compra, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTiquetesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        tiquetesList = new JList<>(tiquetesModel);
        tiquetesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tiquete) {
                    Tiquete t = (Tiquete) value;
                    String evento = t.getEvento() != null ? t.getEvento().getNombre() : "N/A";
                    setText("#" + t.getId() + " - " + evento + " - " + t.getEstado()
                            + (t.isImpreso() ? " (IMPRESO)" : ""));
                }
                return c;
            }
        });
        panel.add(new JScrollPane(tiquetesList), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton imprimir = new JButton("Imprimir");
        imprimir.addActionListener(e -> imprimirSeleccionado());
        JButton reembolso = new JButton("Solicitar reembolso");
        reembolso.addActionListener(e -> reembolsarSeleccionado());
        acciones.add(imprimir);
        acciones.add(reembolso);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMarketplaceTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        ofertasList = new JList<>(ofertasModel);
        ofertasList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OfertaReventa) {
                    setText(((OfertaReventa) value).descripcionBasica());
                }
                return c;
            }
        });
        misOfertasList = new JList<>(misOfertasModel);
        misOfertasList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OfertaReventa) {
                    setText(((OfertaReventa) value).descripcionBasica());
                }
                return c;
            }
        });

        JPanel lists = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.gridx = 0; gbc.gridy = 0;
        lists.add(new JScrollPane(ofertasList), gbc);
        gbc.gridx = 1;
        lists.add(new JScrollPane(misOfertasList), gbc);
        panel.add(lists, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton publicar = new JButton("Publicar oferta");
        publicar.addActionListener(e -> publicarOferta());
        JButton comprar = new JButton("Comprar");
        comprar.addActionListener(e -> comprarOferta());
        JButton contraofertar = new JButton("Contraoferta");
        contraofertar.addActionListener(e -> contraofertar());
        JButton cancelar = new JButton("Cancelar mi oferta");
        cancelar.addActionListener(e -> cancelarOferta());
        JButton gestionar = new JButton("Gestionar contraofertas recibidas");
        gestionar.addActionListener(e -> gestionarContraofertas());
        acciones.add(publicar);
        acciones.add(comprar);
        acciones.add(contraofertar);
        acciones.add(cancelar);
        acciones.add(gestionar);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    public void refresh() {
        saldoLabel.setText("Saldo: " + usuario.getSaldoVirtual());
        eventosModel.clear();
        for (Evento e : sistema.getEventos().values()) {
            eventosModel.addElement(e);
        }
        actualizarLocalidades();
        tiquetesModel.clear();
        for (Tiquete t : usuario.getMisTiquetes()) {
            tiquetesModel.addElement(t);
        }
        cargarMarketplace();
    }

    private void actualizarLocalidades() {
        localidadCombo.removeAllItems();
        Evento sel = eventosList.getSelectedValue();
        if (sel == null) {
            return;
        }
        for (String nombre : sel.getLocalidades().keySet()) {
            localidadCombo.addItem(nombre);
        }
    }

    private void comprarSeleccion() {
        Evento evento = eventosList.getSelectedValue();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String locNombre = (String) localidadCombo.getSelectedItem();
        if (locNombre == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una localidad.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Localidad localidad = evento.getLocalidades().get(locNombre);
        if (localidad == null) {
            JOptionPane.showMessageDialog(this, "Localidad no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int cantidad = (Integer) cantidadSpinner.getValue();
        List<Tiquete> disponibles = new ArrayList<>(localidad.getTiquetesDisponibles().values());
        if (cantidad > disponibles.size()) {
            JOptionPane.showMessageDialog(this, "No hay suficientes tiquetes disponibles.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<Tiquete> seleccionados = disponibles.subList(0, cantidad);
        double total = calcularTotal(evento, seleccionados);
        double saldoOriginal = usuario.getSaldoVirtual();
        double saldoAplicado = Math.min(saldoOriginal, total);
        double pagoExterno = total - saldoAplicado;
        usuario.setSaldoVirtual(saldoOriginal - saldoAplicado);
        registrarVenta(usuario, evento, localidad, seleccionados, evento.getCargoPorcentualServicio(),
                sistema.getCostoPorEmision());
        saldoLabel.setText("Saldo: " + usuario.getSaldoVirtual());
        persistencia.saveDefault(sistema);
        refresh();
        JOptionPane.showMessageDialog(this,
                "Compra realizada.\nTotal: " + total + "\nSaldo usado: " + saldoAplicado + "\nPago externo: "
                        + pagoExterno,
                "Compra", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calcularTotal(Evento evento, List<Tiquete> tiquetes) {
        double total = 0.0;
        double servicio = evento.getCargoPorcentualServicio();
        for (Tiquete t : tiquetes) {
            double emisionAplicable = t.getCuotaAdicionalEmision() > 0
                    ? t.getCuotaAdicionalEmision()
                    : sistema.getCostoPorEmision();
            total += t.calcularPrecioFinal(servicio, emisionAplicable);
        }
        return total;
    }

    private void registrarVenta(Usuario usuario, Evento evento, Localidad localidad, List<Tiquete> tiquetes,
            double servicio, double emisionGlobal) {
        for (Tiquete t : tiquetes) {
            double emisionAplicable = t.getCuotaAdicionalEmision() > 0 ? t.getCuotaAdicionalEmision() : emisionGlobal;
            t.setCuotaAdicionalEmision(emisionAplicable);
            double precioFinal = t.calcularPrecioFinal(servicio, emisionAplicable);
            t.setCliente(usuario);
            t.setMontoPagado(precioFinal);
            t.setEstado("ACTIVO");
            t.setReembolsado(false);
            t.setImpreso(false);
            t.setTransferible(true);
            t.setFechaImpresion(null);
            localidad.marcarVendido(t);
            evento.marcarVendido(t);
            usuario.agregarTiquete(t);
        }
    }

    private void imprimirSeleccionado() {
        Tiquete t = tiquetesList.getSelectedValue();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tiquete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new ImpresionTiqueteDialog(null, sistema, persistencia, t).setVisible(true);
        refresh();
    }

    private void reembolsarSeleccionado() {
        Tiquete t = tiquetesList.getSelectedValue();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tiquete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (t.isReembolsado() || t.isUsado()) {
            JOptionPane.showMessageDialog(this, "No es elegible para reembolso.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (tieneSolicitudPendiente(t)) {
            JOptionPane.showMessageDialog(this, "Ya tiene solicitud pendiente.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        SolicitudReembolso s = sistema.crearSolicitudReembolso(t, usuario, "Solicitud GUI");
        if (s != null) {
            persistencia.saveDefault(sistema);
            JOptionPane.showMessageDialog(this, "Solicitud #" + s.getId() + " enviada.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la solicitud.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean tieneSolicitudPendiente(Tiquete tiquete) {
        if (tiquete == null) return false;
        for (SolicitudReembolso sr : sistema.getSolicitudesReembolso().values()) {
            if (sr != null && sr.getTiquete() != null && sr.getTiquete().getId() == tiquete.getId()) {
                return true;
            }
        }
        return false;
    }

    private void cargarMarketplace() {
        ofertasModel.clear();
        misOfertasModel.clear();
        MarketplaceReventa mp = sistema.getMarketplaceReventa();
        for (OfertaReventa o : mp.listarOfertasActivas()) {
            ofertasModel.addElement(o);
        }
        for (OfertaReventa o : mp.listarOfertasPorUsuario(usuario)) {
            misOfertasModel.addElement(o);
        }
    }

    private List<Tiquete> tiquetesElegiblesParaMarketplace() {
        List<Tiquete> elegibles = new ArrayList<>();
        for (Tiquete t : usuario.getMisTiquetes()) {
            if (t == null) continue;
            if (usuario.estaTiqueteEnReventa(t.getId())) continue;
            if (t.isUsado() || t.isReembolsado() || t.isImpreso()) continue;
            elegibles.add(t);
        }
        elegibles.sort(Comparator.comparingInt(Tiquete::getId));
        return elegibles;
    }

    private void publicarOferta() {
        List<Tiquete> elegibles = tiquetesElegiblesParaMarketplace();
        if (elegibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes tiquetes elegibles para publicar.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] opciones = elegibles.stream()
                .map(t -> "#" + t.getId() + " - " + (t.getEvento() != null ? t.getEvento().getNombre() : ""))
                .toArray(String[]::new);
        String seleccion = (String) JOptionPane.showInputDialog(this, "Elige tiquete", "Publicar",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return;
        int id = Integer.parseInt(seleccion.replaceAll("[^0-9]", ""));
        Tiquete elegido = elegibles.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (elegido == null) return;
        String precioStr = JOptionPane.showInputDialog(this, "Precio de venta");
        if (precioStr == null) return;
        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            sistema.getMarketplaceReventa().publicarOferta(usuario, elegido, precio);
            persistencia.saveDefault(sistema);
            cargarMarketplace();
            refresh();
            JOptionPane.showMessageDialog(this, "Oferta publicada.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void comprarOferta() {
        OfertaReventa sel = ofertasList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una oferta.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            ResultadoCompraMarketplace r = sistema.getMarketplaceReventa().comprarOferta(sel.getId(), usuario);
            persistencia.saveDefault(sistema);
            cargarMarketplace();
            refresh();
            JOptionPane.showMessageDialog(this, r.getMensaje(), "Compra", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void contraofertar() {
        OfertaReventa sel = ofertasList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una oferta.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String montoStr = JOptionPane.showInputDialog(this, "Valor de contraoferta");
        if (montoStr == null) return;
        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            sistema.getMarketplaceReventa().crearContraoferta(sel.getId(), usuario, monto);
            persistencia.saveDefault(sistema);
            JOptionPane.showMessageDialog(this, "Contraoferta registrada.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarOferta() {
        OfertaReventa sel = misOfertasList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione su oferta.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            sistema.getMarketplaceReventa().cancelarOferta(sel.getId(), usuario);
            persistencia.saveDefault(sistema);
            cargarMarketplace();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gestionarContraofertas() {
        OfertaReventa sel = misOfertasList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione su oferta.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<ContraofertaReventa> pendientes = sel.getContraofertasPendientes();
        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay contraofertas pendientes.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] opciones = pendientes.stream()
                .map(c -> "#" + c.getId() + " - " + c.getComprador().getLogin() + " $" + c.getMonto())
                .toArray(String[]::new);
        String seleccion = (String) JOptionPane.showInputDialog(this, "Elige contraoferta",
                "Contraofertas", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return;
        int idx = -1;
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(seleccion)) {
                idx = i;
                break;
            }
        }
        if (idx < 0 || idx >= pendientes.size()) {
            JOptionPane.showMessageDialog(this, "Selección inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int contraId = pendientes.get(idx).getId();
        String accion = JOptionPane.showInputDialog(this, "Aceptar (a) / Rechazar (r)");
        if (accion == null) return;
        try {
            if ("a".equalsIgnoreCase(accion)) {
                ResultadoCompraMarketplace r = sistema.getMarketplaceReventa()
                        .aceptarContraoferta(sel.getId(), contraId, usuario);
                JOptionPane.showMessageDialog(this, r.getMensaje(), "Venta", JOptionPane.INFORMATION_MESSAGE);
            } else if ("r".equalsIgnoreCase(accion)) {
                sistema.getMarketplaceReventa().rechazarContraoferta(sel.getId(), contraId, usuario);
            }
            persistencia.saveDefault(sistema);
            cargarMarketplace();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String toString() {
        return "UsuarioDashboardPanel";
    }
}
