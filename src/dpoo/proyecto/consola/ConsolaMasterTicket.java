package dpoo.proyecto.consola;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.usuarios.Usuario;

public class ConsolaMasterTicket extends ConsolaBasica {
	
	private static MasterTicket sistemaBoleteria;
	
	
	
	public ConsolaMasterTicket(MasterTicket sistemaBoleteria) {
		super();
		this.sistemaBoleteria = sistemaBoleteria;
	}
	
	
	
	private void iniciar() {
		try {
			
			sistemaBoleteria = new MasterTicket<Tiquete>();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	

	private Usuario logInYAuth() {
		
		
		
		Usuario retorno = new Natural("UsuarioPrueba", "123");
		
		
		boolean running = true;
		
		String menu = "1 - Login y contraseña\n2 - Crear usuario";
		
		System.out.println(menu);
		
		String opcionLogIn = pedirCadena("Seleccione la opción: ");
		
		if (opcionLogIn.equals("1")) {
			String logIn = pedirCadena("Log In");
			String contrasena = pedirCadena("Contraseña");
			
			
			//Validacion del log in
			if (logIn.isEmpty()) {
				return null;
			}
			
			
		}else {
			String newLogIn = pedirCadena("Igrese un nombre de usuario");
			String newContrasena = pedirCadena("Ingrese una contraseña");
		}
		
		
		
		
		return retorno;
		
	}
	
	private static void menuUsuario() {
		try {
			System.out.print("no soy admin");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void menuAdmin() {
		
		try {
			System.out.print("Soy admin");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	private static void main(String[] args) {
		
		ConsolaMasterTicket c = new ConsolaMasterTicket(sistemaBoleteria);
		
		Usuario usuario = c.logInYAuth();
		if (usuario instanceof Natural ) { //Crear clase abstracta de todos los tipos de usuarios unificados
			Usuario cliente = (Natural) usuario;
			menuUsuario();
			
		}else if(usuario instanceof Organizador) {
			
			Usuario organizador = (Organizador) usuario;
			menuUsuario();
			
		}else {
			menuAdmin();
		}
		
		
	}

}
