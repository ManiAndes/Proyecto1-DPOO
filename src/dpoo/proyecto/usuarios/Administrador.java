package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.eventos.Evento;

public class Administrador extends UsuarioGenerico {
	
	private double cargoServicio;
	private static double COBRO_POR_EMISION;
	
	// Llaves son logins y valor son Organizador
	private Map<String, Organizador> organizadores = new HashMap<String, Organizador>();
	
	// Llaves son fechas y valor son lista de Evento
	private Map<String, List<Evento>> eventosFecha = new HashMap<String, List<Evento>>();
	
	// Todos los tiquetes
	private List<Tiquete> tiquetes = new ArrayList<Tiquete>();
	
	// Todos los eventos
	private List<Evento> eventos = new ArrayList<Evento>();
	

	public Administrador(String login, String password) {
		super(login, password);
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

	public Map<String, Organizador> getOrganizadores() {
		return organizadores;
	}

	public Map<String, List<Evento>> getEventosFecha() {
		return eventosFecha;
	}

	public List<Tiquete> getTiquetes() {
		return tiquetes;
	}

	public List<Evento> getEventos() {
		return eventos;
	}

	public void setOrganizadores(Map<String, Organizador> organizadores) {
		this.organizadores = organizadores;
	}

	public void setEventosFecha(Map<String, List<Evento>> eventosFecha) {
		this.eventosFecha = eventosFecha;
	}

	public void setTiquetes(List<Tiquete> tiquetes) {
		this.tiquetes = tiquetes;
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}

}
