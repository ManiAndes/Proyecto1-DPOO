package dpoo.proyecto.tiquetes;

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
}
