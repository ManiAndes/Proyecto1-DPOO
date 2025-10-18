package dpoo.proyecto.eventos;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;

public class Venue {
	
	private String nombre;
	private int capacidad;
	private String ubicacion;
	private List<Evento> eventos = new ArrayList<>();
	private Organizador organizador;
	
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

}
