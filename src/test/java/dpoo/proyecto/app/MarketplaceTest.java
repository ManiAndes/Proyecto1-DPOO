package dpoo.proyecto.app;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import dpoo.proyecto.app.Contraoferta;
import dpoo.proyecto.app.OfertaReventa;
import dpoo.proyecto.app.TransaccionReventa;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Usuario;

public class MarketplaceTest {

    private MasterTicket sistema;
    private Natural vendedor;
    private Natural comprador;

    @Before
    public void setUp() {
        sistema = new MasterTicket();
        sistema.inicializarDemo();
        vendedor = (Natural) sistema.getUsuarios().get("cliente1");
        comprador = (Natural) sistema.getUsuarios().get("cliente2");
        assertNotNull("El seed debe contener el cliente vendedor", vendedor);
        assertNotNull("El seed debe contener el cliente comprador", comprador);
        assertFalse("El vendedor debe tener tiquetes seed", vendedor.getMisTiquetes().isEmpty());
    }

    @Test
    public void crearYRetirarOferta() {
        int idTiquete = seleccionarTiqueteLibre().getId();
        OfertaReventa oferta = sistema.crearOfertaReventa(vendedor.getLogin(),
                Collections.singletonList(idTiquete), 120000);
        assertNotNull("La oferta debe crearse", oferta);
        assertEquals(OfertaReventa.Estado.PUBLICADA, oferta.getEstado());
        assertEquals(1, sistema.listarOfertasActivas().size());

        boolean retirada = sistema.retirarOferta(oferta.getId(), vendedor.getLogin());
        assertTrue("El vendedor puede retirar su oferta", retirada);
        assertEquals(OfertaReventa.Estado.RETIRADA, oferta.getEstado());
        assertTrue("No debe quedar publicada", sistema.listarOfertasActivas().isEmpty());
    }

    @Test
    public void noPermiteOfertaConPaqueteDeluxe() {
        Tiquete base = seleccionarTiqueteLibre();
        PaqueteDeluxe deluxe = new PaqueteDeluxe(500000, 10000, "2031-01-01", "20:00", 2,
                "DELUXE", "Pack", new ArrayList<>(), new ArrayList<>());
        deluxe.setEvento(base.getEvento());
        deluxe.setCliente(vendedor);
        deluxe.setId(999999);
        sistema.registrarTiquete(deluxe);
        vendedor.agregarTiquete(deluxe);

        OfertaReventa oferta = sistema.crearOfertaReventa(vendedor.getLogin(),
                Collections.singletonList(deluxe.getId()), 200000);
        assertNull("No se pueden publicar paquetes deluxe", oferta);
    }

    @Test
    public void aceptarContraofertaTransfiereTiquete() {
        Tiquete tiquete = seleccionarTiqueteLibre();
        OfertaReventa oferta = sistema.crearOfertaReventa(vendedor.getLogin(),
                Collections.singletonList(tiquete.getId()), 130000);
        assertNotNull(oferta);

        Contraoferta contra = sistema.crearContraoferta(oferta.getId(), comprador.getLogin(), 100000);
        assertNotNull(contra);

        double saldoCompradorAntes = comprador.getSaldoVirtual();
        double saldoVendedorAntes = vendedor.getSaldoVirtual();
        int logAntes = sistema.obtenerLogAuditoria().size();

        boolean aceptada = sistema.responderContraoferta(contra.getId(), vendedor.getLogin(), true);
        assertTrue("El vendedor acepta la contraoferta", aceptada);
        assertEquals(OfertaReventa.Estado.VENDIDA, oferta.getEstado());
        assertEquals(Contraoferta.Estado.ACEPTADA, contra.getEstado());
        assertFalse("El vendedor ya no debe tener el tiquete",
                contieneTiquete(vendedor, tiquete.getId()));
        assertTrue("El comprador recibe el tiquete",
                contieneTiquete(comprador, tiquete.getId()));

        double saldoCompradorDespues = comprador.getSaldoVirtual();
        double saldoVendedorDespues = vendedor.getSaldoVirtual();
        assertTrue("El comprador usa saldo virtual",
                saldoCompradorAntes >= saldoCompradorDespues);
        assertEquals("El vendedor recibe el precio negociado",
                saldoVendedorAntes + contra.getPrecioPropuesto(), saldoVendedorDespues, 0.001);

        List<TransaccionReventa> transacciones = sistema.listarTransaccionesReventa();
        assertEquals("Debe existir una transacción", 1, transacciones.size());
        assertEquals(contra.getPrecioPropuesto(), transacciones.get(0).getPrecioFinal(), 0.001);
        assertTrue("Se registran logs adicionales",
                sistema.obtenerLogAuditoria().size() > logAntes);
    }

    @Test
    public void compraDirectaActualizaSaldoYLog() {
        Tiquete tiquete = seleccionarTiqueteLibre();
        OfertaReventa oferta = sistema.crearOfertaReventa(vendedor.getLogin(),
                Collections.singletonList(tiquete.getId()), 90000);
        assertNotNull(oferta);

        double saldoCompradorAntes = comprador.getSaldoVirtual();
        double saldoVendedorAntes = vendedor.getSaldoVirtual();
        int logAntes = sistema.obtenerLogAuditoria().size();

        TransaccionReventa tx = sistema.comprarOferta(oferta.getId(), comprador.getLogin());
        assertNotNull("Debe concretarse la compra", tx);

        assertEquals("El vendedor recibe el pago",
                saldoVendedorAntes + oferta.getPrecioPedido(), vendedor.getSaldoVirtual(), 0.001);
        double saldoCompradorDespues = comprador.getSaldoVirtual();
        assertTrue("Se descuenta saldo del comprador",
                saldoCompradorDespues <= saldoCompradorAntes);
        assertTrue("Log incorpora los eventos de compra y venta",
                sistema.obtenerLogAuditoria().size() >= logAntes + 2);
    }

    private boolean contieneTiquete(Usuario usuario, int id) {
        for (Tiquete t : usuario.getMisTiquetes()) {
            if (t.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private Tiquete seleccionarTiqueteLibre() {
        Set<Integer> ocupados = new HashSet<>();
        for (OfertaReventa activa : sistema.listarOfertasActivas()) {
            ocupados.addAll(activa.getTiqueteIds());
        }
        for (Tiquete t : vendedor.getMisTiquetes()) {
            if (!ocupados.contains(t.getId())) {
                return t;
            }
        }
        fail("No hay tiquetes disponibles para las pruebas");
        return null;
    }
}
