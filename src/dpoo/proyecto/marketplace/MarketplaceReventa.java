package dpoo.proyecto.marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dpoo.proyecto.tiquetes.PaqueteDeluxe;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Usuario;
import dpoo.proyecto.usuarios.UsuarioGenerico;

public class MarketplaceReventa {

    private final Map<Integer, OfertaReventa> ofertas;
    private final Map<Integer, Integer> ofertaPorTiquete;
    private final List<RegistroReventa> registros;

    private int secuenciaOfertas;
    private int secuenciaContraofertas;

    public MarketplaceReventa() {
        this.ofertas = new HashMap<>();
        this.ofertaPorTiquete = new HashMap<>();
        this.registros = new ArrayList<>();
        this.secuenciaOfertas = 1;
        this.secuenciaContraofertas = 1;
    }

    public synchronized OfertaReventa publicarOferta(Usuario vendedor, Tiquete tiquete, double precio) {
        if (vendedor == null || tiquete == null) {
            throw new IllegalArgumentException("Vendedor y tiquete son obligatorios.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        validarTiqueteParaVenta(vendedor, tiquete);

        int id = secuenciaOfertas++;
        OfertaReventa oferta = new OfertaReventa(id, tiquete, vendedor, precio);
        ofertas.put(id, oferta);
        ofertaPorTiquete.put(tiquete.getId(), id);
        vendedor.registrarTiqueteEnReventa(tiquete.getId());
        tiquete.setEstado("EN_REVENTA");
        registrar("Oferta #" + id + " creada por " + vendedor.getLogin() + " para tiquete " + tiquete.getId()
                + " a precio " + precio);
        return oferta;
    }

    public synchronized boolean cancelarOferta(int ofertaId, Usuario vendedor) {
        OfertaReventa oferta = ofertas.get(ofertaId);
        if (oferta == null) {
            throw new IllegalArgumentException("La oferta no existe.");
        }
        if (vendedor == null || oferta.getVendedor() == null || !oferta.getVendedor().equals(vendedor)) {
            throw new IllegalArgumentException("Solo el vendedor puede cancelar su oferta.");
        }
        if (!oferta.estaActiva()) {
            return false;
        }
        oferta.marcarCerrada("CANCELADA_POR_VENDEDOR");
        liberarTiquete(oferta);
        registrar("Oferta #" + ofertaId + " cancelada por " + vendedor.getLogin());
        return true;
    }

    public synchronized boolean eliminarOfertaComoAdmin(int ofertaId, UsuarioGenerico administrador, String motivo) {
        OfertaReventa oferta = ofertas.get(ofertaId);
        if (oferta == null) {
            throw new IllegalArgumentException("La oferta no existe.");
        }
        if (!(administrador instanceof Administrador)) {
            throw new IllegalArgumentException("Solo el administrador puede ejecutar esta acción.");
        }
        if (!oferta.estaActiva()) {
            return false;
        }
        oferta.marcarCerrada("ELIMINADA_ADMIN");
        liberarTiquete(oferta);
        registrar("Oferta #" + ofertaId + " eliminada por admin " + administrador.getLogin()
                + (motivo != null ? " (" + motivo + ")" : ""));
        return true;
    }

    public synchronized ResultadoCompraMarketplace comprarOferta(int ofertaId, Usuario comprador) {
        OfertaReventa oferta = validarOfertaActiva(ofertaId);
        if (comprador == null) {
            throw new IllegalArgumentException("Comprador requerido.");
        }
        if (oferta.getVendedor() != null && oferta.getVendedor().equals(comprador)) {
            throw new IllegalArgumentException("No puedes comprar tu propia oferta.");
        }
        return completarVenta(oferta, comprador, oferta.getPrecioBase(), "COMPRA_DIRECTA");
    }

    public synchronized ContraofertaReventa crearContraoferta(int ofertaId, Usuario comprador, double monto) {
        OfertaReventa oferta = validarOfertaActiva(ofertaId);
        if (comprador == null) {
            throw new IllegalArgumentException("Comprador requerido para contraofertar.");
        }
        if (oferta.getVendedor() != null && oferta.getVendedor().equals(comprador)) {
            throw new IllegalArgumentException("No puedes contraofertar tu propia oferta.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        int id = secuenciaContraofertas++;
        ContraofertaReventa contraoferta = new ContraofertaReventa(id, comprador, monto);
        oferta.agregarContraoferta(contraoferta);
        registrar("Contraoferta #" + id + " de " + comprador.getLogin() + " sobre oferta #" + ofertaId
                + " por " + monto);
        return contraoferta;
    }

    public synchronized ResultadoCompraMarketplace aceptarContraoferta(int ofertaId, int contraofertaId, Usuario vendedor) {
        OfertaReventa oferta = validarOfertaActiva(ofertaId);
        if (vendedor == null || oferta.getVendedor() == null || !oferta.getVendedor().equals(vendedor)) {
            throw new IllegalArgumentException("Solo el vendedor puede aceptar contraofertas.");
        }
        ContraofertaReventa contraoferta = buscarContraoferta(oferta, contraofertaId);
        if (!ContraofertaReventa.PENDIENTE.equals(contraoferta.getEstado())) {
            throw new IllegalArgumentException("La contraoferta seleccionada ya fue resuelta.");
        }
        ResultadoCompraMarketplace resultado = completarVenta(oferta, contraoferta.getComprador(),
                contraoferta.getMonto(), "CONTRAOFERTA");
        contraoferta.setEstado(ContraofertaReventa.ACEPTADA);
        for (ContraofertaReventa c : oferta.getContraofertas()) {
            if (c.getId() != contraofertaId && ContraofertaReventa.PENDIENTE.equals(c.getEstado())) {
                c.setEstado(ContraofertaReventa.RECHAZADA);
            }
        }
        registrar("Contraoferta #" + contraofertaId + " aceptada por " + vendedor.getLogin()
                + " para oferta #" + ofertaId);
        return resultado;
    }

    public synchronized boolean rechazarContraoferta(int ofertaId, int contraofertaId, Usuario vendedor) {
        OfertaReventa oferta = validarOfertaActiva(ofertaId);
        if (vendedor == null || oferta.getVendedor() == null || !oferta.getVendedor().equals(vendedor)) {
            throw new IllegalArgumentException("Solo el vendedor puede rechazar contraofertas.");
        }
        ContraofertaReventa contraoferta = buscarContraoferta(oferta, contraofertaId);
        if (!ContraofertaReventa.PENDIENTE.equals(contraoferta.getEstado())) {
            throw new IllegalArgumentException("La contraoferta ya fue gestionada.");
        }
        contraoferta.setEstado(ContraofertaReventa.RECHAZADA);
        registrar("Contraoferta #" + contraofertaId + " rechazada por " + vendedor.getLogin()
                + " para oferta #" + ofertaId);
        return true;
    }

    public synchronized List<OfertaReventa> listarOfertasActivas() {
        List<OfertaReventa> activas = new ArrayList<>();
        for (OfertaReventa oferta : ofertas.values()) {
            if (oferta.estaActiva()) {
                activas.add(oferta);
            }
        }
        return activas;
    }

    public synchronized List<OfertaReventa> listarOfertasPorUsuario(Usuario usuario) {
        List<OfertaReventa> propias = new ArrayList<>();
        if (usuario == null) {
            return propias;
        }
        for (OfertaReventa oferta : ofertas.values()) {
            if (oferta.getVendedor() != null && oferta.getVendedor().equals(usuario)) {
                propias.add(oferta);
            }
        }
        return propias;
    }

    public synchronized List<OfertaReventa> listarTodasLasOfertas() {
        return new ArrayList<>(ofertas.values());
    }

    public synchronized OfertaReventa buscarOferta(int ofertaId) {
        return ofertas.get(ofertaId);
    }

    public synchronized List<RegistroReventa> getRegistros() {
        return new ArrayList<>(registros);
    }

    private void validarTiqueteParaVenta(Usuario vendedor, Tiquete tiquete) {
        if (!vendedor.getMisTiquetes().contains(tiquete)) {
            throw new IllegalArgumentException("El tiquete no pertenece al vendedor.");
        }
        if (tiquete instanceof PaqueteDeluxe) {
            throw new IllegalArgumentException("Los paquetes Deluxe no se pueden revender.");
        }
        if (ofertaPorTiquete.containsKey(tiquete.getId())) {
            throw new IllegalArgumentException("El tiquete ya tiene una oferta activa.");
        }
        if (tiquete.isUsado()) {
            throw new IllegalArgumentException("El tiquete ya fue usado.");
        }
        if (tiquete.isReembolsado()) {
            throw new IllegalArgumentException("El tiquete fue reembolsado.");
        }
        if (!tiquete.isTransferible()) {
            throw new IllegalArgumentException("Este tiquete no permite transferencia.");
        }
    }

    private ResultadoCompraMarketplace completarVenta(OfertaReventa oferta, Usuario comprador, double precio, String fuente) {
        if (oferta == null || comprador == null) {
            throw new IllegalArgumentException("Datos incompletos para cerrar la venta.");
        }
        Tiquete tiquete = oferta.getTiquete();
        Usuario vendedor = oferta.getVendedor();
        if (tiquete == null || vendedor == null) {
            throw new IllegalStateException("La oferta no tiene tiquete o vendedor asociado.");
        }

        double saldoComprador = comprador.getSaldoVirtual();
        double saldoUsado = Math.min(saldoComprador, precio);
        double pagoExterno = Math.max(0, precio - saldoUsado);

        comprador.setSaldoVirtual(saldoComprador - saldoUsado);
        vendedor.addSaldoVirtual(precio);

        vendedor.removerTiquete(tiquete);
        comprador.agregarTiquete(tiquete);
        tiquete.setCliente(comprador);
        tiquete.setEstado("REVENDIDO");
        vendedor.removerTiqueteEnReventa(tiquete.getId());

        oferta.marcarVendida(comprador, precio);
        eliminarReferenciasTiquete(tiquete.getId());

        registrar("Venta completada via " + fuente + " en oferta #" + oferta.getId()
                + " entre " + vendedor.getLogin() + " y " + comprador.getLogin()
                + " por " + precio);

        return new ResultadoCompraMarketplace(true, oferta.getId(), tiquete.getId(), precio,
                saldoUsado, pagoExterno, "Venta completada");
    }

    private void liberarTiquete(OfertaReventa oferta) {
        Tiquete tiquete = oferta.getTiquete();
        if (tiquete != null) {
            ofertaPorTiquete.remove(tiquete.getId());
            tiquete.setEstado("ACTIVO");
            if (oferta.getVendedor() != null) {
                oferta.getVendedor().removerTiqueteEnReventa(tiquete.getId());
            }
        }
    }

    private void eliminarReferenciasTiquete(int tiqueteId) {
        ofertaPorTiquete.remove(tiqueteId);
    }

    private OfertaReventa validarOfertaActiva(int ofertaId) {
        OfertaReventa oferta = ofertas.get(ofertaId);
        if (oferta == null) {
            throw new IllegalArgumentException("La oferta no existe.");
        }
        if (!oferta.estaActiva()) {
            throw new IllegalArgumentException("La oferta no está activa.");
        }
        return oferta;
    }

    private ContraofertaReventa buscarContraoferta(OfertaReventa oferta, int contraofertaId) {
        for (ContraofertaReventa c : oferta.getContraofertas()) {
            if (c.getId() == contraofertaId) {
                return c;
            }
        }
        throw new IllegalArgumentException("Contraoferta inexistente.");
    }

    private void registrar(String mensaje) {
        this.registros.add(new RegistroReventa(mensaje));
    }
}
