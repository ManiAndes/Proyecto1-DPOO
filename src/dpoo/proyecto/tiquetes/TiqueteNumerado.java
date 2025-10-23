package dpoo.proyecto.tiquetes;

import org.json.JSONObject;

public class TiqueteNumerado extends Tiquete{
    
    private int numeroAsiento;

    public TiqueteNumerado(double precioOriginal, double cuotaAdicionalEmision, String fecha,
                           String hora, int maximoTiquetesPorTransaccion, String tipo, int numeroAsiento) {
        super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
        this.numeroAsiento = numeroAsiento;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("numeroAsiento", this.numeroAsiento);
        return json;
    }

    public static TiqueteNumerado fromJSON(JSONObject json) {
        double precio = json.getDouble("precioOriginal");
        double emision = json.optDouble("cuotaAdicionalEmision", 0.0);
        String fecha = json.optString("fecha", "");
        String hora = json.optString("hora", "");
        int max = json.optInt("maximoTiquetesPorTransaccion", 0);
        String tipo = json.optString("tipo", "");
        int asiento = json.optInt("numeroAsiento", 0);
        return new TiqueteNumerado(precio, emision, fecha, hora, max, tipo, asiento);
    }
}
