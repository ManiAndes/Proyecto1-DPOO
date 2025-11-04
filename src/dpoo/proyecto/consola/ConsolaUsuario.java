package dpoo.proyecto.consola;

import java.util.*;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class ConsolaUsuario extends ConsolaBasica {
	
	private MasterTicket sistemaBoleteria;
	private Usuario usuario;
	
	ConsolaUsuario(MasterTicket sistemaBoleteria, Usuario usuario) {
		 this.sistemaBoleteria = sistemaBoleteria;
		 this.usuario = usuario;
		 
	}
	
	public void consolaUsuario() {
		
		System.out.println(
				"1 - Seleccionar evento y comprar\n" +
				"2 - Ver saldo\n" + 
				"3 - Ver mis eventos\n" +
				"4 - Ver mis tiquetes\n" +
				"5 - Transferir tiquete\n" +
				"0 - Salir\n"
				);
	}
	
	private void mostrarEvento(Evento evento) {
		
		System.out.println("Evento: " + evento.getNombre() + "\n");
		System.out.println("Tipo: " + evento.getTipoEvento() + "\n");
		System.out.println("Fecha: " + evento.getFecha() + "\n");
		System.out.println("Venue: " + evento.getVenue() + "\n");
		
	}
	
	private void mostrarLocalidad(Localidad localidad) {
		
		System.out.println("Localidad: " + localidad.getNombreLocalidad() + "\n");
		System.out.println("Tipo: " + localidad.getPrecioTiquetes() + "\n");
		boolean esNumerada = localidad.getEsNumerada();
		
		if (esNumerada) {
			System.out.println("Es Numerada: SI");
		} else {
			System.out.println("Es Numerada: NO");
		}
		
	}
	
	private Map<Integer, Tiquete> seleccionarTiquetes(Localidad localidad, int cantidadTiquetes) {
		
		Map<Integer, Tiquete> tiquetesSeleccionados = new HashMap<Integer, Tiquete>();
		List<Tiquete> tiquetesDisponibles = new ArrayList<Tiquete>(localidad.getTiquetesDisponibles().values());
		
		if (cantidadTiquetes <= tiquetesDisponibles.size()) {
			
			int i = 0;
			while (i < cantidadTiquetes) {
				
				int cId = tiquetesDisponibles.get(i).getId();
				Tiquete cTiquete = tiquetesDisponibles.get(i);
				
				tiquetesSeleccionados.put(cId, cTiquete);
				
				localidad.marcarVendido(cTiquete);
				localidad.getEvento().marcarVendido(cTiquete);
				
			}
			
		}
		
		return tiquetesSeleccionados;
	}
	
	public void comprarEvento() {
		
		boolean seguirComprando = true;
		while (seguirComprando) {
			
			// Seleccionar un evento a comprar
			Map<String, Evento> eventos = sistemaBoleteria.getEventos();
			for (Evento cEvento : eventos.values()) {
				mostrarEvento(cEvento);
			}
			
			Evento eventoSeleccionado = null;
			boolean eventoValido = false;
			while (eventoValido == false) {
				
				String nombreEvento = pedirCadena("Escriba el nombre del EVENTO deseado o 0 para salir").toUpperCase();
				
				if (nombreEvento.equals("0")) {
					seguirComprando = false;
					break;
				}
				
				eventoSeleccionado = eventos.get(nombreEvento);
				
				if (eventoSeleccionado != null) {
					eventoValido = true;
				}
			}
			
			if (seguirComprando == false) {
				break;
			}
			
			System.out.println("Evento seleccionado...\n");
			mostrarEvento(eventoSeleccionado);
			
			// Seleccionar una localidad del evento
			Map<String, Localidad> localidades = eventoSeleccionado.getLocalidades();
			for (Localidad localidad : localidades.values()) {
				
				mostrarLocalidad(localidad);
				
			}
			
			Localidad localidadSeleccionada = null;
			boolean localidadValida = false;
			while (localidadValida == false) {
				
				String nombreLocalidad = pedirCadena("Escriba el nombre de la LOCALIDAD deseado o 0 para salir").toUpperCase();
				
				if (nombreLocalidad.equals("0")) {
					seguirComprando = false;
					break;
				}
				
				localidadSeleccionada = localidades.get(nombreLocalidad);
				
				if (localidadSeleccionada != null) {
					localidadValida = true;
				}
			}
			
			if (seguirComprando == false) {
				break;
			}
			
			System.out.println("Localidad seleccionada...\n");
			mostrarLocalidad(localidadSeleccionada);
			
			// Seleccionar los tiquetes a comprar de la localidad en cuestión
			boolean compraValida = false;
			while (compraValida == false) {
				
				String cantidadTiquetesStr = pedirCadena("Escriba el nombre de la LOCALIDAD deseado o 0 para salir");
				
				if (cantidadTiquetesStr.equals("0")) {
					seguirComprando = false;
					break;
				}
				
				int cantidadTiquetes = -1;
				try{
					cantidadTiquetes = (Integer.parseInt(cantidadTiquetesStr));
					
				} catch (Exception e) {
					System.out.println("Cantidad incorrecta...");
					
				}
				
				Map<Integer, Tiquete> tiquetesSeleccionados = null;
				if (cantidadTiquetes != -1) {
					tiquetesSeleccionados = seleccionarTiquetes(localidadSeleccionada, cantidadTiquetes);
					
				}
				
				if (tiquetesSeleccionados != null) {
					compraValida = true;
				}
			}
			
			// pasar los tiquetes al usuario
			
			if (seguirComprando == false) {
				break;
			}
			
		}
	}

}
