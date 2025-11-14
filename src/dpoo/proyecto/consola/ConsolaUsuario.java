package dpoo.proyecto.consola;

import java.util.*;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.marketplace.ContraofertaReventa;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.marketplace.OfertaReventa;
import dpoo.proyecto.marketplace.ResultadoCompraMarketplace;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
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
				"6 - Marketplace de reventa\n" +
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

	public void gestionarMarketplace() {
		boolean continuar = true;
		while (continuar) {
			System.out.println("=== Marketplace de Reventa ===");
			System.out.println("1 - Publicar oferta de tiquete");
			System.out.println("2 - Ver ofertas disponibles");
			System.out.println("3 - Comprar oferta");
			System.out.println("4 - Realizar contraoferta");
			System.out.println("5 - Ver mis ofertas");
			System.out.println("6 - Cancelar una oferta propia");
			System.out.println("7 - Gestionar contraofertas recibidas");
			System.out.println("0 - Volver");
			String opcion = pedirCadena("Seleccione una opción");
			try {
				switch (opcion) {
					case "1":
						publicarOfertaMarketplace();
						break;
					case "2":
						verOfertasDisponiblesMarketplace();
						break;
					case "3":
						comprarOfertaMarketplace();
						break;
					case "4":
						realizarContraofertaMarketplace();
						break;
					case "5":
						verMisOfertasMarketplace();
						break;
					case "6":
						cancelarOfertaMarketplace();
						break;
					case "7":
						gestionarContraofertasRecibidas();
						break;
					case "0":
						continuar = false;
						break;
					default:
						System.out.println("Opción inválida.");
				}
			} catch (Exception e) {
				System.out.println("Acción no completada: " + e.getMessage());
			}
		}
	}

	private void publicarOfertaMarketplace() {
		List<Tiquete> elegibles = tiquetesElegiblesParaMarketplace();
		if (elegibles.isEmpty()) {
			System.out.println("No tienes tiquetes elegibles para publicar.");
			return;
		}
		System.out.println("=== Tiquetes elegibles ===");
		for (Tiquete t : elegibles) {
			String evento = t.getEvento() != null ? t.getEvento().getNombre() : "N/A";
			System.out.println("ID: " + t.getId() + " | Evento: " + evento + " | Estado: " + t.getEstado());
		}
		String idStr = pedirCadena("ID del tiquete a publicar (0 para cancelar)");
		if ("0".equals(idStr)) {
			return;
		}
		int id = Integer.parseInt(idStr);
		Tiquete objetivo = null;
		for (Tiquete t : elegibles) {
			if (t.getId() == id) {
				objetivo = t;
				break;
			}
		}
		if (objetivo == null) {
			System.out.println("Tiquete no encontrado entre los elegibles.");
			return;
		}
		double precio = Double.parseDouble(pedirCadena("Precio de venta"));
		marketplace().publicarOferta(usuario, objetivo, precio);
		System.out.println("Oferta publicada exitosamente.");
	}

	private void verOfertasDisponiblesMarketplace() {
		List<OfertaReventa> ofertas = marketplace().listarOfertasActivas();
		if (ofertas.isEmpty()) {
			System.out.println("No hay ofertas disponibles en este momento.");
			return;
		}
		System.out.println("=== Ofertas disponibles ===");
		for (OfertaReventa oferta : ofertas) {
			System.out.println(oferta.descripcionBasica());
		}
	}

	private void comprarOfertaMarketplace() {
		verOfertasDisponiblesMarketplace();
		String idStr = pedirCadena("ID de la oferta a comprar (0 para cancelar)");
		if ("0".equals(idStr)) {
			return;
		}
		int entrada = Integer.parseInt(idStr);
		OfertaReventa oferta = resolverOfertaPorEntrada(entrada);
		if (oferta == null) {
			System.out.println("La oferta no existe.");
			return;
		}
		ResultadoCompraMarketplace resultado = marketplace().comprarOferta(oferta.getId(), usuario);
		imprimirResultadoCompra(resultado);
	}

	private void realizarContraofertaMarketplace() {
		verOfertasDisponiblesMarketplace();
		String idStr = pedirCadena("ID de la oferta para contraofertar (0 para cancelar)");
		if ("0".equals(idStr)) {
			return;
		}
		int entrada = Integer.parseInt(idStr);
		OfertaReventa oferta = resolverOfertaPorEntrada(entrada);
		if (oferta == null) {
			System.out.println("La oferta no existe.");
			return;
		}
		double monto = Double.parseDouble(pedirCadena("Valor de la contraoferta"));
		ContraofertaReventa contra = marketplace().crearContraoferta(oferta.getId(), usuario, monto);
		System.out.println("Contraoferta #" + contra.getId() + " registrada.");
	}

	private void verMisOfertasMarketplace() {
		List<OfertaReventa> propias = marketplace().listarOfertasPorUsuario(usuario);
		if (propias.isEmpty()) {
			System.out.println("No tienes ofertas registradas.");
			return;
		}
		System.out.println("=== Mis ofertas ===");
		for (OfertaReventa oferta : propias) {
			int pendientes = oferta.getContraofertasPendientes().size();
			System.out.println(oferta.descripcionBasica() + " | Contraofertas pendientes: " + pendientes);
		}
	}

	private void cancelarOfertaMarketplace() {
		verMisOfertasMarketplace();
		String idStr = pedirCadena("ID de la oferta a cancelar (0 para cancelar)");
		if ("0".equals(idStr)) {
			return;
		}
		int entrada = Integer.parseInt(idStr);
		OfertaReventa oferta = resolverOfertaPorEntrada(entrada);
		if (oferta == null || oferta.getVendedor() == null || !oferta.getVendedor().equals(usuario)) {
			System.out.println("Oferta inválida.");
			return;
		}
		boolean ok = marketplace().cancelarOferta(oferta.getId(), usuario);
		System.out.println(ok ? "Oferta cancelada." : "No se pudo cancelar la oferta.");
	}

	private void gestionarContraofertasRecibidas() {
		List<OfertaReventa> propias = marketplace().listarOfertasPorUsuario(usuario);
		List<OfertaReventa> conPendientes = new ArrayList<>();
		for (OfertaReventa oferta : propias) {
			if (!oferta.getContraofertasPendientes().isEmpty()) {
				conPendientes.add(oferta);
			}
		}
		if (conPendientes.isEmpty()) {
			System.out.println("No tienes contraofertas pendientes.");
			return;
		}
		System.out.println("=== Ofertas con contraofertas pendientes ===");
		for (OfertaReventa oferta : conPendientes) {
			System.out.println(oferta.descripcionBasica());
		}
		int entrada = Integer.parseInt(pedirCadena("ID de la oferta a gestionar"));
		OfertaReventa ofertaSeleccionada = resolverOfertaPorEntrada(entrada);
		if (ofertaSeleccionada == null || ofertaSeleccionada.getVendedor() == null
				|| !ofertaSeleccionada.getVendedor().equals(usuario)) {
			System.out.println("Oferta inválida.");
			return;
		}
		List<ContraofertaReventa> pendientes = ofertaSeleccionada.getContraofertasPendientes();
		if (pendientes.isEmpty()) {
			System.out.println("No hay contraofertas pendientes para esta oferta.");
			return;
		}
		System.out.println("=== Contraofertas pendientes ===");
		for (ContraofertaReventa c : pendientes) {
			System.out.println(c.descripcionCorta());
		}
		int contraId = Integer.parseInt(pedirCadena("ID de la contraoferta"));
		String accion = pedirCadena("Aceptar (a) o rechazar (r)?");
		if ("a".equalsIgnoreCase(accion)) {
			ResultadoCompraMarketplace resultado = marketplace().aceptarContraoferta(ofertaSeleccionada.getId(), contraId, usuario);
			imprimirResultadoCompra(resultado);
		} else if ("r".equalsIgnoreCase(accion)) {
			marketplace().rechazarContraoferta(ofertaSeleccionada.getId(), contraId, usuario);
			System.out.println("Contraoferta rechazada.");
		} else {
			System.out.println("Acción inválida.");
		}
	}

	private MarketplaceReventa marketplace() {
		return sistemaBoleteria.getMarketplaceReventa();
	}

	private List<Tiquete> tiquetesElegiblesParaMarketplace() {
		List<Tiquete> elegibles = new ArrayList<>();
		for (Tiquete t : usuario.getMisTiquetes()) {
			if (t == null) {
				continue;
			}
			if (usuario.estaTiqueteEnReventa(t.getId())) {
				continue;
			}
			if (t.isUsado() || t.isReembolsado()) {
				continue;
			}
			if (t instanceof PaqueteDeluxe) {
				continue;
			}
			elegibles.add(t);
		}
		return elegibles;
	}

	private void imprimirResultadoCompra(ResultadoCompraMarketplace resultado) {
		if (resultado == null) {
			System.out.println("No se pudo completar la transacción.");
			return;
		}
		System.out.println(resultado.getMensaje());
		System.out.println("Oferta #" + resultado.getOfertaId() + " | Tiquete #" + resultado.getTiqueteId());
		System.out.println("Precio final: " + resultado.getPrecioFinal());
		System.out.println("Saldo usado: " + resultado.getSaldoUsado());
		if (resultado.getPagoExterno() > 0) {
			System.out.println("Pago externo: " + resultado.getPagoExterno());
		} else {
			System.out.println("No fue necesario pago externo.");
		}
	}

	private OfertaReventa resolverOfertaPorEntrada(int valor) {
		if (valor <= 0) {
			return null;
		}
		MarketplaceReventa mp = marketplace();
		OfertaReventa oferta = mp.buscarOferta(valor);
		if (oferta == null) {
			oferta = mp.buscarOfertaPorTiquete(valor);
		}
		return oferta;
	}

}
