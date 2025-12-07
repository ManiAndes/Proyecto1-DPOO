package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.marketplace.OfertaReventa;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class UserPanel extends JPanel {

    private final MasterTicket sistema;
    private final Usuario usuario;
    private final Runnable onLogout;
    private final Runnable onChange;
    private final JLabel saldoLabel = new JLabel();

    private final DefaultTableModel tiquetesModel = new DefaultTableModel(
            new Object[] { "ID", "Evento", "Estado", "Impreso" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultListModel<Tiquete> elegiblesModel = new DefaultListModel<>();
    private final DefaultListModel<OfertaReventa> ofertasModel = new DefaultListModel<>();

    public UserPanel(MasterTicket sistema, Usuario usuario, Runnable onLogout, Runnable onChange) {
        super(new BorderLayout());
        this.sistema = sistema;
        this.usuario = usuario;
        this.onLogout = onLogout;
        this.onChange = onChange;

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        refreshSaldo();
        reloadTiquetes();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Usuario: " + usuario.getLogin());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Cerrar sesión");
        logoutBtn.addActionListener(e -> onLogout.run());
        right.add(saldoLabel);
        right.add(logoutBtn);
        panel.add(welcome, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Comprar", buildCompraTab());
        tabs.addTab("Mis tiquetes", buildTiquetesTab());
        tabs.addTab("Marketplace", buildMarketplaceTab());
        return tabs;
    }

    private JPanel buildCompraTab() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<Evento> eventosModel = new DefaultListModel<>();
        sistema.getEventos().values().forEach(eventosModel::addElement);
        JList<Evento> eventosList = new JList<>(eventosModel);
        eventosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventosList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel(value.getNombre() + " | " + value.getFecha()
                    + (value.isCancelado() ? " (CANCELADO)" : ""));
            if (isSelected) l.setOpaque(true);
            if (isSelected) l.setBackground(list.getSelectionBackground());
            return l;
        });

        DefaultListModel<Localidad> locModel = new DefaultListModel<>();
        JList<Localidad> localidades = new JList<>(locModel);
        localidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        localidades.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            int disp = value.getTiquetesDisponibles().size();
            JLabel l = new JLabel(value.getNombreLocalidad() + " | $" + value.getPrecioTiquetes() + " | Disp: " + disp);
            if (isSelected) {
                l.setOpaque(true);
                l.setBackground(list.getSelectionBackground());
            }
            return l;
        });

        eventosList.addListSelectionListener(e -> {
            locModel.clear();
            Evento ev = eventosList.getSelectedValue();
            if (ev != null) {
                ev.getLocalidades().values().forEach(locModel::addElement);
            }
        });

        SpinnerNumberModel qtyModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner qty = new JSpinner(qtyModel);
        JButton comprar = new JButton("Comprar");
        comprar.addActionListener(e -> comprar(eventosList.getSelectedValue(), localidades.getSelectedValue(),
                ((Number) qty.getValue()).intValue()));

        JPanel right = new JPanel(new BorderLayout());
        JPanel action = new JPanel(new FlowLayout(FlowLayout.LEFT));
        action.add(new JLabel("Cantidad:"));
        action.add(qty);
        action.add(comprar);
        right.add(action, BorderLayout.NORTH);
        right.add(UIUtils.scroll(localidades), BorderLayout.CENTER);

        panel.add(UIUtils.scroll(eventosList), BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel buildTiquetesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(tiquetesModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton imprimir = new JButton("Imprimir");
        JButton reembolso = new JButton("Solicitar reembolso");
        buttons.add(imprimir);
        buttons.add(reembolso);
        panel.add(buttons, BorderLayout.SOUTH);

        imprimir.addActionListener(e -> {
            Tiquete t = getSelectedTiquete(table);
            if (t == null) return;
            if (t.isImpreso()) {
                JOptionPane.showMessageDialog(this, "Ya fue impreso y no se puede reimprimir.");
                return;
            }
            boolean ok = TicketPrintDialog.imprimir(this, t);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Tiquete impreso y bloqueado para reimpresión/transferencia.");
                reloadTiquetes();
                onChange.run();
            }
        });

        reembolso.addActionListener(e -> {
            Tiquete t = getSelectedTiquete(table);
            if (t == null) return;
            if (t.getEstado().equalsIgnoreCase("REEMBOLSADO") || t.isReembolsado()) {
                JOptionPane.showMessageDialog(this, "El tiquete ya fue reembolsado.");
                return;
            }
            if (tieneSolicitudPendiente(t)) {
                JOptionPane.showMessageDialog(this, "Ya existe una solicitud pendiente para este tiquete.");
                return;
            }
            String motivo = JOptionPane.showInputDialog(this, "Motivo de reembolso:");
            if (motivo == null) return;
            SolicitudReembolso sr = sistema.crearSolicitudReembolso(t, usuario, motivo);
            if (sr != null) {
                JOptionPane.showMessageDialog(this, "Solicitud #" + sr.getId() + " registrada.");
                onChange.run();
            }
        });
        return panel;
    }

    private JPanel buildMarketplaceTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(buildPublicarPanel());
        panel.add(buildOfertasPanel());
        refreshElegiblesMarketplace();
        refreshOfertas();
        return panel;
    }

    private JPanel buildPublicarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Publicar / gestionar mis ofertas"));

        JList<Tiquete> elegibles = new JList<>(elegiblesModel);
        elegibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        elegibles.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String evento = value.getEvento() != null ? value.getEvento().getNombre() : "N/A";
            JLabel l = new JLabel("ID " + value.getId() + " | " + evento + " | " + value.getEstado());
            if (isSelected) {
                l.setOpaque(true);
                l.setBackground(list.getSelectionBackground());
            }
            return l;
        });
        refreshElegiblesMarketplace();

        JTextField precio = new JTextField();
        JButton publicar = new JButton("Publicar");
        JButton cancelar = new JButton("Cancelar oferta seleccionada");

        publicar.addActionListener(e -> {
            Tiquete t = elegibles.getSelectedValue();
            if (t == null) return;
            double p;
            try {
                p = Double.parseDouble(precio.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.");
                return;
            }
            try {
                sistema.getMarketplaceReventa().publicarOferta(usuario, t, p);
                JOptionPane.showMessageDialog(this, "Oferta publicada.");
                refreshElegiblesMarketplace();
                refreshOfertas();
                reloadTiquetes();
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        cancelar.addActionListener(e -> {
            MarketplaceReventa mk = sistema.getMarketplaceReventa();
            List<OfertaReventa> mias = mk.listarOfertasPorUsuario(usuario);
            if (mias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tienes ofertas activas.");
                return;
            }
            String[] options = mias.stream().map(o -> "Oferta #" + o.getId() + " tiquete " + o.getTiquete().getId())
                    .toArray(String[]::new);
            String sel = (String) JOptionPane.showInputDialog(this, "Selecciona oferta a cancelar", "Cancelar",
                    JOptionPane.PLAIN_MESSAGE, null, options, null);
            if (sel == null) return;
            int index = java.util.Arrays.asList(options).indexOf(sel);
            OfertaReventa objetivo = mias.get(index);
            try {
                mk.cancelarOferta(objetivo.getId(), usuario);
                JOptionPane.showMessageDialog(this, "Oferta cancelada.");
                refreshElegiblesMarketplace();
                refreshOfertas();
                reloadTiquetes();
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.add(new JLabel("Precio:"));
        precio.setColumns(8);
        footer.add(precio);
        footer.add(publicar);
        footer.add(cancelar);

        panel.add(UIUtils.scroll(elegibles), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOfertasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Comprar / contraofertar"));
        JList<OfertaReventa> ofertasList = new JList<>(ofertasModel);
        ofertasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ofertasList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel(value.toString());
            if (isSelected) {
                l.setOpaque(true);
                l.setBackground(list.getSelectionBackground());
            }
            return l;
        });
        refreshOfertas();

        JButton comprar = new JButton("Comprar");
        JButton contra = new JButton("Contraoferta");
        JButton refrescar = new JButton("Refrescar");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refrescar);
        actions.add(contra);
        actions.add(comprar);

        refrescar.addActionListener(e -> refreshOfertas());

        comprar.addActionListener(e -> {
            OfertaReventa o = ofertasList.getSelectedValue();
            if (o == null) return;
            try {
                sistema.getMarketplaceReventa().comprarOferta(o.getId(), usuario);
                JOptionPane.showMessageDialog(this, "Compra realizada.");
                refreshOfertas();
                refreshElegiblesMarketplace();
                reloadTiquetes();
                refreshSaldo();
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        contra.addActionListener(e -> {
            OfertaReventa o = ofertasList.getSelectedValue();
            if (o == null) return;
            String montoStr = JOptionPane.showInputDialog(this, "Monto de contraoferta:");
            if (montoStr == null) return;
            double monto;
            try {
                monto = Double.parseDouble(montoStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.");
                return;
            }
            try {
                sistema.getMarketplaceReventa().crearContraoferta(o.getId(), usuario, monto);
                JOptionPane.showMessageDialog(this, "Contraoferta enviada.");
                onChange.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        panel.add(UIUtils.scroll(ofertasList), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSaldo() {
        saldoLabel.setText("Saldo: $" + usuario.getSaldoVirtual());
    }

    private void reloadTiquetes() {
        tiquetesModel.setRowCount(0);
        for (Tiquete t : usuario.getMisTiquetes()) {
            String evento = t.getEvento() != null ? t.getEvento().getNombre() : "N/A";
            tiquetesModel.addRow(new Object[] { t.getId(), evento, t.getEstado(), t.isImpreso() ? "SI" : "NO" });
        }
    }

    private Tiquete getSelectedTiquete(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= usuario.getMisTiquetes().size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un tiquete.");
            return null;
        }
        int id = (Integer) tiquetesModel.getValueAt(row, 0);
        for (Tiquete t : usuario.getMisTiquetes()) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    private void comprar(Evento evento, Localidad localidad, int cantidad) {
        if (evento == null || localidad == null) {
            JOptionPane.showMessageDialog(this, "Seleccione evento y localidad.");
            return;
        }
        if (evento.isCancelado()) {
            JOptionPane.showMessageDialog(this, "El evento está cancelado.");
            return;
        }
        List<Tiquete> disponibles = new ArrayList<>(localidad.getTiquetesDisponibles().values());
        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay tiquetes disponibles en esta localidad.");
            return;
        }
        int max = disponibles.get(0).getMaximoTiquetesPorTransaccion();
        if (cantidad < 1 || cantidad > max || cantidad > disponibles.size()) {
            JOptionPane.showMessageDialog(this, "Cantidad fuera del rango permitido (max " + Math.min(max, disponibles.size()) + ").");
            return;
        }
        List<Tiquete> seleccion = disponibles.subList(0, cantidad);
        double total = calcularTotal(evento, seleccion);
        double saldoOriginal = usuario.getSaldoVirtual();
        double saldoAplicado = Math.min(saldoOriginal, total);
        double pagoExterno = total - saldoAplicado;
        usuario.setSaldoVirtual(saldoOriginal - saldoAplicado);

        registrarVenta(usuario, evento, localidad, seleccion, evento.getCargoPorcentualServicio(),
                sistema.getCostoPorEmision());
        refreshSaldo();
        reloadTiquetes();
        onChange.run();
        JOptionPane.showMessageDialog(this, "Compra exitosa. Pagaste " + total + " (saldo usado: " + saldoAplicado
                + ", externo: " + pagoExterno + ")");
    }

    private double calcularTotal(Evento evento, List<Tiquete> tiquetes) {
        double total = 0.0;
        double servicio = evento.getCargoPorcentualServicio();
        for (Tiquete t : tiquetes) {
            double emisionAplicable = t.getCuotaAdicionalEmision() > 0 ? t.getCuotaAdicionalEmision()
                    : sistema.getCostoPorEmision();
            total += t.calcularPrecioFinal(servicio, emisionAplicable);
        }
        return total;
    }

    private void registrarVenta(Usuario usuario, Evento evento, Localidad localidad, List<Tiquete> tiquetes,
            double servicio, double emisionGlobal) {
        for (Tiquete tiquete : tiquetes) {
            double emisionAplicable = tiquete.getCuotaAdicionalEmision() > 0 ? tiquete.getCuotaAdicionalEmision()
                    : emisionGlobal;
            tiquete.setCuotaAdicionalEmision(emisionAplicable);
            double precioFinal = tiquete.calcularPrecioFinal(servicio, emisionAplicable);
            tiquete.setCliente(usuario);
            tiquete.setMontoPagado(precioFinal);
            tiquete.setEstado("ACTIVO");
            tiquete.setReembolsado(false);
            localidad.marcarVendido(tiquete);
            evento.marcarVendido(tiquete);
            usuario.agregarTiquete(tiquete);
        }
    }

    private boolean tieneSolicitudPendiente(Tiquete tiquete) {
        for (SolicitudReembolso sr : sistema.getSolicitudesReembolso().values()) {
            if (sr.getTiquete() != null && sr.getTiquete().getId() == tiquete.getId()
                    && "PENDIENTE".equalsIgnoreCase(sr.getEstado())) {
                return true;
            }
        }
        return false;
    }

    private void refreshElegiblesMarketplace(DefaultListModel<Tiquete> model) {
        if (model == null) return;
        model.clear();
        for (Tiquete t : tiquetesElegiblesParaMarketplace()) {
            model.addElement(t);
        }
    }

    private void refreshElegiblesMarketplace() {
        refreshElegiblesMarketplace(elegiblesModel);
    }

    private List<Tiquete> tiquetesElegiblesParaMarketplace() {
        List<Tiquete> elegibles = new ArrayList<>();
        for (Tiquete t : usuario.getMisTiquetes()) {
            if (t instanceof PaqueteDeluxe) continue;
            if (t.isUsado() || t.isReembolsado() || t.isImpreso()) continue;
            if (!t.isTransferible()) continue;
            if (usuario.getTiquetesEnReventa().contains(t.getId())) continue;
            elegibles.add(t);
        }
        return elegibles;
    }

    private void refreshOfertas(DefaultListModel<OfertaReventa> model) {
        model.clear();
        MarketplaceReventa mk = sistema.getMarketplaceReventa();
        for (OfertaReventa o : mk.listarOfertasActivas()) {
            if (o.getVendedor() != null && o.getVendedor().equals(usuario)) {
                continue; // no mostrar las propias aquí
            }
            model.addElement(o);
        }
    }

    private void refreshOfertas() {
        refreshOfertas(ofertasModel);
    }
}
