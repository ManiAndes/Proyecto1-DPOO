package dpoo.proyecto.consola;

import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
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
					
				}else if (opcion.equals("5")) {
					
				}
				
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public boolean verFinanzas() {
		
		System.out.println("1. Ver finanzas por Fecha");
		System.out.println("2. Ver finanzas por Evento");
		System.out.println("3. Ver finanzas por Organizador");
		String opcion = pedirCadena("Como desea ver las finanzas de MasterTicket?");
		
		
		return true;
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
			int tipoReembolso = Integer.parseInt(pedirCadena("Escoja el tipo de reembolso: "));
			
			this.sistemaBoleteria.eliminarEvento(this.admin, eventoSeleccionado.getNombre());
			this.admin.cancelarEvento(eventoSeleccionado, tipoReembolso); //REEMBOLSO EVENTO
			
			if (this.sistemaBoleteria.getEventos().get(eventoSeleccion).equals(null)) {
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
