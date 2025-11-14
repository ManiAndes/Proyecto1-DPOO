package tests;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteNumerado;
import dpoo.proyecto.usuarios.Organizador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrganizadorFlowsTest {

    private MasterTicket sistema;
    private Organizador organizador;

    @BeforeEach
    void setUp() {
        sistema = new MasterTicket();
        sistema.inicializarDemo();
        organizador = (Organizador) sistema.getUsuarios().get("organizador1");
    }

    @Test
    void organizadorPuedeCrearEventoLocalidadYTiquetes() {
        Venue venue = organizador.crearVenue(sistema, "TEATRO_CENTRAL", 150, "Bogota");
        venue.setAprobado(true);

        Evento evento = organizador.crearEvento(
                sistema,
                "GALA_PRUEBA",
                "TEATRO",
                "NUMERADO",
                0,
                venue,
                "2025-06-10"
        );

        Localidad localidad = organizador.crearLocalidad(evento, "PLATEA", 90000, true);
        assertEquals("PLATEA", localidad.getNombreLocalidad());
        assertTrue(evento.getLocalidades().containsKey("PLATEA"));

        int tiquetesEmitidos = 5;
        for (int i = 0; i < tiquetesEmitidos; i++) {
            Tiquete tiquete = new TiqueteNumerado(
                    localidad.getPrecioTiquetes(),
                    sistema.getCostoPorEmision(),
                    evento.getFecha(),
                    "21:00",
                    4,
                    "VIP",
                    i + 1
            );
            tiquete.setEvento(evento);
            tiquete.setLocalidad(localidad.getNombreLocalidad());
            tiquete.setId(sistema.siguienteIdTiquete());
            localidad.addTiquete(tiquete);
            evento.addTiquete(tiquete);
            sistema.registrarTiquete(tiquete);
        }

        evento.setCantidadTiquetesDisponibles(tiquetesEmitidos);

        assertEquals(tiquetesEmitidos, localidad.getTiquetes().size());
        assertEquals(tiquetesEmitidos, evento.getTiquetes().size());
        assertEquals(tiquetesEmitidos, evento.getCantidadTiquetesDisponibles());
        assertTrue(organizador.getEventos().contains(evento));
        Tiquete ultimoRegistrado = sistema.getIndiceTiquetes().get(sistema.getSecuenciaTiquetes());
        assertNotNull(ultimoRegistrado);
        assertEquals(localidad.getNombreLocalidad(), ultimoRegistrado.getLocalidad());
    }
}
