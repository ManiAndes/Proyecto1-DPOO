package dpoo.proyecto.consola;

public class ConsolaMasterTicket extends ConsolaBasica {

    public void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("=== MasterTicket ===");
            System.out.println("Seleccione la consola a iniciar:");
            System.out.println("1. Administrador");
            System.out.println("2. Organizador");
            System.out.println("3. Cliente");
            System.out.println("0. Salir");
            String opcion = pedirCadena("Opción");
            switch (opcion) {
                case "1":
                    new ConsolaAdministradorApp().ejecutar();
                    break;
                case "2":
                    new ConsolaOrganizadorApp().ejecutar();
                    break;
                case "3":
                    new ConsolaClienteApp().ejecutar();
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
        System.out.println("MasterTicket finalizado.");
    }

    public static void main(String[] args) {
        ConsolaMasterTicket master = new ConsolaMasterTicket();
        master.ejecutar();
    }
}
