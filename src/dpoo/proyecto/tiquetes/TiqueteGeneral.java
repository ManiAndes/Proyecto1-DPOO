package dpoo.proyecto.tiquetes;

import org.json.JSONObject;

public class TiqueteGeneral extends Tiquete {

    public TiqueteGeneral(double precioOriginal, double cuotaAdicionalEmision, String fecha,
                          String hora, int maximoTiquetesPorTransaccion, String tipo) {
        super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
    }

    public static TiqueteGeneral fromJSON(JSONObject json) {
        double precio = json.optDouble("precioOriginal", 0.0);
        double emision = json.optDouble("cuotaAdicionalEmision", 0.0);
        String fecha = json.optString("fecha", "");
        String hora = json.optString("hora", "");
        int max = json.optInt("maximoTiquetesPorTransaccion", 0);
        String tipo = json.optString("tipo", "");
        return new TiqueteGeneral(precio, emision, fecha, hora, max, tipo);
    }
}
