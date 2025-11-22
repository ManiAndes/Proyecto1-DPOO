package persistencia;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dpoo.proyecto.app.MasterTicket;

public class CentralPersistencia {

    public static final String JSON = "JSON";

    // Carpeta y archivo por defecto para persistencia
    public static final String DEFAULT_DIR = "Proyecto1-DPOO/datos";
    public static final String DEFAULT_FILE = "masterticket.json";

    public static IPersistenciaMasterticket getPersistenciaMasterticket(String tipoArchivo) {
        //Valiida que el tipo del archivo donde se va a crear todo sea JSON
        try {
            if (JSON.equals(tipoArchivo))
                return new PersistenciaMasterticket(); //no tiene atributos, es como una clase de metodos (hereda de interfaz)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Path defaultPath() {
        //retorna un objeto path con la direccion del archivo, NO SOLO LA CARPETA   
        return Paths.get(DEFAULT_DIR, DEFAULT_FILE);
    }

    private static void ensureDefaultDir() {
        //Basicamente te crea un directorio en el path que le pongas, para evitar errores de file not found
        //SI ya existia el directorio, no hace nada, y todo funciona correctamente
        //SOLO CREA CARPETAS, NO ARCHIVOS
        try {
            Files.createDirectories(Paths.get(DEFAULT_DIR));
        } catch (Exception e) {
        }
    }

    // Guarda el estado en datos/masterticket.json
    public void saveDefault(MasterTicket sistema) {
        //MIrar salvarMasterTickjet en PeristenciaMastertickjet para ver bien como funciona
        try {
            ensureDefaultDir();
            IPersistenciaMasterticket p = getPersistenciaMasterticket(JSON); //p es Persistencia Masterticket
            if (p != null && sistema != null) {
                p.salvarMasterTicket(defaultPath().toString(), sistema);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void loadDefault(MasterTicket sistema) {
    // Crea al path (carpetas) donde va a almacenar la persistencia, o si ya existe no hace nada
    // Revisa si el path ya tenia persistencia, si no, crea una DEmo
    // En vaos de que si, carga todo desde la persistencia creada antes
    // Revisa de nuevo si todo fue cargado con exito, en caso de que no, inicializa la demo
    // Copia y pega lo cargado al sistema que recibe como entrada.
        try {
            ensureDefaultDir();//Garantizar o crear las carpetas donde va datos json
            if (sistema == null) {
                return;
            } 

            Path pth = defaultPath();
            IPersistenciaMasterticket p = getPersistenciaMasterticket(JSON); //Me devuelve un PersistenciaMasterTicket (hereda de IPers.)
            
            if (p == null) {//caso de error
                return;
            }

            if (! Files.exists(pth)) { //En caso de que no haya ningun masterticket.json
                sistema.inicializarDemo();
                saveDefault(sistema);
                return;
            }
            
            MasterTicket cargado = p.cargarMasterTicket(pth.toString()); //
            
            if (cargado == null) {
                sistema.inicializarDemo();
                saveDefault(sistema);

            } else {
                copiarEstado(sistema, cargado);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copiarEstado(MasterTicket destino, MasterTicket fuente) {
        //Copy paste de dos instancias diferentes
        try {

            destino.setCostoPorEmision(fuente.getCostoPorEmision());
            destino.setUsuarios(fuente.getUsuarios());
            destino.setEventos(fuente.getEventos());
            destino.setVenues(fuente.getVenues());
            destino.setVenuesPendientes(fuente.getVenuesPendientes());
            destino.setSolicitudesReembolso(fuente.getSolicitudesReembolso());
            destino.setSolicitudesReembolsoProcesadas(fuente.getSolicitudesReembolsoProcesadas());
            destino.setIndiceTiquetes(fuente.getIndiceTiquetes());
            destino.setSecuenciaTiquetes(fuente.getSecuenciaTiquetes());
            destino.setSecuenciaSolicitudes(fuente.getSecuenciaSolicitudes());
            destino.setMarketplaceReventa(fuente.getMarketplaceReventa());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
