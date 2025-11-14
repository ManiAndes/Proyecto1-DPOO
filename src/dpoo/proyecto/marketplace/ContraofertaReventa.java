package dpoo.proyecto.marketplace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

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
        this(id, comprador, monto, LocalDateTime.now(), PENDIENTE);
    }

    public ContraofertaReventa(int id, Usuario comprador, double monto, LocalDateTime fechaCreacion, String estado) {
        this.id = id;
        this.comprador = comprador;
        this.monto = monto;
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
        this.estado = estado != null ? estado : PENDIENTE;
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

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("monto", monto);
        json.put("fechaCreacion", FORMATTER.format(fechaCreacion));
        json.put("estado", estado);
        if (comprador != null) {
            json.put("compradorLogin", comprador.getLogin());
        }
        return json;
    }

    public static ContraofertaReventa fromJSON(JSONObject json, Usuario comprador) {
        if (json == null) {
            return null;
        }
        int id = json.optInt("id", 0);
        double monto = json.optDouble("monto", 0.0);
        String fechaStr = json.optString("fechaCreacion", "");
        LocalDateTime fecha = fechaStr.isEmpty()
                ? LocalDateTime.now()
                : LocalDateTime.parse(fechaStr, FORMATTER);
        String estado = json.optString("estado", PENDIENTE);
        return new ContraofertaReventa(id, comprador, monto, fecha, estado);
    }
}
