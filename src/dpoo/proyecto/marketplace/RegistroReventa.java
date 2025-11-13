package dpoo.proyecto.marketplace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entrada inmutable del log del Marketplace.
 */
public class RegistroReventa {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime fechaHora;
    private final String descripcion;

    public RegistroReventa(String descripcion) {
        this.fechaHora = LocalDateTime.now();
        this.descripcion = descripcion != null ? descripcion : "";
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String formatear() {
        return FORMATTER.format(fechaHora) + " - " + descripcion;
    }

    @Override
    public String toString() {
        return formatear();
    }
}
