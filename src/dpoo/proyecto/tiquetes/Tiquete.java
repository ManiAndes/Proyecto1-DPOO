package dpoo.proyecto.tiquetes;

import java.util.List;
import java.util.Random;

import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Usuario;

public abstract class Tiquete {
	private double precioOriginal;
	private double cargoPorcentualServicio;
	private double cuotaAdicionalEmision;
	private String fecha;
	private String hora;
	private int id;
	private int maximoTiquetesPorTransaccion;
	private String tipo;
	private boolean usado;
	
	private Usuario cliente;
	
	
	
	



	public Usuario getCliente() {
		return cliente;
	}


	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}


	public Tiquete(double precioOriginal, double cargoPorcentualServicio, double cuotaAdicionalEmision, String fecha,
			String hora, int maximoTiquetesPorTransaccion, String tipo) {
		
		Random random = new Random();
		
        this.id = random.nextInt(99999);
		this.precioOriginal = precioOriginal;
		this.cargoPorcentualServicio = cargoPorcentualServicio;
		this.cuotaAdicionalEmision = cuotaAdicionalEmision;
		this.fecha = fecha;
		this.hora = hora;
		this.maximoTiquetesPorTransaccion = maximoTiquetesPorTransaccion;
		this.tipo = tipo;
		
	}
	
	
	public double getprecioOriginal() {
		return precioOriginal;
	}
	
	public void setprecioOriginal(double precioOriginal) {
		this.precioOriginal = precioOriginal;
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
	
	
	public void setRandomId() {
        Random random = new Random();
        this.id = random.nextInt(99999); 
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
	
	public boolean isUsado() {
		return usado;
	}


	public void setUsado(boolean usado) {
		this.usado = usado;
	}
	
	public double getprecioOriginalConCostos(double costoEmision, double costoServicio) {
		double costoTiquete = ((this.precioOriginal * costoEmision) + this.precioOriginal) + costoEmision;
		
		return costoTiquete;
	}
	
	public double getprecioOriginalConPorecentaje(double costoServicio) {
		return this.precioOriginal * costoServicio;
	}
	
	


	
	
	
	

}
