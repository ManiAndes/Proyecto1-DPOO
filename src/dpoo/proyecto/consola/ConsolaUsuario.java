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
		System.out.println("Evento: " + evento.getNombre());
		System.out.println("Tipo: " + evento.getTipoEvento());
		System.out.println("Fecha: " + evento.getFecha());
		System.out.println("Venue: " + (evento.getVenue() != null ? evento.getVenue().getNombre() : "N/A"));
		System.out.println("Cancelado: " + (evento.isCancelado() ? "SI" : "NO"));
	}
	
	private void mostrarLocalidad(Localidad localidad) {
		System.out.println("Localidad: " + localidad.getNombreLocalidad());
		System.out.println("Precio base: " + localidad.getPrecioTiquetes());
		System.out.println("Es Numerada: " + (localidad.getEsNumerada() ? "SI" : "NO"));
		System.out.println("Tiquetes disponibles: " + localidad.getTiquetesDisponibles().size());
	}
	
	private List<Tiquete> seleccionarTiquetes(Localidad localidad, int cantidadTiquetes) {
		List<Tiquete> seleccionados = new ArrayList<>();
		List<Tiquete> disponibles = new ArrayList<>(localidad.getTiquetesDisponibles().values());
		if (cantidadTiquetes > disponibles.size()) {
            return seleccionados;
        }
		for (int i = 0; i < cantidadTiquetes; i++) {
			seleccionados.add(disponibles.get(i));
		}
		return seleccionados;
	}
	
	private Evento seleccionarEvento() {
		Map<String, Evento> eventos = sistemaBoleteria.getEventos();
		if (eventos.isEmpty()) {
			System.out.println("No hay eventos registrados.");
			return null;
		}
		System.out.println("=== EVENTOS DISPONIBLES ===");
		for (Evento evento : eventos.values()) {
			mostrarEvento(evento);
			System.out.println("------------------");
		}
		while (true) {
			String nombreEvento = pedirCadena("Escriba el nombre del EVENTO deseado o 0 para salir").toUpperCase();
			if ("0".equals(nombreEvento)) return null;
			Evento evento = eventos.get(nombreEvento);
			if (evento != null) {
				return evento;
			}
			System.out.println("Evento no encontrado.");
		}
	}
	
	private Localidad seleccionarLocalidad(Evento evento) {
		if (evento.getLocalidades().isEmpty()) {
			System.out.println("El evento no tiene localidades configuradas.");
			return null;
		}
		System.out.println("=== LOCALIDADES ===");
		for (Localidad localidad : evento.getLocalidades().values()) {
			mostrarLocalidad(localidad);
			System.out.println("------------------");
		}
		while (true) {
			String nombreLocalidad = pedirCadena("Escriba el nombre de la LOCALIDAD deseada o 0 para volver").toUpperCase();
			if ("0".equals(nombreLocalidad)) return null;
			Localidad localidad = evento.getLocalidades().get(nombreLocalidad);
			if (localidad != null) {
				if (localidad.getTiquetesDisponibles().isEmpty()) {
					System.out.println("No hay tiquetes disponibles en esta localidad.");
					return null;
				}
				return localidad;
			}
			System.out.println("Localidad no encontrada.");
		}
	}
	
	private double calcularTotal(Evento evento, List<Tiquete> tiquetes) {
		double total = 0.0;
		double servicio = evento.getCargoPorcentualServicio();
		for (Tiquete t : tiquetes) {
			double emisionAplicable = t.getCuotaAdicionalEmision() > 0
					? t.getCuotaAdicionalEmision()
					: sistemaBoleteria.getCostoPorEmision();
			double precioFinal = t.calcularPrecioFinal(servicio, emisionAplicable);
			total += precioFinal;
		}
		return total;
	}
	
	private void registrarVenta(Usuario usuario, Evento evento, Localidad localidad, List<Tiquete> tiquetes, double servicio, double emisionGlobal) {
		for (Tiquete tiquete : tiquetes) {
			double emisionAplicable = tiquete.getCuotaAdicionalEmision() > 0 ? tiquete.getCuotaAdicionalEmision() : emisionGlobal;
			tiquete.setCuotaAdicionalEmision(emisionAplicable);
			double precioFinal = tiquete.calcularPrecioFinal(servicio, emisionAplicable);
			tiquete.setCliente(usuario);
			tiquete.setMontoPagado(precioFinal);
			tiquete.setEstado("ACTIVO");
			tiquete.setReembolsado(false);
			localidad.marcarVendido(tiquete);
			evento.marcarVendido(tiquete);
			usuario.agregarTiquete(tiquete);
		}
	}
	
	public void verSaldo() {
		System.out.println("Saldo disponible: " + usuario.getSaldoVirtual());
	}
	
	public void verMisEventos() {
		Set<String> eventos = new HashSet<>();
		for (Tiquete tiquete : usuario.getMisTiquetes()) {
			if (tiquete.getEvento() != null) {
				eventos.add(tiquete.getEvento().getNombre());
			}
		}
		if (eventos.isEmpty()) {
			System.out.println("No tienes eventos asociados.");
			return;
		}
		System.out.println("=== MIS EVENTOS ===");
		for (String nombre : eventos) {
			System.out.println("- " + nombre);
		}
	}
	
	public void verMisTiquetes() {
		if (usuario.getMisTiquetes().isEmpty()) {
			System.out.println("No tienes tiquetes.");
			return;
		}
		System.out.println("=== MIS TIQUETES ===");
		for (Tiquete tiquete : usuario.getMisTiquetes()) {
			String evento = tiquete.getEvento() != null ? tiquete.getEvento().getNombre() : "N/A";
			System.out.println("ID: " + tiquete.getId() + " | Evento: " + evento + " | Estado: " + tiquete.getEstado());
		}
	}
	
	public void transferirTiquete() {
		System.out.println("La transferencia de tiquetes estará disponible próximamente.");
	}
	
	public void comprarEvento() {
		
		boolean seguirComprando = true;
		while (seguirComprando) {
			Evento eventoSeleccionado = seleccionarEvento();
			if (eventoSeleccionado == null) {
				break;
			}
			if (eventoSeleccionado.isCancelado()) {
				System.out.println("El evento está cancelado. No se pueden comprar tiquetes.");
				continue;
			}

			Localidad localidadSeleccionada = seleccionarLocalidad(eventoSeleccionado);
			if (localidadSeleccionada == null) {
				continue;
			}

			int disponibles = localidadSeleccionada.getTiquetesDisponibles().size();
			int maximo = 0;
			if (!localidadSeleccionada.getTiquetesDisponibles().isEmpty()) {
				Tiquete muestra = localidadSeleccionada.getTiquetesDisponibles().values().iterator().next();
				maximo = muestra.getMaximoTiquetesPorTransaccion();
			}
			if (maximo <= 0 || maximo > disponibles) {
				maximo = disponibles;
			}

			int cantidad = 0;
			while (cantidad <= 0) {
				String cantidadStr = pedirCadena("Cantidad de tiquetes (1 - " + maximo + ") o 0 para cancelar");
				try {
					cantidad = Integer.parseInt(cantidadStr);
				} catch (NumberFormatException e) {
					System.out.println("Cantidad inválida.");
					continue;
				}
				if (cantidad == 0) {
					return;
				}
				if (cantidad < 1 || cantidad > maximo) {
					System.out.println("Cantidad fuera del límite permitido.");
					cantidad = 0;
				}
			}

			List<Tiquete> tiquetesSeleccionados = seleccionarTiquetes(localidadSeleccionada, cantidad);
			if (tiquetesSeleccionados.size() != cantidad) {
				System.out.println("No se pudo completar la selección de tiquetes.");
				continue;
			}

			double total = calcularTotal(eventoSeleccionado, tiquetesSeleccionados);
			double servicio = eventoSeleccionado.getCargoPorcentualServicio();
			double emision = sistemaBoleteria.getCostoPorEmision();
			System.out.println("Total a pagar: " + total);
			double saldoOriginal = usuario.getSaldoVirtual();
			double saldoAplicado = Math.min(saldoOriginal, total);
			double pagoExterno = total - saldoAplicado;
			usuario.setSaldoVirtual(saldoOriginal - saldoAplicado);
			System.out.println("Saldo virtual usado: " + saldoAplicado);
			if (pagoExterno > 0) {
				System.out.println("Pago con método externo aprobado: " + pagoExterno);
			} else {
				System.out.println("Pago cubierto completamente con saldo virtual.");
			}

			registrarVenta(usuario, eventoSeleccionado, localidadSeleccionada, tiquetesSeleccionados, servicio, emision);
			System.out.println("Compra realizada con éxito. Tiquetes agregados a tu cuenta.");

			String continuar = pedirCadena("¿Desea comprar otro evento? (s/n)");
			if (!"s".equalsIgnoreCase(continuar)) {
				seguirComprando = false;
			}
		}
	}

}
