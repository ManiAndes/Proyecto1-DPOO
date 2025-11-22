package tests;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
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
    void proponerVenueLoRegistraNoAprobadoYEnPendientes() {
        Organizador organizador = (Organizador) sistema.getUsuarios().get("organizador1");

        Venue venue = new Venue();
        venue.setNombre("coliseo_demo");
        venue.setCapacidad(300);
        venue.setUbicacion("Ciudad X");
        venue.setOrganizador(organizador);

        sistema.proponerVenue(venue);

        assertTrue(sistema.getVenues().containsKey("COLISEO_DEMO"));
        Venue registrado = sistema.getVenues().get("COLISEO_DEMO");
        assertEquals("COLISEO_DEMO", registrado.getNombre());
        assertFalse(registrado.isAprobado());
        assertTrue(sistema.getVenuesPendientes().containsKey("COLISEO_DEMO"));
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
    
    @Test
    void rechazarVenuePendienteLoEliminaDeVenuesYPendientes() {
        Organizador organizador = (Organizador) sistema.getUsuarios().get("organizador1");

        Venue venue = new Venue();
        venue.setNombre("COLISEO_RECHAZO");
        venue.setCapacidad(150);
        venue.setUbicacion("Ciudad");
        venue.setOrganizador(organizador);

        sistema.proponerVenue(venue);
        assertTrue(sistema.getVenuesPendientes().containsKey("COLISEO_RECHAZO"));
        assertTrue(sistema.getVenues().containsKey("COLISEO_RECHAZO"));

        boolean rechazado = sistema.rechazarVenue("coliseo_rechazo");
        assertTrue(rechazado);
        assertFalse(sistema.getVenuesPendientes().containsKey("COLISEO_RECHAZO"));
        assertFalse(sistema.getVenues().containsKey("COLISEO_RECHAZO"));
    }
    
    @Test
    void esLoginDisponibleConsideraUsuariosYSolicitudesPendientes() {
        
        assertFalse(sistema.esLoginDisponible("organizador1"));

        assertTrue(sistema.esLoginDisponible("nuevoOrg"));

        boolean registrada = sistema.registrarSolicitudOrganizador("nuevoOrg", "clave123");
        assertTrue(registrada);
        assertFalse(sistema.esLoginDisponible("nuevoOrg"));
    }
    
    @Test
    void crearSolicitudOrganizadorYLuegoAprobarlaCreaUsuarioOrganizador() {
    	
        assertTrue(sistema.esLoginDisponible("nuevoOrg"));

        boolean registrada = sistema.registrarSolicitudOrganizador("nuevoOrg", "pass123");
        assertTrue(registrada);
        assertFalse(sistema.esLoginDisponible("nuevoOrg"));
        
        boolean aprobada = sistema.aprobarSolicitudOrganizador("nuevoOrg");
        assertTrue(aprobada);

        assertTrue(sistema.getUsuarios().get("nuevoOrg") instanceof Organizador);
        
    }
    
    void registrarTiqueteActualizaIndiceYSecuencia() {
        int secuenciaInicial = sistema.getSecuenciaTiquetes();

        TiqueteGeneral tiquete = new TiqueteGeneral(
                100000,
                sistema.getCostoPorEmision(),
                "2025-01-01",
                "20:00",
                4,
                "GENERAL"
        );
        int idNuevo = secuenciaInicial + 10;
        tiquete.setId(idNuevo);

        sistema.registrarTiquete(tiquete);

        Tiquete encontrado = sistema.buscarTiquete(idNuevo);
        assertNotNull(encontrado);
        assertEquals(idNuevo, encontrado.getId());
        assertEquals(idNuevo, sistema.getSecuenciaTiquetes());
    }
    
    @Test
    void marcarEventoCanceladoActualizaEstadoYMapaEventos() {
        Evento evento = sistema.selectorEvento("CONCIERTO_DEMO");
        assertNotNull(evento);
        assertFalse(evento.isCancelado());

        sistema.marcarEventoCancelado(evento);

        assertTrue(evento.isCancelado());
        Evento desdeMapa = sistema.getEventos().get(evento.getNombre());
        assertNotNull(desdeMapa);
        assertTrue(desdeMapa.isCancelado());
    }
    
}
