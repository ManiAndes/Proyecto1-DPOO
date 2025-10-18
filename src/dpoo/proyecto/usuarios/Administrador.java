package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.eventos.Evento;

public class Administrador {
	
	private double cargoServicio;
	private static double COBRO_POR_EMISION;
	
	// Llaves son logins y valor son Organizador
	private Map<String, Organizador> organizadores = new HashMap<String, Organizador>();
	
	// Llaves son fechas y valor son lista de Evento
	private Map<String, List<Evento>> eventosFecha = new HashMap<String, List<Evento>>();
	

	

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
	
	private void reembolsar(Usuario cliente, double precio) {
		
	
			
			double saldoOriginal = cliente.getSaldoVirtual();
			
			cliente.setSaldoVirtual(precio + saldoOriginal);
			
			
		}
		
		
		
		
	
	
	public void cancelarEvento(Evento evento, Map<String, Evento> eventos) {
		String nombre = evento.cancelar();
		
		
		List<Tiquete> tiquetes = evento.getTiquetesVendidos();
		for (Tiquete tiquete: tiquetes) {
			Usuario cliente = tiquete.getCliente();
			
			double precio = tiquete.getPrecio();
			
			reembolsar(cliente, 0.0);
			
			
		}
		
		eventos.remove(nombre);
		
		
		
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
