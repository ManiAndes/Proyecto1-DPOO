package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.tiquetes.*;

import java.util.*;

public class MasterTicket<T extends Tiquete> {
	
	// Mapa de los usuarios registrados
	private Map<String, Usuario<T>> clientes;
	
	// Mapa de todos los eventos (activos?)
	private Map<String, Evento<T>> eventos;
	
	// Mapa de todos los venues
	private Map<String, Venue> venues;

	public MasterTicket() {
		super();
		this.clientes = new HashMap<String, Usuario<T>>();
		this.eventos = new HashMap<String, Evento<T>>();
		this.venues = new HashMap<String, Venue>();
	}

	public Map<String, Usuario<T>> getClientes() {
		return clientes;
	}

	public Map<String, Evento<T>> getEventos() {
		return eventos;
	}

	public Map<String, Venue> getVenues() {
		return venues;
	}

	public void setClientes(Map<String, Usuario<T>> clientes) {
		this.clientes = clientes;
	}

	public void setEventos(Map<String, Evento<T>> eventos) {
		this.eventos = eventos;
	}

	public void setVenues(Map<String, Venue> venues) {
		this.venues = venues;
	}
	
}
