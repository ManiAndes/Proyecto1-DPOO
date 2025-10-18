package dpoo.proyecto.consola;

import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.*;


public class ConsolaMasterTicket extends ConsolaBasica {
	
	private static MasterTicket sistemaBoleteria;
	
	private void correrApp() {
		
		try {
			
			sistemaBoleteria = new MasterTicket();
			// Cargar la persistencia y añadirla al objeto de MasterTicket para operar
			UsuarioGenerico usuario = logInYAuth();
	
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		UsuarioGenerico usuarioActual = logInYAuth();
		
		// Mostrar los menus para el usuario respectivo
		if (usuarioActual instanceof Usuario) {
			menuUsuario();
			
		} else if (usuarioActual instanceof Administrador) {
			menuAdmin();
			
		}
		
	}
	
	private UsuarioGenerico logInYAuth() {
		
		boolean running = true;
		Map<String, UsuarioGenerico> usuarios = this.sistemaBoleteria.getUsuarios();
		UsuarioGenerico usuarioDeseado = null;
		
		while (running) {
			
			String menu = "1 - Login y contraseña\n2 - Crear usuario";
			System.out.println(menu);
			String opcionLogIn = pedirCadena("Seleccione la opción");
			
			// Login usuario existente
			if (opcionLogIn.equals("1")) {
				String logIn = pedirCadena("Log In");
				String contrasena = pedirCadena("Contraseña");
				
				try {
					if (usuarios.get(logIn).getPassword().equals(contrasena)) {
						
						usuarioDeseado = usuarios.get(logIn);
						running = false;
						
					} else {
						System.out.println("Contraseña o login incorrecto...");
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("Contraseña o login incorrecto...");
				}
				
				
				
			
			// Crear un nuevo usuario específico REGISTRO
			} else if (opcionLogIn.equals("2")) {
				String newLogIn = pedirCadena("Igrese un nombre de usuario");
				String newContrasena = pedirCadena("Ingrese una contraseña");
				String tipoUsuario = pedirCadena("Tipo de usuario deseado...\nn - Natural\no - Organizador\na - Administrador");
				
				if (usuarios.get(newLogIn) == null) {
					
					UsuarioGenerico nuevoUsuario = null;
					
					if (tipoUsuario.equals("n")) {
						nuevoUsuario = new Natural(newLogIn, newContrasena);
						
					} else if (tipoUsuario.equals("o")) {
						nuevoUsuario = new Organizador(newLogIn, newContrasena);
						
					} else if (tipoUsuario.equals("a")) {
						nuevoUsuario = new Administrador(newLogIn, newContrasena);
						
					} else {
						System.out.println("Opción inválida...");
					
					}
					
					if (nuevoUsuario != null) {
						usuarios.put(newLogIn, nuevoUsuario);
						usuarioDeseado = nuevoUsuario;
						System.out.println("Usuario creado exitosamente!");
						running = false;
						
					}
				}
			} else {
				System.out.println("Opción inválida...");
				
			}
			
		}
		
		return usuarioDeseado;
		
	}
	
	private void menuUsuario(Usuario usuario) {
		try {
			System.out.print("no soy admin");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void menuAdmin(Administrador admin) {
		
		ConsolaAdmin consolaAdmin = new ConsolaAdmin(this.sistemaBoleteria, admin);
		
		boolean running = true;
		
		while (running){
			
			consolaAdmin.showMenuAdmin();
			String opcion = pedirCadena("Opcion: ");
			consolaAdmin.consolaAdmin(opcion);
			
	
			
		}
		
		
		
	}
	
	public static void main(String[] args) {
		
		ConsolaMasterTicket c = new ConsolaMasterTicket();
		c.correrApp();
		
		
	}

}
