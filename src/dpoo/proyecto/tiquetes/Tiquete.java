package dpoo.proyecto.tiquetes;

import java.util.List;
import java.util.Random;

import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Usuario;
import org.json.JSONObject;

public abstract class Tiquete {
	private double precioOriginal;

	private double cuotaAdicionalEmision;
	private String fecha;
	private String hora;
	private int id;
	private int maximoTiquetesPorTransaccion;
	private String tipo;
	private boolean usado;
	private boolean reembolsado;
	private boolean transferible = true;
	private double montoPagado;
	private String estado = "ACTIVO";
	private String localidad;
	
	private Usuario cliente;
	
	private Evento evento;
	
	public Tiquete(double precioOriginal, int id, int maximoTiquetesPorTransaccion) {
		this.precioOriginal = precioOriginal;
		this.id = id;
		this.maximoTiquetesPorTransaccion = maximoTiquetesPorTransaccion;
	}
	
	public Tiquete(double precioOriginal, double cuotaAdicionalEmision, String fecha,
			String hora, int maximoTiquetesPorTransaccion, String tipo) {
		
		Random random = new Random();
		
        this.id = random.nextInt(99999);
		this.precioOriginal = precioOriginal;
		
		this.cuotaAdicionalEmision = cuotaAdicionalEmision;
		this.fecha = fecha;
		this.hora = hora;
		this.maximoTiquetesPorTransaccion = maximoTiquetesPorTransaccion;
		this.tipo = tipo;
		
	}
	
	
	

	public Usuario getCliente() {
		return cliente;
	}


	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	
	}
	
	public double getPrecioOriginal() {
		return precioOriginal;
	}
	
	public void setprecioOriginal(double precioOriginal) {
		this.precioOriginal = precioOriginal;
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
	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
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
	
	public boolean isReembolsado() {
		return reembolsado;
	}

	public void setReembolsado(boolean reembolsado) {
		this.reembolsado = reembolsado;
		if (reembolsado) {
			this.estado = "REEMBOLSADO";
		}
	}

	public boolean isTransferible() {
		return transferible;
	}

	public void setTransferible(boolean transferible) {
		this.transferible = transferible;
	}

	public double getMontoPagado() {
		return montoPagado;
	}

	public void setMontoPagado(double montoPagado) {
		this.montoPagado = montoPagado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		if (estado != null) {
			this.estado = estado;
		}
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public void marcarTransferido() {
		this.estado = "TRANSFERIDO";
	}

	public void marcarUsado() {
		this.usado = true;
		this.estado = "USADO";
	}
	
	
	public double getprecioOriginalConCostos(double costoEmision, double costoServicio) {
		double costoTiquete = ((this.precioOriginal * costoEmision) + this.precioOriginal) + costoEmision;
		
		return costoTiquete;
	}
	
	public double getprecioOriginalConPorecentaje(double costoServicio) {
		return this.precioOriginal * costoServicio;
	}
	
	
	public double calcularPrecioFinal(double servicio, double emision) {
		double retorno = ((this.precioOriginal * servicio) + this.precioOriginal) + emision;
		return retorno;
	}

	public void registrarPago(double servicio, double emision) {
		this.montoPagado = calcularPrecioFinal(servicio, emision);
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("type", this.getClass().getSimpleName());
		json.put("precioOriginal", this.precioOriginal);
		json.put("cuotaAdicionalEmision", this.cuotaAdicionalEmision);
		json.put("fecha", this.fecha);
		json.put("hora", this.hora);
		json.put("id", this.id);
		json.put("maximoTiquetesPorTransaccion", this.maximoTiquetesPorTransaccion);
		json.put("tipo", this.tipo);
		json.put("usado", this.usado);
		json.put("reembolsado", this.reembolsado);
		json.put("transferible", this.transferible);
		json.put("montoPagado", this.montoPagado);
		json.put("estado", this.estado);
		json.put("localidad", this.localidad);
		if (this.cliente != null) json.put("clienteLogin", this.cliente.getLogin());
		if (this.evento != null) json.put("eventoNombre", this.evento.getNombre());
		return json;
	}

}
