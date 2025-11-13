package dpoo.proyecto.consola;

import java.util.List;
import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.marketplace.OfertaReventa;
import dpoo.proyecto.marketplace.RegistroReventa;
import dpoo.proyecto.usuarios.Administrador;


public class ConsolaAdmin extends ConsolaBasica {
	
	
	private MasterTicket sistemaBoleteria;
	private Administrador admin;

	public ConsolaAdmin(MasterTicket sistemaBoleteria, Administrador admin) {
		super();
		this.sistemaBoleteria = sistemaBoleteria;
		this.admin = admin;
	}

	public void showMenuAdmin() {
		System.out.println("===BIENVENIDO "+this.admin.getLogin()+" AL MENU DE ADMINS===");
		System.out.println("SELECCIONE UNA OPCION: ");
		System.out.println("1. VER EVENTOS");
		System.out.println("2. ESTABLECER COSTO POR EMISION");
		System.out.println("3. VER FINANZAS");
		System.out.println("4. VER SOLICITUDES DE VENUES");
		System.out.println("5. VER SOLICITUDES DE REEMBOLSOS");
		System.out.println("6. Ver log de Marketplace");
		System.out.println("7. Gestionar ofertas del Marketplace");
		System.out.println("0. Salir");
	}

	
	public void consolaAdmin(String opcion) {
		
		boolean notError = false;
		
		while (!notError) {
			
			notError = true;
			try {

				if (opcion.equals("1")) {
					notError = gestionarEventos();
					
				} else if(opcion.equals("2")) {
					notError = establecerCostoEmision();
					
				} else if(opcion.equals("3")){
					
					notError = verFinanzas();
					
				} else if (opcion.equals("4")) {
					notError = verSolicitudesVenues();

				} else if (opcion.equals("5")) {
					notError = gestionarReembolsos();
				} else if (opcion.equals("6")) {
					notError = verLogMarketplace();
				} else if (opcion.equals("7")) {
					notError = gestionarMarketplace();

				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean verFinanzas() {
        System.out.println("=== VER FINANZAS ===");
        System.out.println("1. Ver finanzas por Fecha");
        System.out.println("2. Ver finanzas por Evento");
        System.out.println("3. Ver finanzas por Organizador");
        String opcion = pedirCadena("¿Cómo desea ver las finanzas de MasterTicket? (0 para volver)");

        if ("0".equals(opcion)) {
            return true; // volver al menú admin
        }

        Map<String, Double> resultado = null;
        try {
            if ("1".equals(opcion)) {
                resultado = this.admin.verFinanzasPorFecha(this.sistemaBoleteria);
            } else if ("2".equals(opcion)) {
                resultado = this.admin.verFinanzasPorEvento(this.sistemaBoleteria);
            } else if ("3".equals(opcion)) {
                resultado = this.admin.verFinanzasPorOrganizador(this.sistemaBoleteria);
            } else {
                System.out.println("Opción inválida");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        if (resultado == null || resultado.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return true;
        }

        double total = 0.0;
        System.out.println("=== RESULTADOS ===");
        for (Map.Entry<String, Double> entry : resultado.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
            total += entry.getValue();
        }
        System.out.println("TOTAL: " + total);
        return true;
    }

	public boolean verSolicitudesVenues() {
	
		Map<String, Venue> pendientes = this.sistemaBoleteria.getVenuesPendientes();
		if (pendientes.isEmpty()) {
			System.out.println("No hay venues pendientes de aprobación.");
			return true;
		}
		System.out.println("=== VENUES PENDIENTES ===");
		for (String nombre : pendientes.keySet()) {
			System.out.println("- " + nombre);
		}
		String nombre = pedirCadena("Escriba el nombre a gestionar o 0 para volver").toUpperCase();
		if ("0".equals(nombre)) return true;
		String accion = pedirCadena("Aprobar (a) / Rechazar (r) ?");
		boolean ok = false;
		if ("a".equalsIgnoreCase(accion)) {
			ok = this.admin.aprobarVenue(this.sistemaBoleteria, nombre);
		} else if ("r".equalsIgnoreCase(accion)) {
			ok = this.admin.rechazarVenue(this.sistemaBoleteria, nombre);
		}
		System.out.println(ok ? "Operación exitosa." : "Operación fallida.");
		return ok;
	}

	
	public boolean establecerCostoEmision() {
		
		double nuevoCostoEmision = Double.parseDouble(pedirCadena("Nuevo costo por Emision de MasterTicket: "));
		
		this.sistemaBoleteria.setCostoPorEmision(nuevoCostoEmision);
		
		if (this.sistemaBoleteria.getCostoPorEmision() == nuevoCostoEmision) {
			
			System.out.println("COSTO POR EMISION CAMBIADO EXITOSAMENTE");
		}else {
			System.out.println("Ups, algo fallo...");
			return false;
		}
		
		
		return true;
	}
	
    public boolean gestionarEventos() {
        this.sistemaBoleteria.viewEventos();
        String eventoSeleccion = pedirCadena("Ingrese el nombre del evento que quiere gestionar: ").toUpperCase();
        Evento eventoSeleccionado = this.sistemaBoleteria.selectorEvento(eventoSeleccion);
        if (eventoSeleccionado == null) {
            System.out.println("Evento no encontrado.");
            return true;
        }
        viewEventoAdmin(eventoSeleccionado);
		
		System.out.println("1. Cancelar el evento");

		System.out.println("2. Establecer un costo porcentual de servicios");
		
		System.out.println("3. Salir");
		String opcion = pedirCadena("Que desea hacer con el evento?: ");
		if (opcion.equals("1")) {
			//CANCELAR EVENTO
			System.out.println("Tipos de reembolso.");
			System.out.println("1. Reembolsar solamente sin costos emision");
			System.out.println("2. Reembolsar solo precio base (Recomendado)");
			System.out.println("3. Salir");
			int tipoReembolso = Integer.parseInt(pedirCadena("Escoja el tipo de reembolso"));
			
            this.admin.cancelarEvento(eventoSeleccionado, tipoReembolso, sistemaBoleteria); //REEMBOLSO EVENTO
            System.out.println("Evento cancelado y reembolsos procesados.");

		}else if(opcion.equals("2")) {
			
			double nuevoCostoPorcentual = Double.parseDouble(pedirCadena("Nuevo costo porcentual (decimal) de servicios del evento: "));
			eventoSeleccionado.setCargoPorcentualServicio(nuevoCostoPorcentual);
			if (eventoSeleccionado.getCargoPorcentualServicio() == nuevoCostoPorcentual) {
				System.out.println("COSTO PORCENTUAL CAMBIADO EXITOSAMENTE");
			}else {
				System.out.println("Ups, algo fallo...");
			}
			
		}else {
			//salir TODO
			System.out.println("Salir");
			return false;
		}
		return true;
		
	}

	
	public boolean gestionarReembolsos() {
		Map<Integer, SolicitudReembolso> pendientes = this.sistemaBoleteria.getSolicitudesReembolso();
		if (pendientes.isEmpty()) {
			System.out.println("No hay solicitudes de reembolso pendientes.");
			return true;
		}
		System.out.println("=== SOLICITUDES PENDIENTES ===");
		for (SolicitudReembolso solicitud : this.sistemaBoleteria.listarSolicitudesPendientes()) {
			String evento = solicitud.getTiquete() != null && solicitud.getTiquete().getEvento() != null
					? solicitud.getTiquete().getEvento().getNombre()
					: "N/A";
			String solicitante = solicitud.getSolicitante() != null ? solicitud.getSolicitante().getLogin() : "N/A";
			System.out.println("ID: " + solicitud.getId() + " | Evento: " + evento + " | Usuario: " + solicitante + " | Estado: " + solicitud.getEstado());
		}
		String idStr = pedirCadena("Ingrese ID de la solicitud a gestionar (0 para volver)");
		if ("0".equals(idStr)) return true;
		int idSolicitud;
		try {
			idSolicitud = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			System.out.println("ID inválido.");
			return true;
		}
		SolicitudReembolso solicitud = pendientes.get(idSolicitud);
		if (solicitud == null) {
			System.out.println("Solicitud no encontrada o ya procesada.");
			return true;
		}
		String accion = pedirCadena("Aprobar (a) / Rechazar (r)");
		boolean resultado = false;
		if ("a".equalsIgnoreCase(accion)) {
			System.out.println("1. Reembolso sin costo de emisión (cancelación por irregularidad)");
			System.out.println("2. Reembolso precio base (cancelación por organizador)");
			System.out.println("3. Reembolso total pagado");
			int tipo;
			try {
				tipo = Integer.parseInt(pedirCadena("Seleccione tipo de reembolso"));
			} catch (NumberFormatException e) {
				System.out.println("Tipo inválido.");
				return true;
			}
			resultado = this.admin.aprobarReembolso(this.sistemaBoleteria, idSolicitud, tipo);
		} else if ("r".equalsIgnoreCase(accion)) {
			resultado = this.admin.rechazarReembolso(this.sistemaBoleteria, idSolicitud);
		} else {
			System.out.println("Acción no válida.");
		}
		System.out.println(resultado ? "Solicitud procesada." : "No se pudo procesar la solicitud.");
		return resultado;
	}

	public boolean verLogMarketplace() {
		MarketplaceReventa marketplace = this.sistemaBoleteria.getMarketplaceReventa();
		if (marketplace == null) {
			System.out.println("Marketplace no disponible.");
			return true;
		}
		List<RegistroReventa> registros = marketplace.getRegistros();
		if (registros.isEmpty()) {
			System.out.println("El log aún no tiene registros.");
			return true;
		}
		System.out.println("=== LOG DE MARKETPLACE ===");
		for (RegistroReventa registro : registros) {
			System.out.println(registro.formatear());
		}
		return true;
	}

	public boolean gestionarMarketplace() {
		MarketplaceReventa marketplace = this.sistemaBoleteria.getMarketplaceReventa();
		if (marketplace == null) {
			System.out.println("Marketplace no disponible.");
			return true;
		}
		List<OfertaReventa> ofertas = marketplace.listarOfertasActivas();
		if (ofertas.isEmpty()) {
			System.out.println("No hay ofertas activas en el Marketplace.");
			return true;
		}
		System.out.println("=== OFERTAS ACTIVAS ===");
		for (OfertaReventa oferta : ofertas) {
			System.out.println(oferta.descripcionBasica());
		}
		String idStr = pedirCadena("ID de la oferta a eliminar (0 para volver)");
		if ("0".equals(idStr)) {
			return true;
		}
		try {
			int ofertaId = Integer.parseInt(idStr);
			boolean ok = marketplace.eliminarOfertaComoAdmin(ofertaId, this.admin, "Removida por administrador");
			System.out.println(ok ? "Oferta eliminada correctamente." : "La oferta no pudo ser eliminada.");
		} catch (NumberFormatException e) {
			System.out.println("ID inválido.");
		}
		return true;
	}

}
