package tests;

import dpoo.proyecto.app.MasterTicket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistencia.PersistenciaMasterticket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PersistenciaMasterticketTest {

    private Path testDataDir;
    private Path archivoTemporal;
    private PersistenciaMasterticket persistencia;

    @BeforeEach
    void setUp() throws Exception {
        testDataDir = Paths.get("test-data");
        Files.createDirectories(testDataDir);
        archivoTemporal = testDataDir.resolve("masterticket_it.json");
        Files.deleteIfExists(archivoTemporal);
        persistencia = new PersistenciaMasterticket();
    }

    @AfterEach
    void cleanUp() throws Exception {
        Files.deleteIfExists(archivoTemporal);
    }

    @Test
    void guardarYCargarMantieneEventosUsuariosYCosto() {
        MasterTicket original = new MasterTicket();
        original.inicializarDemo();
        original.setCostoPorEmision(7777);

        persistencia.salvarMasterTicket(archivoTemporal.toString(), original);
        MasterTicket cargado = persistencia.cargarMasterTicket(archivoTemporal.toString());

        assertEquals(original.getCostoPorEmision(), cargado.getCostoPorEmision());
        assertEquals(original.getEventos().keySet(), cargado.getEventos().keySet());
        assertEquals(original.getUsuarios().keySet(), cargado.getUsuarios().keySet());
    }

    @Test
    void cargarPlantillaBaseDesdeTestDataNoFalla() {
        Path plantilla = testDataDir.resolve("masterticket_base.json");
        assertTrue(Files.exists(plantilla), "Debe existir el archivo base en test-data.");

        MasterTicket cargado = persistencia.cargarMasterTicket(plantilla.toString());
        assertNotNull(cargado);
        assertEquals(0, cargado.getEventos().size());
        assertEquals(0, cargado.getUsuarios().size());
    }
}
