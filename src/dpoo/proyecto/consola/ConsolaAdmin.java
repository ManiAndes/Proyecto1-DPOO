package dpoo.proyecto.consola;

import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
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
	}

	
	public void consolaAdmin(String opcion) {
		
		boolean notError = false;
		
		while (!notError) {
			
			notError = true;
			
			try {
				
				
				if (opcion.equals("1")) {
					notError = gestionarEventos();
					
					
				}else if(opcion.equals("2")) {
					notError = establecerCostoEmision();
					
				}else if(opcion.equals("3")){
					
					notError = verFinanzas();
					
			}else if (opcion.equals("4")) {
				notError = verSolicitudesVenues();
			}else if (opcion.equals("5")) {
				
			}
				
				
				
			}catch (Exception e) {
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
			
            this.sistemaBoleteria.eliminarEvento(this.admin, eventoSeleccionado.getNombre());
            this.admin.cancelarEvento(eventoSeleccionado, tipoReembolso, sistemaBoleteria); //REEMBOLSO EVENTO
            
            if (!this.sistemaBoleteria.getEventos().containsKey(eventoSeleccion)) {
                System.out.println("EVENTO ELIMINADO EXITOSAMENTE!");
            }else {
                System.out.println("Ups! Algo fallo...");
                return false;
            }
			
			

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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
