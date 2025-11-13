package dpoo.proyecto.consola;

import java.util.List;
import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.AuditoriaMarketplaceEntry;
import dpoo.proyecto.app.OfertaReventa;
import dpoo.proyecto.app.TransaccionReventa;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.app.SolicitudReembolso;


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
        System.out.println("6. Aprobar organizadores");
        System.out.println("7. Gestionar Marketplace de reventa");
        System.out.println("8. Ver log de auditoría");
		System.out.println("0. Salir");
	}

	
	public void consolaAdmin(String opcion) {
		try {
			switch (opcion) {
                case "1":
                    gestionarEventos();
                    break;
                case "2":
                    establecerCostoEmision();
                    break;
                case "3":
                    verFinanzas();
                    break;
                case "4":
                    verSolicitudesVenues();
                    break;
                case "5":
                    gestionarReembolsos();
                    break;
                case "6":
                    gestionarOrganizadoresPendientes();
                    break;
                case "7":
                    gestionarMarketplace();
                    break;
                case "8":
                    verLogAuditoria();
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public boolean verFinanzasFecha() {
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

    private void gestionarOrganizadoresPendientes() {
        List<Organizador> pendientes = this.sistemaBoleteria.listarOrganizadoresPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("No hay organizadores pendientes.");
            return;
        }
        System.out.println("=== Organizadores pendientes ===");
        for (Organizador org : pendientes) {
            System.out.println("- " + org.getLogin());
        }
        String login = pedirCadena("Login a gestionar (0 para volver)");
        if ("0".equals(login)) {
            return;
        }
        String accion = pedirCadena("Aprobar (a) o Rechazar (r)");
        boolean resultado = false;
        if ("a".equalsIgnoreCase(accion)) {
            resultado = this.sistemaBoleteria.aprobarOrganizador(login);
            System.out.println(resultado ? "Organizador aprobado." : "No se pudo aprobar.");
        } else if ("r".equalsIgnoreCase(accion)) {
            resultado = this.sistemaBoleteria.rechazarOrganizador(login);
            System.out.println(resultado ? "Organizador rechazado." : "No se pudo rechazar.");
        } else {
            System.out.println("Acción inválida.");
        }
    }

    private void gestionarMarketplace() {
        System.out.println("=== Marketplace de Reventa ===");
        List<OfertaReventa> ofertas = this.sistemaBoleteria.listarOfertasActivas();
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas activas.");
        } else {
            for (OfertaReventa oferta : ofertas) {
                imprimirOferta(oferta);
            }
        }
        System.out.println("1. Eliminar oferta");
        System.out.println("2. Ver transacciones");
        System.out.println("0. Volver");
        String opcion = pedirCadena("Seleccione una opción");
        if ("1".equals(opcion)) {
            String idStr = pedirCadena("ID de la oferta a eliminar");
            try {
                int id = Integer.parseInt(idStr);
                boolean ok = this.sistemaBoleteria.eliminarOfertaPorAdmin(id, this.admin.getLogin());
                System.out.println(ok ? "Oferta eliminada." : "No fue posible eliminar la oferta.");
            } catch (NumberFormatException e) {
                System.out.println("ID inválido.");
            }
        } else if ("2".equals(opcion)) {
            mostrarTransacciones();
        }
    }

    private void mostrarTransacciones() {
        List<TransaccionReventa> transacciones = this.sistemaBoleteria.listarTransaccionesReventa();
        if (transacciones.isEmpty()) {
            System.out.println("No hay transacciones registradas.");
            return;
        }
        System.out.println("=== Transacciones de reventa ===");
        for (TransaccionReventa tx : transacciones) {
            System.out.println("Tx#" + tx.getId() + " Oferta#" + tx.getIdOferta()
                    + " Vendedor: " + tx.getIdVendedor()
                    + " Comprador: " + tx.getIdComprador()
                    + " Precio: " + tx.getPrecioFinal()
                    + " Fecha: " + tx.getFechaHora());
        }
    }

    private void verLogAuditoria() {
        List<AuditoriaMarketplaceEntry> log = this.sistemaBoleteria.obtenerLogAuditoria();
        if (log.isEmpty()) {
            System.out.println("No hay eventos registrados en el log.");
            return;
        }
        System.out.println("=== Log de auditoría Marketplace ===");
        for (AuditoriaMarketplaceEntry entry : log) {
            System.out.println("[" + entry.getTimestamp() + "] "
                    + entry.getAccion() + " actor=" + entry.getActor()
                    + " rol=" + entry.getRol()
                    + " recurso=" + entry.getRecurso()
                    + " resultado=" + entry.getResultado()
                    + " detalle=" + entry.getDetalle());
        }
    }

    private void imprimirOferta(OfertaReventa oferta) {
        StringBuilder detalle = new StringBuilder();
        for (Integer idTiq : oferta.getTiqueteIds()) {
            Tiquete tiquete = this.sistemaBoleteria.buscarTiquete(idTiq);
            String evento = tiquete != null && tiquete.getEvento() != null ? tiquete.getEvento().getNombre() : "N/A";
            detalle.append("#").append(idTiq).append("(").append(evento).append(") ");
        }
        System.out.println("Oferta#" + oferta.getId()
                + " Vendedor: " + oferta.getIdVendedor()
                + " Precio: " + oferta.getPrecioPedido()
                + " Estado: " + oferta.getEstado()
                + " Tiquetes: " + detalle);
    }

}
