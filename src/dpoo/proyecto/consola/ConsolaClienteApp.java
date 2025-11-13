package dpoo.proyecto.consola;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Usuario;
import dpoo.proyecto.usuarios.UsuarioGenerico;
import persistencia.CentralPersistencia;

public class ConsolaClienteApp extends ConsolaBasica {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;

    public ConsolaClienteApp() {
        this.persistencia = new CentralPersistencia();
        this.sistema = new MasterTicket();
        this.persistencia.loadDefault(this.sistema);
    }

    public void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("=== Consola Cliente ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("0. Salir");
            String opcion = pedirCadena("Seleccione una opción");
            switch (opcion) {
                case "1":
                    Natural cliente = autenticarCliente();
                    if (cliente != null) {
                        ejecutarMenuCliente(cliente);
                    }
                    break;
                case "2":
                    registrarCliente();
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

    private Natural autenticarCliente() {
        String login = pedirCadena("Login");
        String password = pedirCadena("Contraseña");
        UsuarioGenerico posible = this.sistema.getUsuarios().get(login);
        if (posible instanceof Natural && password.equals(posible.getPassword())) {
            return (Natural) posible;
        }
        System.out.println("Credenciales inválidas o el usuario no es cliente natural.");
        return null;
    }

    private void registrarCliente() {
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
        Natural nuevo = new Natural(login, password);
        this.sistema.getUsuarios().put(login, nuevo);
        System.out.println("Registro exitoso. Ahora puedes iniciar sesión.");
    }

    private void ejecutarMenuCliente(Usuario cliente) {
        ConsolaUsuario consola = new ConsolaUsuario(this.sistema, cliente);
        boolean enSesion = true;
        while (enSesion) {
            consola.consolaUsuario();
            String opcion = pedirCadena("Opción");
            switch (opcion) {
                case "0":
                    enSesion = false;
                    break;
                case "1":
                    consola.comprarEvento();
                    break;
                case "2":
                    consola.verSaldo();
                    break;
                case "3":
                    consola.verMisEventos();
                    break;
                case "4":
                    consola.verMisTiquetes();
                    break;
                case "5":
                    consola.transferirTiquete();
                    break;
                case "6":
                    consola.publicarOfertaReventa();
                    break;
                case "7":
                    consola.verOfertasMarketplace();
                    break;
                case "8":
                    consola.gestionarMisOfertas();
                    break;
                case "9":
                    consola.gestionarContraofertasRecibidas();
                    break;
                case "10":
                    consola.verMisContraofertas();
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
            this.persistencia.saveDefault(this.sistema);
        }
    }

    public static void main(String[] args) {
        ConsolaClienteApp app = new ConsolaClienteApp();
        app.ejecutar();
    }
}
