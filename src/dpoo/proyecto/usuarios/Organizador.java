package dpoo.proyecto.usuarios;

import java.util.ArrayList;
import java.util.List;

import dpoo.proyecto.eventos.Evento;

public class Organizador<T extends Tiquete> extends Cliente<T> {
	
	private List<Evento<T>> eventos = new ArrayList<Evento<T>>();

	public Organizador(String login, String password) {
		super(login, password);
	}
	
	public boolean crearEvento() {
		// TODO
		return false;
	}
	
	public boolean crearVenue() {
		// TODO
		return false;
	}
	
	public boolean crearLocalidad() {
		// TODO
		return false;
	}

}
