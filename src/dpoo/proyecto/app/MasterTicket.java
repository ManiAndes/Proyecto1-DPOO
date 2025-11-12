package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.tiquetes.*;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MasterTicket {
	
	private double costoPorEmision;
	
	// Mapa de los usuarios registrados
	private Map<String, UsuarioGenerico> usuarios;
	
	// Mapa de todos los eventos (activos?)
	private Map<String, Evento> eventos;
	
    // Mapa de todos los venues
    private Map<String, Venue> venues;
    // Venues sugeridos por organizadores pendientes de aprobación
    private Map<String, Venue> venuesPendientes;
    // Solicitudes de reembolsos
    private Map<Integer, SolicitudReembolso> solicitudesReembolso;
	

	public MasterTicket() {
		super();
		this.usuarios = new HashMap<String, UsuarioGenerico>();
		this.eventos = new HashMap<String, Evento>();
        this.venues = new HashMap<String, Venue>();
        this.venuesPendientes = new HashMap<String, Venue>();
        this.solicitudesReembolso = new HashMap<Integer, SolicitudReembolso>();
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
    public Map<String, Venue> getVenuesPendientes() {
        return venuesPendientes;
    }
    public Map<Integer, SolicitudReembolso> getSolicitudesReembolso() {
        return solicitudesReembolso;
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
    public void setVenuesPendientes(Map<String, Venue> venuesPendientes) {
        this.venuesPendientes = venuesPendientes;
    }
    public void setSolicitudesReembolso(Map<Integer, SolicitudReembolso> solicitudesReembolso) {
        this.solicitudesReembolso = solicitudesReembolso;
    }

    // Utilidad para crear solicitudes de reembolso (prototipo)
    public SolicitudReembolso crearSolicitudReembolso(Tiquete tiquete, Usuario solicitante, String motivo) {
        int id = (int) (Math.random() * 100000);
        SolicitudReembolso s = new SolicitudReembolso(id, tiquete, solicitante, motivo);
        this.solicitudesReembolso.put(id, s);
        return s;
    }
	
	public void eliminarEvento(UsuarioGenerico admin, String nombreEvento){
		
		
		if (admin instanceof Administrador) {
			
			this.eventos.remove(nombreEvento);
			
		}else {
			System.out.println("No eres admin");
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
		
		Iterator<Localidad> it = evento.getLocalidades().values().iterator();
		
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

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("costoPorEmision", this.costoPorEmision);
		JSONArray u = new JSONArray();
		for (Map.Entry<String, UsuarioGenerico> e : this.usuarios.entrySet()) {
			u.put(e.getValue().toJSON());
		}
		json.put("usuarios", u);
		JSONArray ev = new JSONArray();
		for (Map.Entry<String, Evento> e : this.eventos.entrySet()) {
			ev.put(e.getValue().toJSON());
		}
		json.put("eventos", ev);
		JSONArray vv = new JSONArray();
		for (Map.Entry<String, Venue> e : this.venues.entrySet()) {
			vv.put(e.getValue().toJSON());
		}
		json.put("venues", vv);
		return json;
	}
	
}
