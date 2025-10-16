package dpoo.proyecto.eventos;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;

public class Evento<T extends Tiquete> {
	
	private List<T> tiquetes = new ArrayList<T>();
	private List<Localidad> localidades = new ArrayList<Localidad>();
	private Organizador<T> organizador;
	private List<T> tiquetesVendidos = new ArrayList<T>();
	
	

	private String nombre;
	private String tipoEvento;
	private String tipoTiquetes;
	private int cantidadTiquetesDisponibles;
	private Venue venue;
	private String fecha;

	public Evento(String nombre, String tipoEvento, String tipoTiquetes, int cantidadTiquetesDisponibles, Venue venue, String fecha) {
		super();
		this.nombre = nombre;
		this.tipoEvento = tipoEvento;
		this.tipoTiquetes = tipoTiquetes;
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
		this.venue = venue;
		this.fecha = fecha;
	}

	// ??? pq uno sin nombre
	public Evento(Organizador<T> organizador, String tipoEvento, String tipoTiquetes, int cantidadTiquetesDisponibles,
			Venue venue, String fecha) {
		this.organizador = organizador;
		this.tipoEvento = tipoEvento;
		this.tipoTiquetes = tipoTiquetes;
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
		this.venue = venue;
		this.fecha = fecha;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public List<T> getTiquetes() {
		return tiquetes;
	}

	public List<Localidad> getLocalidades() {
		return this.localidades;
	}

	public Organizador<T> getOrganizador() {
		return organizador;
	}

	public String getTipoEvento() {
		return tipoEvento;
	}

	public String getTipoTiquetes() {
		return tipoTiquetes;
	}

	public int getCantidadTiquetesDisponibles() {
		return cantidadTiquetesDisponibles;
	}

	public String getFecha() {
		return fecha;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setTiquetes(List<T> tiquetes) {
		this.tiquetes = tiquetes;
	}

	public void setLocalidades(List<Localidad> localidades) {
		this.localidades = localidades;
	}

	public void setOrganizador(Organizador<T> organizador) {
		this.organizador = organizador;
	}

	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public void setTipoTiquetes(String tipoTiquetes) {
		this.tipoTiquetes = tipoTiquetes;
	}

	public void setCantidadTiquetesDisponibles(int cantidadTiquetesDisponibles) {
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public Venue getVenue() {
		return venue;
	}
	
	public void setVenue(Venue venue) {
		this.venue = venue;
		this.venue.addEvento(this);
		this.venue.setOrganizador(organizador);
	}
	public List<T> getTiquetesVendidos() {
		return tiquetesVendidos;
	}

	public void setTiquetesVendidos(List<T> tiquetesVendidos) {
		this.tiquetesVendidos = tiquetesVendidos;
	}

}
