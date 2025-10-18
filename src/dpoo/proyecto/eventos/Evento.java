package dpoo.proyecto.eventos;

import java.util.Map;
import java.util.HashMap;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;

public class Evento {
	
	private Map<Integer, Tiquete> tiquetesDisponibles = new HashMap<Integer, Tiquete>();
	private Map<Integer, Tiquete> tiquetesNoDisponibles = new HashMap<Integer, Tiquete>();
	private Map<String, Localidad> localidades = new HashMap<String, Localidad>();
	private Organizador organizador;
	
	private String nombre;
	private String tipoEvento;
	private String tipoTiquetes;
	private int cantidadTiquetesDisponibles;
	private Venue venue;
	private String fecha;
	
	private double ganancias = 0;

	public Evento(String nombre, String tipoEvento, String tipoTiquetes, int cantidadTiquetesDisponibles, Venue venue, String fecha) {
		super();
		this.nombre = nombre;
		this.tipoEvento = tipoEvento;
		this.tipoTiquetes = tipoTiquetes;
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
		this.venue = venue;
		this.fecha = fecha;
	}

	public Evento(Organizador organizador, String tipoEvento, String tipoTiquetes, int cantidadTiquetesDisponibles,
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
	
	public Map<Integer, Tiquete> getTiquetesDisponibles() {
		return tiquetesDisponibles;
	}

	public Map<Integer, Tiquete> getTiquetesNoDisponibles() {
		return tiquetesNoDisponibles;
	}

	public void setTiquetesNoDisponibles(Map<Integer, Tiquete> tiquetesNoDisponibles) {
		this.tiquetesNoDisponibles = tiquetesNoDisponibles;
	}

	public Map<String, Localidad> getLocalidades() {
		return localidades;
	}

	public Organizador getOrganizador() {
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

	public void setTiquetesDisponibles(Map<Integer, Tiquete> tiquetes) {
		this.tiquetesDisponibles = tiquetes;
	}

	public void setLocalidades(Map<String, Localidad> localidades) {
		this.localidades = localidades;
	}

	public void setOrganizador(Organizador organizador) {
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

	public double getGanancias() {
		return ganancias;
	}

	public void setGanancias(double ganancias) {
		this.ganancias = ganancias;
	}
	
	public void marcarVendido(Tiquete tiquete) {
		
		this.tiquetesDisponibles.remove(tiquete.getId());
		this.tiquetesNoDisponibles.put(tiquete.getId(), tiquete);
		
	}

}
