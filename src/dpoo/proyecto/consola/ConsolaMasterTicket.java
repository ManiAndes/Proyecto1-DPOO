package dpoo.proyecto.consola;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.tiquetes.Tiquete;

public class ConsolaMasterTicket extends ConsolaBasica {
	
	private MasterTicket sistemaBoleteria;
	
	private void menuLogin() {
		String menu = "1 - Login y contraseña\n2 - Crear usuario";
		System.out.println(menu);
	}
	
	private void correrAplicacion() {
		
		try {
			
			sistemaBoleteria = new MasterTicket<Tiquete>();
			
		} catch (Exception e) {
			
		}
		
		boolean running = true;
		
		while (running) {
			
			menuLogin();
			pedirCadena("Seleccione la opción");
			
		}
		
	}
	
	private static void main(String[] args) {
		
		ConsolaMasterTicket c = new ConsolaMasterTicket();
		c.correrAplicacion();
		
	}

}
