package dpoo.proyecto.consola;

import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.usuarios.*;
import persistencia.CentralPersistencia;


public class ConsolaMasterTicket extends ConsolaBasica {
	
    private static MasterTicket sistemaBoleteria;
    private static final CentralPersistencia cp = new CentralPersistencia();
	
	private void correrApp() {

        try {
            sistemaBoleteria = new MasterTicket();
            cp.loadDefault(sistemaBoleteria);
			// Cargar la persistencia y añadirla al objeto de MasterTicket para operar
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Bucle principal: regresar al login cuando un menú termine (opción 0)
		while (true) {
        	UsuarioGenerico usuarioActual = logInYAuth();
			// Mostrar los menus para el usuario respectivo
            if (usuarioActual instanceof Administrador) {
                menuAdmin((Administrador) usuarioActual);
                cp.saveDefault(sistemaBoleteria);
            } else if (usuarioActual instanceof Organizador) {
                menuOrganizador((Organizador) usuarioActual);
                cp.saveDefault(sistemaBoleteria);
            } else if (usuarioActual instanceof Usuario) {
                menuUsuario((Usuario) usuarioActual);
                cp.saveDefault(sistemaBoleteria);
            }
		}
		
	}
	
	private UsuarioGenerico logInYAuth() {
		
		boolean running = true;
		Map<String, UsuarioGenerico> usuarios = sistemaBoleteria.getUsuarios();
		UsuarioGenerico usuarioDeseado = null;
		
		while (running) {
			
			String menu = "1 - Login y contraseña\n2 - Crear usuario\n0 - Cerrar programa";
			System.out.println(menu);
			String opcionLogIn = pedirCadena("Seleccione la opción");
			
			// Login usuario existente
			if (opcionLogIn.equals("1")) {
				String logIn = pedirCadena("Log In");
				String contrasena = pedirCadena("Contraseña");
				
				UsuarioGenerico candidato = usuarios.get(logIn);
				if (candidato == null) {
					System.out.println("El usuario no existe.");
					continue;
				}
				if (candidato.getPassword().equals(contrasena)) {
					usuarioDeseado = candidato;
					running = false;
				} else {
					System.out.println("Contraseña incorrecta.");
				}
				
				
				
			
			// Crear un nuevo usuario específico REGISTRO
			} else if (opcionLogIn.equals("2")) {
				String newLogIn = pedirCadena("Igrese un nombre de usuario");
				String newContrasena = pedirCadena("Ingrese una contraseña");
				String tipoUsuario = pedirCadena("Tipo de usuario deseado...\nn - Natural\no - Organizador\n");
				
				if (usuarios.get(newLogIn) == null) {
					
					UsuarioGenerico nuevoUsuario = null;
					
					if (tipoUsuario.equals("n")) {
						nuevoUsuario = new Natural(newLogIn, newContrasena);
						
					} else if (tipoUsuario.equals("o")) {
						nuevoUsuario = new Organizador(newLogIn, newContrasena);
						
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
			} else if (opcionLogIn.equals("0")) {

				System.exit(0);

			} else {
				System.out.println("Opción inválida...");
				
			}
			
		}
		
		return usuarioDeseado;
		
	}
	
	private void menuUsuario(Usuario usuario) {
		try {
			ConsolaUsuario consolaUsuario = new ConsolaUsuario(sistemaBoleteria, usuario);
			boolean running = true;
			while (running) {
				consolaUsuario.consolaUsuario();
				String opcion = pedirCadena("Opcion");
				if ("0".equals(opcion)) {
					break; // volver al login
				}
				if ("1".equals(opcion)) {
					consolaUsuario.comprarEvento();
				} else if ("2".equals(opcion)) {
					consolaUsuario.verSaldo();
				} else if ("3".equals(opcion)) {
					consolaUsuario.verMisEventos();
				} else if ("4".equals(opcion)) {
					consolaUsuario.verMisTiquetes();
				} else if ("5".equals(opcion)) {
					consolaUsuario.transferirTiquete();
				} else if ("6".equals(opcion)) {
					consolaUsuario.gestionarMarketplace();
				} else {
					System.out.println("Opción no válida.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void menuAdmin(Administrador admin) {
		
		ConsolaAdmin consolaAdmin = new ConsolaAdmin(sistemaBoleteria, admin);
		
		boolean running = true;
		
		while (running){
			
			consolaAdmin.showMenuAdmin();
			String opcion = pedirCadena("Opcion: ");
			if ("0".equals(opcion)) {
				break; // volver al login
			}
			consolaAdmin.consolaAdmin(opcion);
        
		}
        
	}

    private void menuOrganizador(Organizador org) {
        ConsolaOrganizador consolaOrg = new ConsolaOrganizador(sistemaBoleteria, org);
        boolean running = true;
        while (running) {
            consolaOrg.showMenuOrganizador();
            String opcion = pedirCadena("Opcion");
            if ("0".equals(opcion)) {
            	break; // volver al login
            }
            running = consolaOrg.consolaOrganizador(opcion);
        }
    }
	
	public static void main(String[] args) {
		
		ConsolaMasterTicket c = new ConsolaMasterTicket();
		c.correrApp();
		
		
	}

}
