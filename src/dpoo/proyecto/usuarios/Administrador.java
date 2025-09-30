package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.eventos.Evento;

public class Administrador<T extends Tiquete> {
	
	private double cargoServicio;
	private static double COBRO_POR_EMISION;
	
	// Llaves son logins y valor son Organizador
	private Map<String, Organizador<T>> organizadores = new HashMap<String, Organizador<T>>();
	
	// Llaves son fechas y valor son lista de Evento
	private Map<String, List<Evento<T>>> eventosFecha = new HashMap<String, List<Evento<T>>>();
	
	// Todos los tiquetes
	private List<T> tiquetes = new ArrayList<T>();
	
	// Todos los eventos
	private List<Evento<T>> eventos = new ArrayList<Evento<T>>();
	

	public Administrador() {
	}
	

	public boolean aprobacionVenue() {
		// TODO
		return false;
	}
	
	public boolean aprobacionReembolso() {
		// TODO
		return false;
	}
	
	private static boolean reembolsar() {
		//TODO 
		return false;
	}
	
	public boolean cancelarEvento() {
		// TODO
		return false;
	}

	public Map<String, Organizador<T>> getOrganizadores() {
		return organizadores;
	}

	public Map<String, List<Evento<T>>> getEventosFecha() {
		return eventosFecha;
	}

	public List<T> getTiquetes() {
		return tiquetes;
	}

	public List<Evento<T>> getEventos() {
		return eventos;
	}

	public void setOrganizadores(Map<String, Organizador<T>> organizadores) {
		this.organizadores = organizadores;
	}

	public void setEventosFecha(Map<String, List<Evento<T>>> eventosFecha) {
		this.eventosFecha = eventosFecha;
	}

	public void setTiquetes(List<T> tiquetes) {
		this.tiquetes = tiquetes;
	}

	public void setEventos(List<Evento<T>> eventos) {
		this.eventos = eventos;
	}

}
