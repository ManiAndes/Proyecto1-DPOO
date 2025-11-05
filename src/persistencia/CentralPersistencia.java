package persistencia;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dpoo.proyecto.app.MasterTicket;

public class CentralPersistencia {

    public static final String JSON = "JSON";

    // Carpeta y archivo por defecto para persistencia
    public static final String DEFAULT_DIR = "datos";
    public static final String DEFAULT_FILE = "masterticket.json";

    public static IPersistenciaMasterticket getPersistenciaMasterticket(String tipoArchivo) {
        try {
            if (JSON.equals(tipoArchivo))
                return new PersistenciaMasterticket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Path defaultPath() {
        return Paths.get(DEFAULT_DIR, DEFAULT_FILE);
    }

    private static void ensureDefaultDir() {
        try {
            Files.createDirectories(Paths.get(DEFAULT_DIR));
        } catch (Exception e) {
            // Silent: do not block app on dir creation errors
        }
    }

    // Guarda el estado en datos/masterticket.json
    public void saveDefault(MasterTicket sistema) {
        try {
            ensureDefaultDir();
            IPersistenciaMasterticket p = getPersistenciaMasterticket(JSON);
            if (p != null && sistema != null) {
                p.salvarMasterTicket(defaultPath().toString(), sistema);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Carga el estado desde datos/masterticket.json dentro del objeto recibido
    public void loadDefault(MasterTicket sistema) {
        try {
            Path pth = defaultPath();
            if (!Files.exists(pth) || sistema == null) return;
            IPersistenciaMasterticket p = getPersistenciaMasterticket(JSON);
            if (p == null) return;
            MasterTicket cargado = p.cargarMasterTicket(pth.toString());
            if (cargado != null) {
                copiarEstado(sistema, cargado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copiarEstado(MasterTicket destino, MasterTicket fuente) {
        try {
            destino.setCostoPorEmision(fuente.getCostoPorEmision());
            destino.setUsuarios(fuente.getUsuarios());
            destino.setEventos(fuente.getEventos());
            destino.setVenues(fuente.getVenues());
            destino.setVenuesPendientes(fuente.getVenuesPendientes());
            destino.setSolicitudesReembolso(fuente.getSolicitudesReembolso());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
