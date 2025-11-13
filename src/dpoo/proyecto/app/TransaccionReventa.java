package dpoo.proyecto.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TransaccionReventa {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private int id;
    private int idOferta;
    private final List<Integer> tiqueteIds = new ArrayList<>();
    private String idVendedor;
    private String idComprador;
    private double precioFinal;
    private String fechaHora;

    public TransaccionReventa(int id, int idOferta, Collection<Integer> tiqueteIds, String idVendedor,
                              String idComprador, double precioFinal) {
        this.id = id;
        this.idOferta = idOferta;
        if (tiqueteIds != null) {
            this.tiqueteIds.addAll(tiqueteIds);
        }
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.precioFinal = precioFinal;
        this.fechaHora = LocalDateTime.now().format(FORMATTER);
    }

    public int getId() {
        return id;
    }

    public int getIdOferta() {
        return idOferta;
    }

    public List<Integer> getTiqueteIds() {
        return Collections.unmodifiableList(tiqueteIds);
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("idOferta", this.idOferta);
        json.put("idVendedor", this.idVendedor);
        json.put("idComprador", this.idComprador);
        json.put("precioFinal", this.precioFinal);
        json.put("fechaHora", this.fechaHora);
        JSONArray tiqs = new JSONArray();
        for (Integer idTiq : this.tiqueteIds) {
            tiqs.put(idTiq);
        }
        json.put("tiqueteIds", tiqs);
        return json;
    }

    public static TransaccionReventa fromJSON(JSONObject json) {
        int id = json.optInt("id", 0);
        int idOferta = json.optInt("idOferta", 0);
        String vendedor = json.optString("idVendedor", null);
        String comprador = json.optString("idComprador", null);
        double precio = json.optDouble("precioFinal", 0.0);
        List<Integer> tiqs = new ArrayList<>();
        JSONArray arr = json.optJSONArray("tiqueteIds");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                tiqs.add(arr.optInt(i));
            }
        }
        TransaccionReventa tx = new TransaccionReventa(id, idOferta, tiqs, vendedor, comprador, precio);
        tx.fechaHora = json.optString("fechaHora", tx.fechaHora);
        return tx;
    }
}
