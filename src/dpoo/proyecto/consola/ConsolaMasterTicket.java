package dpoo.proyecto.consola;

import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.usuarios.Usuario;


public class ConsolaMasterTicket extends ConsolaBasica {
	
	private MasterTicket sistemaBoleteria;
	
	private void correrApp() {
		
		
	}
	
	private UsuarioGenerico logInYAuth() {
		
		boolean running = true;
		Map<String, UsuarioGenerico> usuarios = this.sistemaBoleteria.getUsuarios();
		UsuarioGenerico usuarioDeseado = null;
		
		while (running) {
			
			String menu = "1 - Login y contraseña\n2 - Crear usuario";
			System.out.println(menu);
			String opcionLogIn = pedirCadena("Seleccione la opción: ");
			
			// Login usuario existente
			if (opcionLogIn.equals("1")) {
				String logIn = pedirCadena("Log In");
				String contrasena = pedirCadena("Contraseña");
				
				if (usuarios.get(logIn).getPassword().equals(contrasena)) {
					
					usuarioDeseado = usuarios.get(logIn);
					running = false;
					
				} else {
					System.out.println("Contraseña o login incorrecto...");
				}
			
			// Crear un nuevo usuario específico
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
						running = false;
						
					}
				}
			} else {
				System.out.println("Opción inválida...");
				
			}
			
		}
		
		return usuarioDeseado;
		
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
	
	public static void main(String[] args) {
		
		ConsolaMasterTicket c = new ConsolaMasterTicket();
		c.logInYAuth();
		
		
	}

}
