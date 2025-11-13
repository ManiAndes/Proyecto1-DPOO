package dpoo.proyecto.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class OfertaReventa {

    public enum Estado {
        PUBLICADA,
        RETIRADA,
        ELIMINADA_ADMIN,
        VENDIDA;

        public static Estado fromString(String value) {
            if (value == null) {
                return PUBLICADA;
            }
            try {
                return Estado.valueOf(value);
            } catch (IllegalArgumentException ex) {
                return PUBLICADA;
            }
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private int id;
    private String idVendedor;
    private double precioPedido;
    private Estado estado;
    private final List<Integer> tiqueteIds = new ArrayList<>();
    private String fechaCreacion;
    private String ultimaActualizacion;

    public OfertaReventa(int id, String idVendedor, Collection<Integer> tiqueteIds, double precioPedido) {
        this.id = id;
        this.idVendedor = idVendedor;
        if (tiqueteIds != null) {
            this.tiqueteIds.addAll(tiqueteIds);
        }
        this.precioPedido = precioPedido;
        this.estado = Estado.PUBLICADA;
        String now = nowAsString();
        this.fechaCreacion = now;
        this.ultimaActualizacion = now;
    }

    public int getId() {
        return id;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public double getPrecioPedido() {
        return precioPedido;
    }

    public void setPrecioPedido(double precioPedido) {
        this.precioPedido = precioPedido;
        actualizarMarcaDeTiempo();
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
        actualizarMarcaDeTiempo();
    }

    public List<Integer> getTiqueteIds() {
        return Collections.unmodifiableList(tiqueteIds);
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public boolean incluyeTiquete(int tiqueteId) {
        return this.tiqueteIds.contains(tiqueteId);
    }

    public void actualizarMarcaDeTiempo() {
        this.ultimaActualizacion = nowAsString();
    }

    private static String nowAsString() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("idVendedor", this.idVendedor);
        json.put("precioPedido", this.precioPedido);
        json.put("estado", this.estado.name());
        json.put("fechaCreacion", this.fechaCreacion);
        json.put("ultimaActualizacion", this.ultimaActualizacion);
        JSONArray tiqs = new JSONArray();
        for (Integer idTiq : this.tiqueteIds) {
            tiqs.put(idTiq);
        }
        json.put("tiqueteIds", tiqs);
        return json;
    }

    public static OfertaReventa fromJSON(JSONObject json) {
        int id = json.optInt("id", 0);
        String vendedor = json.optString("idVendedor", null);
        double precio = json.optDouble("precioPedido", 0.0);
        List<Integer> tiqs = new ArrayList<>();
        JSONArray arr = json.optJSONArray("tiqueteIds");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                tiqs.add(arr.optInt(i));
            }
        }
        OfertaReventa oferta = new OfertaReventa(id, vendedor, tiqs, precio);
        oferta.estado = Estado.fromString(json.optString("estado", Estado.PUBLICADA.name()));
        oferta.fechaCreacion = json.optString("fechaCreacion", oferta.fechaCreacion);
        oferta.ultimaActualizacion = json.optString("ultimaActualizacion", oferta.ultimaActualizacion);
        return oferta;
    }
}
