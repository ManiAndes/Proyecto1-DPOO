package dpoo.proyecto.consola;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.usuarios.UsuarioGenerico;
import persistencia.CentralPersistencia;

public class ConsolaOrganizadorApp extends ConsolaBasica {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;

    public ConsolaOrganizadorApp() {
        this.persistencia = new CentralPersistencia();
        this.sistema = new MasterTicket();
        this.persistencia.loadDefault(this.sistema);
    }

    public void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("=== Consola Organizador ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("0. Salir");
            String opcion = pedirCadena("Seleccione una opción");
            switch (opcion) {
                case "1":
                    Organizador org = autenticarOrganizador();
                    if (org != null) {
                        if (!org.isAprobado()) {
                            System.out.println("Tu registro está pendiente de aprobación por el administrador.");
                        } else {
                            ejecutarMenuOrganizador(org);
                        }
                    }
                    break;
                case "2":
                    registrarOrganizador();
                    this.persistencia.saveDefault(this.sistema);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private Organizador autenticarOrganizador() {
        String login = pedirCadena("Login");
        String password = pedirCadena("Contraseña");
        UsuarioGenerico posible = this.sistema.getUsuarios().get(login);
        if (posible instanceof Organizador && password.equals(posible.getPassword())) {
            return (Organizador) posible;
        }
        System.out.println("Credenciales inválidas o el usuario no es organizador.");
        return null;
    }

    private void registrarOrganizador() {
        String login = pedirCadena("Nuevo login");
        if (login == null || login.isBlank()) {
            System.out.println("Login inválido.");
            return;
        }
        if (this.sistema.getUsuarios().containsKey(login)) {
            System.out.println("Ya existe un usuario con ese login.");
            return;
        }
        String password = pedirCadena("Nueva contraseña");
        if (password == null || password.isBlank()) {
            System.out.println("Contraseña inválida.");
            return;
        }
        Organizador organizador = new Organizador(login, password);
        organizador.setAprobado(false);
        this.sistema.getUsuarios().put(login, organizador);
        this.sistema.registrarOrganizadorPendiente(organizador);
        System.out.println("Registro exitoso. Espera la aprobación del administrador.");
    }

    private void ejecutarMenuOrganizador(Organizador organizador) {
        ConsolaOrganizador consola = new ConsolaOrganizador(this.sistema, organizador);
        boolean enSesion = true;
        while (enSesion) {
            if (!organizador.isAprobado()) {
                System.out.println("Tu estado cambió a pendiente. Cierra sesión por favor.");
                break;
            }
            consola.showMenuOrganizador();
            String opcion = pedirCadena("Opción");
            if ("0".equals(opcion)) {
                enSesion = false;
            } else {
                enSesion = consola.consolaOrganizador(opcion);
            }
            this.persistencia.saveDefault(this.sistema);
        }
    }

    public static void main(String[] args) {
        ConsolaOrganizadorApp app = new ConsolaOrganizadorApp();
        app.ejecutar();
    }
}
