package dpoo.proyecto.usuarios;

import java.util.ArrayList;
import java.util.List;

import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.tiquetes.Tiquete;

public class Organizador extends Usuario {
	
	private List<Evento> eventos = new ArrayList<Evento>();

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
