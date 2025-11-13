package dpoo.proyecto.marketplace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class OfertaReventa {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int id;
    private final Tiquete tiquete;
    private final Usuario vendedor;
    private final double precioBase;
    private final LocalDateTime fechaCreacion;
    private final List<ContraofertaReventa> contraofertas;

    private boolean activa;
    private Usuario compradorFinal;
    private double precioFinal;
    private String motivoCierre;

    public OfertaReventa(int id, Tiquete tiquete, Usuario vendedor, double precioBase) {
        this.id = id;
        this.tiquete = tiquete;
        this.vendedor = vendedor;
        this.precioBase = precioBase;
        this.fechaCreacion = LocalDateTime.now();
        this.contraofertas = new ArrayList<>();
        this.activa = true;
    }

    public int getId() {
        return id;
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public boolean estaActiva() {
        return activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Usuario getCompradorFinal() {
        return compradorFinal;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public void marcarCerrada(String motivo) {
        this.activa = false;
        this.motivoCierre = motivo;
    }

    public void marcarVendida(Usuario comprador, double precioFinal) {
        this.activa = false;
        this.compradorFinal = comprador;
        this.precioFinal = precioFinal;
        this.motivoCierre = "VENDIDA";
    }

    public void agregarContraoferta(ContraofertaReventa contraoferta) {
        if (contraoferta != null) {
            this.contraofertas.add(contraoferta);
        }
    }

    public List<ContraofertaReventa> getContraofertas() {
        return Collections.unmodifiableList(contraofertas);
    }

    public List<ContraofertaReventa> getContraofertasPendientes() {
        List<ContraofertaReventa> pendientes = new ArrayList<>();
        for (ContraofertaReventa c : contraofertas) {
            if (ContraofertaReventa.PENDIENTE.equals(c.getEstado())) {
                pendientes.add(c);
            }
        }
        return pendientes;
    }

    public String descripcionBasica() {
        Evento evento = tiquete != null ? tiquete.getEvento() : null;
        String eventoNombre = evento != null ? evento.getNombre() : "N/A";
        return "[" + id + "] Tiquete #" + (tiquete != null ? tiquete.getId() : -1)
                + " - Evento: " + eventoNombre
                + " - Precio: " + precioBase
                + " - Vend: " + (vendedor != null ? vendedor.getLogin() : "N/A")
                + " - Creada: " + FORMATTER.format(fechaCreacion)
                + (activa ? " - ACTIVA" : " - INACTIVA");
    }
}
