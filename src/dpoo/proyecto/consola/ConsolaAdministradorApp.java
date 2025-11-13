package dpoo.proyecto.consola;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.UsuarioGenerico;
import persistencia.CentralPersistencia;

public class ConsolaAdministradorApp extends ConsolaBasica {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;

    public ConsolaAdministradorApp() {
        this.persistencia = new CentralPersistencia();
        this.sistema = new MasterTicket();
        this.persistencia.loadDefault(this.sistema);
    }

    public void ejecutar() {
        Administrador admin = autenticarAdministrador();
        if (admin == null) {
            System.out.println("Sesión cancelada.");
            return;
        }
        ConsolaAdmin consolaAdmin = new ConsolaAdmin(this.sistema, admin);
        boolean ejecutar = true;
        while (ejecutar) {
            consolaAdmin.showMenuAdmin();
            String opcion = pedirCadena("Opción");
            if ("0".equals(opcion)) {
                ejecutar = false;
            } else {
                consolaAdmin.consolaAdmin(opcion);
            }
            this.persistencia.saveDefault(this.sistema);
        }
        System.out.println("Hasta pronto, " + admin.getLogin());
    }

    private Administrador autenticarAdministrador() {
        while (true) {
            System.out.println("=== Consola Administrador ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("0. Salir");
            String opcion = pedirCadena("Seleccione una opción");
            if ("0".equals(opcion)) {
                return null;
            }
            if ("1".equals(opcion)) {
                String login = pedirCadena("Login");
                String password = pedirCadena("Contraseña");
                UsuarioGenerico usuario = this.sistema.getUsuarios().get(login);
                if (usuario instanceof Administrador && password.equals(usuario.getPassword())) {
                    return (Administrador) usuario;
                }
                System.out.println("Credenciales inválidas o el usuario no es administrador.");
            } else {
                System.out.println("Opción inválida.");
            }
        }
    }

    public static void main(String[] args) {
        ConsolaAdministradorApp app = new ConsolaAdministradorApp();
        app.ejecutar();
    }
}
