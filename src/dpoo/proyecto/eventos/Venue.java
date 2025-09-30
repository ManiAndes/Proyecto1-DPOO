package dpoo.proyecto.eventos;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;

public class Venue {
	
	private int capacidad;
	private String ubicacion;
	private List<Evento<? extends Tiquete>> eventos = new ArrayList<>();
	private Organizador<? extends Tiquete> organizador;
	
	public void addEvento(Evento<? extends Tiquete> evento) {
		if (this.eventos.contains(evento) == false) {
			this.eventos.add(evento);
		}
	}

	public int getCapacidad() {
		return capacidad;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public List<Evento<? extends Tiquete>> getEventos() {
		return eventos;
	}

	public Organizador<? extends Tiquete> getOrganizador() {
		return organizador;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public void setEventos(List<Evento<? extends Tiquete>> eventos) {
		this.eventos = eventos;
	}

	public void setOrganizador(Organizador<? extends Tiquete> organizador) {
		this.organizador = organizador;
	}

}
