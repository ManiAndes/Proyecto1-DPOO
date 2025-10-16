package dpoo.proyecto.consola;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class ConsolaMasterTicket extends ConsolaBasica {
	
	private MasterTicket sistemaBoleteria;
	
	
	
	public ConsolaMasterTicket(MasterTicket sistemaBoleteria) {
		super();
		this.sistemaBoleteria = sistemaBoleteria;
	}

	private Usuario logInYAuth() {
		
		try {
			
			sistemaBoleteria = new MasterTicket<Tiquete>();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		
		
		
		boolean running = true;
		
		String menu = "1 - Login y contraseña\n2 - Crear usuario";
		
		System.out.println(menu);
		
		String opcionLogIn = pedirCadena("Seleccione la opción: ");
		
		if (opcionLogIn.equals("1")) {
			String logIn = pedirCadena("Log In");
			String contrasena = pedirCadena("Contraseña");
			
		}else {
			String newLogIn = pedirCadena("Igrese un nombre de usuario");
			String newContrasena = pedirCadena("Ingrese una contraseña");
		}
		
		
		
		while (running) {
			
			
			
			
		}
		return null;
		
	}
	
	private void menuUsuario() {
		try {
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void menuAdmin() {
		
		try {
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	private static void main(String[] args) {
		
		ConsolaMasterTicket c = new ConsolaMasterTicket();
		c.logInYAuth();
		
		
	}

}
