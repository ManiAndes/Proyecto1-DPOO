package dpoo.proyecto.consola;

import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.usuarios.Usuario;


public class ConsolaMasterTicket extends ConsolaBasica {
	
	private MasterTicket<Tiquete> sistemaBoleteria;
	
	private Usuario<Tiquete> login() {
		
		String menu = "1 - Login y contraseña\n2 - Crear usuario";
		System.out.println(menu);
		
		boolean running = true;
		
		Map<String, Usuario<Tiquete>> usuarios = this.sistemaBoleteria.getUsuarios();
		Usuario elegido;
		
		while (running) {
			
			String opcion = pedirCadena("Seleccione la opción");
			
			if (opcion == "1") {
				
				String usuario = pedirCadena("Usuario");
				String password = pedirCadena("Contraseña");
				
				Usuario<Tiquete> usuarioBuscado = usuarios.get(usuario);
				
				if (usuarioBuscado == null) {
					System.out.println("No existe...");
				}
				
				else if (password == usuarioBuscado.getPassword()) {
					elegido = usuarioBuscado;
					running = false;
				}
				
				else {
					System.out.println("Contraseña incorrecta...");
				}
			}
			
			else if (opcion == "2") {
				
				String tipo;
				boolean tipoElegido = false;
				while (tipoElegido == false) {
					String tipo = pedirCadena("Seleccione tipo de usuario");
					
					if () {
						
					}
					
				}
				
				String usuario = pedirCadena("Usuario");
				String password = pedirCadena("Contraseña");
				
				Usuario<Tiquete> usuarioBuscado = usuarios.get(usuario);
				
				if (usuarioBuscado == null) {
					Usuario nuevo = new Usuario(usuario, password);
					
					usuarios.put(usuario, usuarioBuscado)
				}
				
				else if (password == usuarioBuscado.getPassword()) {
					elegido = usuarioBuscado;
					running = false;
				}
				
				else {
					System.out.println("Contraseña incorrecta...");
				}
				
			}
			
			else {
				System.out.println("Opción inválida...");
			}
			
		
			
		}
		
		return loginUsuario;
	}
	
	private Usuario logInYAuth() {
		
		try {
			
			sistemaBoleteria = new MasterTicket<Tiquete>();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		Usuario usuarioActual = login();
		
		
		
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
		c.login();
		
	}

}
