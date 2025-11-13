package dpoo.proyecto.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

public class AuditoriaMarketplaceEntry {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private int id;
    private String timestamp;
    private String actor;
    private String rol;
    private String accion;
    private String recurso;
    private String resultado;
    private String detalle;

    public AuditoriaMarketplaceEntry(int id, String actor, String rol, String accion,
                                     String recurso, String resultado, String detalle) {
        this.id = id;
        this.actor = actor;
        this.rol = rol;
        this.accion = accion;
        this.recurso = recurso;
        this.resultado = resultado;
        this.detalle = detalle;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getActor() {
        return actor;
    }

    public String getRol() {
        return rol;
    }

    public String getAccion() {
        return accion;
    }

    public String getRecurso() {
        return recurso;
    }

    public String getResultado() {
        return resultado;
    }

    public String getDetalle() {
        return detalle;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("timestamp", this.timestamp);
        json.put("actor", this.actor);
        json.put("rol", this.rol);
        json.put("accion", this.accion);
        json.put("recurso", this.recurso);
        json.put("resultado", this.resultado);
        json.put("detalle", this.detalle);
        return json;
    }

    public static AuditoriaMarketplaceEntry fromJSON(JSONObject json) {
        int id = json.optInt("id", 0);
        String actor = json.optString("actor", null);
        String rol = json.optString("rol", null);
        String accion = json.optString("accion", null);
        String recurso = json.optString("recurso", null);
        String resultado = json.optString("resultado", null);
        String detalle = json.optString("detalle", null);
        AuditoriaMarketplaceEntry entry = new AuditoriaMarketplaceEntry(id, actor, rol, accion, recurso, resultado, detalle);
        entry.timestamp = json.optString("timestamp", entry.timestamp);
        return entry;
    }
}
