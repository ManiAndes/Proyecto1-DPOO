package dpoo.proyecto.eventos;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;
import org.json.JSONArray;
import org.json.JSONObject;

public class Venue {
	
	private String nombre;
	private int capacidad;
	private String ubicacion;
	private List<Evento> eventos = new ArrayList<>();
	private Organizador organizador;
	private boolean aprobado;
	
	public void addEvento(Evento evento) {
		if (this.eventos.contains(evento) == false) {
			this.eventos.add(evento);
		}
	}

	public String getNombre() {
		return this.nombre;
	}
	
	public int getCapacidad() {
		return capacidad;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public List<Evento> getEventos() {
		return eventos;
	}

	public Organizador getOrganizador() {
		return organizador;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}

	public void setOrganizador(Organizador organizador) {
		this.organizador = organizador;
	}
	
	public boolean isAprobado() {
		return aprobado;
	}

	public void setAprobado(boolean aprobado) {
		this.aprobado = aprobado;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("nombre", this.nombre);
		json.put("capacidad", this.capacidad);
		json.put("ubicacion", this.ubicacion);
		json.put("aprobado", this.aprobado);
		if (this.organizador != null) json.put("organizadorLogin", this.organizador.getLogin());
		JSONArray evs = new JSONArray();
		for (Evento e : this.eventos) {
			if (e != null && e.getNombre() != null) evs.put(e.getNombre());
		}
		json.put("eventos", evs);
		return json;
	}

	public static Venue fromJSON(JSONObject json) {
		Venue v = new Venue();
		v.setNombre(json.getString("nombre"));
		v.setCapacidad(json.optInt("capacidad", 0));
		v.setUbicacion(json.optString("ubicacion", ""));
		v.setAprobado(json.optBoolean("aprobado", true));
		return v;
	}

}
