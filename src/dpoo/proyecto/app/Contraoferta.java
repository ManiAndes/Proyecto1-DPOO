package dpoo.proyecto.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

public class Contraoferta {

    public enum Estado {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA;

        public static Estado fromString(String value) {
            if (value == null) {
                return PENDIENTE;
            }
            try {
                return Estado.valueOf(value);
            } catch (IllegalArgumentException ex) {
                return PENDIENTE;
            }
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private int id;
    private int idOferta;
    private String idComprador;
    private double precioPropuesto;
    private Estado estado;
    private String timestamp;

    public Contraoferta(int id, int idOferta, String idComprador, double precioPropuesto) {
        this.id = id;
        this.idOferta = idOferta;
        this.idComprador = idComprador;
        this.precioPropuesto = precioPropuesto;
        this.estado = Estado.PENDIENTE;
        this.timestamp = nowAsString();
    }

    public int getId() {
        return id;
    }

    public int getIdOferta() {
        return idOferta;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public double getPrecioPropuesto() {
        return precioPropuesto;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void marcarAceptada() {
        this.estado = Estado.ACEPTADA;
        this.timestamp = nowAsString();
    }

    public void marcarRechazada() {
        this.estado = Estado.RECHAZADA;
        this.timestamp = nowAsString();
    }

    private static String nowAsString() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("idOferta", this.idOferta);
        json.put("idComprador", this.idComprador);
        json.put("precioPropuesto", this.precioPropuesto);
        json.put("estado", this.estado.name());
        json.put("timestamp", this.timestamp);
        return json;
    }

    public static Contraoferta fromJSON(JSONObject json) {
        int id = json.optInt("id", 0);
        int idOferta = json.optInt("idOferta", 0);
        String comprador = json.optString("idComprador", null);
        double precio = json.optDouble("precioPropuesto", 0.0);
        Contraoferta contraoferta = new Contraoferta(id, idOferta, comprador, precio);
        contraoferta.estado = Estado.fromString(json.optString("estado", Estado.PENDIENTE.name()));
        contraoferta.timestamp = json.optString("timestamp", contraoferta.timestamp);
        return contraoferta;
    }
}
