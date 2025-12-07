package dpoo.proyecto.marketplace;

public class ResultadoCompraMarketplace {

    private final boolean exito;
    private final int ofertaId;
    private final int tiqueteId;
    private final double precioFinal;
    private final double saldoUsado;
    private final double pagoExterno;
    private final String mensaje;

    public ResultadoCompraMarketplace(boolean exito, int ofertaId, int tiqueteId, double precioFinal,
            double saldoUsado, double pagoExterno, String mensaje) {
        this.exito = exito;
        this.ofertaId = ofertaId;
        this.tiqueteId = tiqueteId;
        this.precioFinal = precioFinal;
        this.saldoUsado = saldoUsado;
        this.pagoExterno = pagoExterno;
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public int getOfertaId() {
        return ofertaId;
    }

    public int getTiqueteId() {
        return tiqueteId;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public double getSaldoUsado() {
        return saldoUsado;
    }

    public double getPagoExterno() {
        return pagoExterno;
    }

    public String getMensaje() {
        return mensaje;
    }
}
