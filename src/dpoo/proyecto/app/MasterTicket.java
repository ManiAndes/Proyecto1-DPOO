package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;

import java.util.*;

public class MasterTicket {
	
	// Mapa de los usuarios registrados
	private Map<String, Usuario> clientes;
	
	// Mapa de todos los eventos (activos?)
	private Map<String, Evento> eventos;
	
	// Mapa de todos los venues
	private Map<String, Venue> venues;
	
}
