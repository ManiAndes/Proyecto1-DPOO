package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.tiquetes.*;

import java.util.*;

public class MasterTicket {
	
	// Mapa de los usuarios registrados
	private Map<String, UsuarioGenerico> usuarios;
	
	// Mapa de todos los eventos (activos?)
	private Map<String, Evento> eventos;
	
	// Mapa de todos los venues
	private Map<String, Venue> venues;
	

	public MasterTicket() {
		super();
		this.usuarios = new HashMap<String, UsuarioGenerico>();
		this.eventos = new HashMap<String, Evento>();
		this.venues = new HashMap<String, Venue>();
	}

	public Map<String, UsuarioGenerico> getUsuarios() {
		return usuarios;
	}

	public Map<String, Evento> getEventos() {
		return eventos;
	}

	public Map<String, Venue> getVenues() {
		return venues;
	}

	public void setUsuarios(Map<String, UsuarioGenerico> usuarios) {
		this.usuarios = usuarios;
	}

	public void setEventos(Map<String, Evento> eventos) {
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
