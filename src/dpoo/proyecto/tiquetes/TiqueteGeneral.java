package dpoo.proyecto.tiquetes;

public class TiqueteGeneral extends Tiquete {

    public TiqueteGeneral(double precioOriginal, double cuotaAdicionalEmision, String fecha,
                          String hora, int maximoTiquetesPorTransaccion, String tipo) {
        super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
    }
}

