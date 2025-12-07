package tests;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Natural;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorReembolsoTest {

    private MasterTicket sistema;
    private Administrador admin;
    private Natural comprador;
    private Evento eventoDemo;
    private Localidad localidadGeneral;

    @BeforeEach
    void setUp() {
        sistema = new MasterTicket();
        sistema.inicializarDemo();
        admin = (Administrador) sistema.getUsuarios().get("admin1");
        comprador = (Natural) sistema.getUsuarios().get("cliente1");
        eventoDemo = sistema.selectorEvento("CONCIERTO_DEMO");
        localidadGeneral = eventoDemo.getLocalidades().get("GENERAL");
    }

    @Test
    void aprobarReembolsoTotalMueveSolicitudYAumentaSaldo() {
        Tiquete tiquete = registrarVentaDemo();
        double saldoInicial = comprador.getSaldoVirtual();

        SolicitudReembolso solicitud = sistema.crearSolicitudReembolso(tiquete, comprador, "Prueba");

        boolean aprobado = admin.aprobarReembolso(sistema, solicitud.getId(), 3);
        assertTrue(aprobado);
        assertTrue(tiquete.isReembolsado());
        assertEquals("REEMBOLSADO", tiquete.getEstado());
        assertTrue(comprador.getSaldoVirtual() > saldoInicial);
        assertTrue(sistema.getSolicitudesReembolsoProcesadas().containsKey(solicitud.getId()));
        assertFalse(sistema.getSolicitudesReembolso().containsKey(solicitud.getId()));
    }

    @Test
    void cancelarEventoMarcaTiquetesYRestituyePrecioBase() {
        Tiquete tiquete = registrarVentaDemo();
        double saldoInicial = comprador.getSaldoVirtual();

        admin.cancelarEvento(eventoDemo, 2, sistema);

        assertTrue(eventoDemo.isCancelado());
        assertTrue(tiquete.isReembolsado());
        assertEquals("REEMBOLSADO", tiquete.getEstado());
        assertEquals(saldoInicial + tiquete.getPrecioOriginal(), comprador.getSaldoVirtual(), 0.0001);
    }

    private Tiquete registrarVentaDemo() {
        Tiquete tiquete = localidadGeneral.getTiquetes().values().iterator().next();
        localidadGeneral.marcarVendido(tiquete);
        eventoDemo.marcarVendido(tiquete);
        tiquete.setEvento(eventoDemo);
        tiquete.setLocalidad(localidadGeneral.getNombreLocalidad());
        tiquete.setCliente(comprador);
        double pagado = tiquete.calcularPrecioFinal(eventoDemo.getCargoPorcentualServicio(), tiquete.getCuotaAdicionalEmision());
        tiquete.setMontoPagado(pagado);
        comprador.agregarTiquete(tiquete);
        return tiquete;
    }
}
