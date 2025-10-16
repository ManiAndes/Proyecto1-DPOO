package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.tiquetes.*;

import java.util.*;

public class MasterTicket<T extends Tiquete> {
	
	// Mapa de los usuarios registrados
	private Map<String, Usuario<T>> usuarios;
	
	// Mapa de todos los eventos (activos?)
	private Map<String, Evento<T>> eventos;
	
	// Mapa de todos los venues
	private Map<String, Venue> venues;

	public MasterTicket() {
		super();
		this.usuarios = new HashMap<String, Usuario<T>>();
		this.eventos = new HashMap<String, Evento<T>>();
		this.venues = new HashMap<String, Venue>();
	}

	public Map<String, Usuario<T>> getUsuarios() {
		return usuarios;
	}

	public Map<String, Evento<T>> getEventos() {
		return eventos;
	}

	public Map<String, Venue> getVenues() {
		return venues;
	}

	public void setUsuarios(Map<String, Usuario<T>> usuarios) {
		this.usuarios = usuarios;
	}

	public void setEventos(Map<String, Evento<T>> eventos) {
		this.eventos = eventos;
	}

	public void setVenues(Map<String, Venue> venues) {
		this.venues = venues;
	}
	
	public boolean cargarUsuarios() {
		// TODO
		return false;
	}
	
	public boolean cargarEventos() {
		// TODO
		return false;
	}
	
	public boolean cargarVenues() {
		// TODO
		return false;
	}
	
}
