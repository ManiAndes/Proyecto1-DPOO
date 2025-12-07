package dpoo.proyecto.marketplace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

/**
 * Entrada inmutable del log del Marketplace.
 */
public class RegistroReventa {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime fechaHora;
    private final String descripcion;

    public RegistroReventa(String descripcion) {
        this(LocalDateTime.now(), descripcion);
    }

    public RegistroReventa(LocalDateTime fechaHora, String descripcion) {
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
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

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("fechaHora", FORMATTER.format(fechaHora));
        json.put("descripcion", descripcion);
        return json;
    }

    public static RegistroReventa fromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        String descripcion = json.optString("descripcion", "");
        String fechaStr = json.optString("fechaHora", "");
        LocalDateTime fecha = fechaStr.isEmpty()
                ? LocalDateTime.now()
                : LocalDateTime.parse(fechaStr, FORMATTER);
        return new RegistroReventa(fecha, descripcion);
    }
}
