package dpoo.proyecto.marketplace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dpoo.proyecto.usuarios.Usuario;

public class ContraofertaReventa {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ACEPTADA = "ACEPTADA";
    public static final String RECHAZADA = "RECHAZADA";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int id;
    private final Usuario comprador;
    private final double monto;
    private final LocalDateTime fechaCreacion;
    private String estado;

    public ContraofertaReventa(int id, Usuario comprador, double monto) {
        this.id = id;
        this.comprador = comprador;
        this.monto = monto;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = PENDIENTE;
    }

    public int getId() {
        return id;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado != null) {
            this.estado = estado;
        }
    }

    public String descripcionCorta() {
        String compradorLogin = comprador != null ? comprador.getLogin() : "N/A";
        return "[" + id + "] " + compradorLogin + " -> " + monto + " (" + estado + ") " + FORMATTER.format(fechaCreacion);
    }
}
