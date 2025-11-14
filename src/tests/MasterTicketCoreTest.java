package tests;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.usuarios.Organizador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MasterTicketCoreTest {

    private MasterTicket sistema;

    @BeforeEach
    void setUp() {
        sistema = new MasterTicket();
        sistema.inicializarDemo();
    }

    @Test
    void inicializarDemoRegistraUsuariosEventosYDatosBasicos() {
        assertTrue(sistema.getUsuarios().containsKey("admin1"));
        assertTrue(sistema.getUsuarios().containsKey("organizador1"));
        assertTrue(sistema.getUsuarios().containsKey("cliente1"));
        assertEquals(1, sistema.getEventos().size());

        Evento evento = sistema.selectorEvento("CONCIERTO_DEMO");
        assertNotNull(evento);
        assertFalse(evento.isCancelado());
        assertTrue(evento.getLocalidades().containsKey("GENERAL"));

        assertFalse(sistema.getIndiceTiquetes().isEmpty());
        assertTrue(sistema.getSecuenciaTiquetes() >= 1000);
    }

    @Test
    void aprobarVenueMueveElRegistroDePendienteAAprobado() {
        Organizador organizador = (Organizador) sistema.getUsuarios().get("organizador1");

        Venue venue = new Venue();
        venue.setNombre("COLISEO_TEST");
        venue.setCapacidad(200);
        venue.setUbicacion("Ciudad Test");
        venue.setOrganizador(organizador);

        sistema.proponerVenue(venue);
        assertTrue(sistema.getVenuesPendientes().containsKey("COLISEO_TEST"));

        boolean aprobado = sistema.aprobarVenue("coliseo_test");
        assertTrue(aprobado);
        assertTrue(sistema.getVenues().get("COLISEO_TEST").isAprobado());
        assertFalse(sistema.getVenuesPendientes().containsKey("COLISEO_TEST"));
    }
}
