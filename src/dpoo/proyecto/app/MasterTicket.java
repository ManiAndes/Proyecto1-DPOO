package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.tiquetes.*;

import java.util.*;

public class MasterTicket {
	
	private double costoPorEmision;
	
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
		this.costoPorEmision = 0.0;
	}
	
	

	public double getCostoPorEmision() {
		return costoPorEmision;
	}



	public void setCostoPorEmision(double costoPorEmision) {
		this.costoPorEmision = costoPorEmision;
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
	
	public Map<String, Evento> eliminarEvento(UsuarioGenerico admin, String nombreEvento){
		
		
		if (admin instanceof Administrador) {
			
			this.eventos.remove(nombreEvento);
			Evento evento = this.eventos.get(nombreEvento);
			
			return this.eventos;
			
		}else {
			System.out.println("No eres admin");
			return null;
		}
	}
	
	public void viewEventos() {
		
		System.out.println("Eventos disponibles: ");
		
		int i = 1;
	
		
		for (Map.Entry<String, Evento> pareja : this.eventos.entrySet()) {
			
			String i_ = Integer.toString(i);
			
			
			String nombre = pareja.getKey();
			
			
			System.out.println(i_ + ". "+nombre+"/n");
			
			i++;
			
		}
		
		
	}
	
	public Evento selectorEvento(String nombreEvento) {
		return this.eventos.get(nombreEvento);
	}
	
	public void viewLocalidades(Evento evento) {
		
		Iterator<Localidad> it = evento.getLocalidades().iterator();
		
		int i = 1;
		
		while (it.hasNext()) {
			Localidad localidad = it.next();
			
			String i_ = Integer.toString(i);
			
			System.out.println(i_ + ". "+localidad.getNombreLocalidad());
			System.out.println("    Precio Tiquetes: "+localidad.getPrecioTiquetes());
			System.out.println("    Numerada? : "+localidad.isEsNumerada());
			
			i++;
		}
		
		
	}
	
	
	
	
	/* TODO
	 * 
	 * 
	 * public List<Tiquete> seleccionarTiquete(Evento evento, Localidad localidad){
	 * }
	 */
	
	
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
