package dpoo.proyecto.eventos;

import dpoo.proyecto.tiquetes.Tiquete;

import java.util.ArrayList;
import java.util.List;

public class Localidad {
	
	private String nombreLocalidad;
	private double precioTiquetes;
	private boolean esNumerada;
	private Evento<?> evento;
	
	public Localidad(String nombreLocalidad, double precioTiquetes, boolean esNumerada, Evento<?> evento) {
		this.nombreLocalidad = nombreLocalidad;
		this.precioTiquetes = precioTiquetes;
		this.esNumerada = esNumerada;
		this.evento = evento;
	}

	private List<Tiquete> tiquetes = new ArrayList<Tiquete>();

	public String getNombreLocalidad() {
		return nombreLocalidad;
	}

	public double getPrecioTiquetes() {
		return precioTiquetes;
	}

	public boolean isEsNumerada() {
		return esNumerada;
	}

	public Evento<?> getEvento() {
		return evento;
	}

	public List<Tiquete> getTiquetes() {
		return tiquetes;
	}

	public void setNombreLocalidad(String nombreLocalidad) {
		this.nombreLocalidad = nombreLocalidad;
	}

	public void setPrecioTiquetes(double precioTiquetes) {
		this.precioTiquetes = precioTiquetes;
	}

	public void setEsNumerada(boolean esNumerada) {
		this.esNumerada = esNumerada;
	}

	public void setEvento(Evento<?> evento) {
		this.evento = evento;
	}

	public void setTiquetes(List<Tiquete> tiquetes) {
		this.tiquetes = tiquetes;
	}

}
