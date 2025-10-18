package dpoo.proyecto.tiquetes;

public abstract class Tiquete {
	private double precio;
	private double cargoPorcentualServicio;
	private double cuotaAdicionalEmision;
	private String fecha;
	private String hora;
	private int id;
	private int maximoTiquetesPorTransaccion;
	private String tipo;
	
	public Tiquete(double precio, int id, int maximoTiquetesPorTransaccion) {
		this.precio = precio;
		this.id = id;
		this.maximoTiquetesPorTransaccion = maximoTiquetesPorTransaccion;
	}
	
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	public double getCargoPorcentualServicio() {
		return cargoPorcentualServicio;
	}
	public void setCargoPorcentualServicio(double cargoPorcentualServicio) {
		this.cargoPorcentualServicio = cargoPorcentualServicio;
	}
	public double getCuotaAdicionalEmision() {
		return cuotaAdicionalEmision;
	}
	public void setCuotaAdicionalEmision(double cuotaAdicionalEmision) {
		this.cuotaAdicionalEmision = cuotaAdicionalEmision;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMaximoTiquetesPorTransaccion() {
		return maximoTiquetesPorTransaccion;
	}
	public void setMaximoTiquetesPorTransaccion(int maximoTiquetesPorTransaccion) {
		this.maximoTiquetesPorTransaccion = maximoTiquetesPorTransaccion;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
