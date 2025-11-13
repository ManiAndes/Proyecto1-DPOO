package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.tiquetes.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MasterTicket {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
	
	private double costoPorEmision;
	
	// Mapa de los usuarios registrados
	private Map<String, UsuarioGenerico> usuarios;
	
	// Mapa de todos los eventos (activos?)
	private Map<String, Evento> eventos;
	
    // Mapa de todos los venues
    private Map<String, Venue> venues;
    // Venues sugeridos por organizadores pendientes de aprobación
    private Map<String, Venue> venuesPendientes;
    // Solicitudes de reembolsos
    private Map<Integer, SolicitudReembolso> solicitudesReembolso;
    private Map<Integer, SolicitudReembolso> solicitudesReembolsoProcesadas;
    private Map<Integer, Tiquete> indiceTiquetes;
    private int secuenciaTiquetes;
    private int secuenciaSolicitudes;
    private Map<Integer, OfertaReventa> ofertasReventa;
    private Map<Integer, Contraoferta> contraofertas;
    private Map<Integer, TransaccionReventa> transaccionesReventa;
    private Map<Integer, AuditoriaMarketplaceEntry> auditoriaMarketplace;
    private List<Integer> auditoriaOrden;
    private Map<Integer, Integer> indiceOfertaPorTiquete;
    private int secuenciaOfertas;
    private int secuenciaContraofertas;
    private int secuenciaTransacciones;
    private int secuenciaLogMarketplace;
    private Set<String> organizadoresPendientes;
	

	public MasterTicket() {
		super();
		this.usuarios = new HashMap<String, UsuarioGenerico>();
		this.eventos = new HashMap<String, Evento>();
        this.venues = new HashMap<String, Venue>();
        this.venuesPendientes = new HashMap<String, Venue>();
        this.solicitudesReembolso = new HashMap<Integer, SolicitudReembolso>();
        this.solicitudesReembolsoProcesadas = new HashMap<Integer, SolicitudReembolso>();
        this.indiceTiquetes = new HashMap<Integer, Tiquete>();
        this.secuenciaTiquetes = 1000;
        this.secuenciaSolicitudes = 1;
		this.costoPorEmision = 0.0;
        this.ofertasReventa = new HashMap<>();
        this.contraofertas = new HashMap<>();
        this.transaccionesReventa = new HashMap<>();
        this.auditoriaMarketplace = new LinkedHashMap<>();
        this.auditoriaOrden = new ArrayList<>();
        this.indiceOfertaPorTiquete = new HashMap<>();
        this.secuenciaOfertas = 1;
        this.secuenciaContraofertas = 1;
        this.secuenciaTransacciones = 1;
        this.secuenciaLogMarketplace = 1;
        this.organizadoresPendientes = new LinkedHashSet<>();
	}

	public double getCostoPorEmision() {
		return costoPorEmision;
	}

	public void setCostoPorEmision(double costoPorEmision) {
		this.costoPorEmision = costoPorEmision;
	}

	public Map<String, UsuarioGenerico> getUsuarios() {
		return usuarios;
	}

	public Map<String, Evento> getEventos() {
		return eventos;
	}

    public Map<String, Venue> getVenues() {
        return venues;
    }
    public Map<String, Venue> getVenuesPendientes() {
        return venuesPendientes;
    }
    public Map<Integer, SolicitudReembolso> getSolicitudesReembolso() {
        return solicitudesReembolso;
    }

    public Map<Integer, SolicitudReembolso> getSolicitudesReembolsoProcesadas() {
        return solicitudesReembolsoProcesadas;
    }

	public void setUsuarios(Map<String, UsuarioGenerico> usuarios) {
		this.usuarios = (usuarios != null) ? usuarios : new HashMap<>();
	}

	public void setEventos(Map<String, Evento> eventos) {
		this.eventos = (eventos != null) ? eventos : new HashMap<>();
	}

    public void setVenues(Map<String, Venue> venues) {
        this.venues = venues != null ? venues : new HashMap<>();
        refrescarVenuesPendientes();
    }
    public void setVenuesPendientes(Map<String, Venue> venuesPendientes) {
        this.venuesPendientes = venuesPendientes != null ? venuesPendientes : new HashMap<>();
        refrescarVenuesPendientes();
    }
    public void setSolicitudesReembolso(Map<Integer, SolicitudReembolso> solicitudesReembolso) {
        this.solicitudesReembolso = solicitudesReembolso != null ? solicitudesReembolso : new HashMap<>();
    }
    public void setSolicitudesReembolsoProcesadas(Map<Integer, SolicitudReembolso> solicitudesReembolsoProcesadas) {
        this.solicitudesReembolsoProcesadas = solicitudesReembolsoProcesadas != null ? solicitudesReembolsoProcesadas : new HashMap<>();
    }

    public Map<Integer, Tiquete> getIndiceTiquetes() {
        return indiceTiquetes;
    }

    public void setIndiceTiquetes(Map<Integer, Tiquete> indiceTiquetes) {
        this.indiceTiquetes = indiceTiquetes != null ? indiceTiquetes : new HashMap<>();
    }

    public int getSecuenciaTiquetes() {
        return secuenciaTiquetes;
    }

    public void setSecuenciaTiquetes(int secuenciaTiquetes) {
        this.secuenciaTiquetes = secuenciaTiquetes;
    }

    public int getSecuenciaSolicitudes() {
        return secuenciaSolicitudes;
    }

    public void setSecuenciaSolicitudes(int secuenciaSolicitudes) {
        this.secuenciaSolicitudes = secuenciaSolicitudes;
    }

    public List<Organizador> listarOrganizadoresPendientes() {
        List<Organizador> lista = new ArrayList<>();
        for (String login : this.organizadoresPendientes) {
            UsuarioGenerico usuario = this.usuarios.get(login);
            if (usuario instanceof Organizador) {
                lista.add((Organizador) usuario);
            }
        }
        return lista;
    }

    public void registrarOrganizadorPendiente(Organizador organizador) {
        if (organizador == null || organizador.getLogin() == null) return;
        organizador.setAprobado(false);
        this.organizadoresPendientes.add(organizador.getLogin());
    }

    public boolean aprobarOrganizador(String loginOrganizador) {
        if (loginOrganizador == null) return false;
        UsuarioGenerico usuario = this.usuarios.get(loginOrganizador);
        if (usuario instanceof Organizador) {
            ((Organizador) usuario).setAprobado(true);
            this.organizadoresPendientes.remove(loginOrganizador);
            return true;
        }
        return false;
    }

    public boolean rechazarOrganizador(String loginOrganizador) {
        if (loginOrganizador == null) return false;
        UsuarioGenerico usuario = this.usuarios.get(loginOrganizador);
        if (usuario instanceof Organizador) {
            this.organizadoresPendientes.remove(loginOrganizador);
            this.usuarios.remove(loginOrganizador);
            return true;
        }
        return false;
    }
    public Map<Integer, OfertaReventa> getOfertasReventa() {
        return ofertasReventa;
    }

    public void setOfertasReventa(Map<Integer, OfertaReventa> ofertasReventa) {
        this.ofertasReventa = ofertasReventa != null ? ofertasReventa : new HashMap<>();
        reconstruirIndiceOfertas();
    }

    public Map<Integer, Contraoferta> getContraofertas() {
        return contraofertas;
    }

    public void setContraofertas(Map<Integer, Contraoferta> contraofertas) {
        this.contraofertas = contraofertas != null ? contraofertas : new HashMap<>();
    }

    public Map<Integer, TransaccionReventa> getTransaccionesReventaMap() {
        return transaccionesReventa;
    }

    public void setTransaccionesReventa(Map<Integer, TransaccionReventa> transaccionesReventa) {
        this.transaccionesReventa = transaccionesReventa != null ? transaccionesReventa : new HashMap<>();
    }

    public Map<Integer, AuditoriaMarketplaceEntry> getAuditoriaMarketplace() {
        return auditoriaMarketplace;
    }

    public void setAuditoriaMarketplace(Map<Integer, AuditoriaMarketplaceEntry> auditoriaMarketplace) {
        this.auditoriaMarketplace = auditoriaMarketplace != null ? auditoriaMarketplace : new LinkedHashMap<>();
    }

    public List<Integer> getAuditoriaOrden() {
        return auditoriaOrden;
    }

    public void setAuditoriaOrden(List<Integer> auditoriaOrden) {
        this.auditoriaOrden = auditoriaOrden != null ? auditoriaOrden : new ArrayList<>();
    }

    public Map<Integer, Integer> getIndiceOfertaPorTiquete() {
        return indiceOfertaPorTiquete;
    }

    public void setIndiceOfertaPorTiquete(Map<Integer, Integer> indiceOfertaPorTiquete) {
        this.indiceOfertaPorTiquete = indiceOfertaPorTiquete != null ? indiceOfertaPorTiquete : new HashMap<>();
    }

    public int getSecuenciaOfertas() {
        return secuenciaOfertas;
    }

    public void setSecuenciaOfertas(int secuenciaOfertas) {
        this.secuenciaOfertas = secuenciaOfertas;
    }

    public int getSecuenciaContraofertas() {
        return secuenciaContraofertas;
    }

    public void setSecuenciaContraofertas(int secuenciaContraofertas) {
        this.secuenciaContraofertas = secuenciaContraofertas;
    }

    public int getSecuenciaTransacciones() {
        return secuenciaTransacciones;
    }

    public void setSecuenciaTransacciones(int secuenciaTransacciones) {
        this.secuenciaTransacciones = secuenciaTransacciones;
    }

    public int getSecuenciaLogMarketplace() {
        return secuenciaLogMarketplace;
    }

    public void setSecuenciaLogMarketplace(int secuenciaLogMarketplace) {
        this.secuenciaLogMarketplace = secuenciaLogMarketplace;
    }

    public Set<String> getOrganizadoresPendientes() {
        return organizadoresPendientes;
    }

    public void setOrganizadoresPendientes(Set<String> organizadoresPendientes) {
        this.organizadoresPendientes = organizadoresPendientes != null ? organizadoresPendientes : new LinkedHashSet<>();
    }

    private void refrescarVenuesPendientes() {
        Map<String, Venue> pend = new HashMap<>();
        if (this.venues == null) {
            this.venues = new HashMap<>();
        }
        for (Map.Entry<String, Venue> entry : this.venues.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isAprobado()) {
                pend.put(entry.getKey(), entry.getValue());
            }
        }
        this.venuesPendientes = pend;
    }

    public synchronized int siguienteIdTiquete() {
        return ++secuenciaTiquetes;
    }

    public synchronized int siguienteIdSolicitud() {
        return ++secuenciaSolicitudes;
    }

    public synchronized int siguienteIdOferta() {
        return ++secuenciaOfertas;
    }

    public synchronized int siguienteIdContraoferta() {
        return ++secuenciaContraofertas;
    }

    public synchronized int siguienteIdTransaccion() {
        return ++secuenciaTransacciones;
    }

    public synchronized int siguienteIdLogMarketplace() {
        return ++secuenciaLogMarketplace;
    }

    public void registrarTiquete(Tiquete tiquete) {
        if (tiquete != null) {
            indiceTiquetes.put(tiquete.getId(), tiquete);
            if (tiquete.getId() > secuenciaTiquetes) {
                secuenciaTiquetes = tiquete.getId();
            }
        }
    }

    public List<AuditoriaMarketplaceEntry> obtenerLogAuditoria() {
        List<AuditoriaMarketplaceEntry> lista = new ArrayList<>();
        for (Integer id : this.auditoriaOrden) {
            AuditoriaMarketplaceEntry entry = this.auditoriaMarketplace.get(id);
            if (entry != null) {
                lista.add(entry);
            }
        }
        return lista;
    }

    public AuditoriaMarketplaceEntry registrarEventoAuditoria(String actor, String rol, String accion,
                                                             String recurso, String resultado, String detalle) {
        AuditoriaMarketplaceEntry entry = new AuditoriaMarketplaceEntry(
                siguienteIdLogMarketplace(), actor, rol, accion, recurso, resultado, detalle);
        this.auditoriaMarketplace.put(entry.getId(), entry);
        this.auditoriaOrden.add(entry.getId());
        return entry;
    }

    // === Marketplace de reventa ===
    public OfertaReventa crearOfertaReventa(String vendedorLogin, List<Integer> tiqueteIds, double precioPedido) {
        if (vendedorLogin == null || tiqueteIds == null || tiqueteIds.isEmpty() || precioPedido <= 0) {
            return null;
        }
        UsuarioGenerico vendedorGenerico = this.usuarios.get(vendedorLogin);
        if (!(vendedorGenerico instanceof Usuario)) {
            return null;
        }
        Usuario vendedor = (Usuario) vendedorGenerico;
        List<Integer> validados = new ArrayList<>();
        Set<Integer> vistos = new HashSet<>();
        for (Integer id : tiqueteIds) {
            if (id == null || id <= 0 || vistos.contains(id)) {
                continue;
            }
            Tiquete tiquete = this.indiceTiquetes.get(id);
            if (!tiqueteDisponibleParaReventa(tiquete, vendedor)) {
                return null;
            }
            Integer ofertaExistente = this.indiceOfertaPorTiquete.get(id);
            if (ofertaExistente != null) {
                OfertaReventa activa = this.ofertasReventa.get(ofertaExistente);
                if (activa != null && activa.getEstado() == OfertaReventa.Estado.PUBLICADA) {
                    return null;
                } else {
                    this.indiceOfertaPorTiquete.remove(id);
                }
            }
            validados.add(id);
            vistos.add(id);
        }
        if (validados.isEmpty()) {
            return null;
        }
        int idOferta = siguienteIdOferta();
        OfertaReventa oferta = new OfertaReventa(idOferta, vendedorLogin, validados, precioPedido);
        this.ofertasReventa.put(idOferta, oferta);
        for (Integer id : validados) {
            this.indiceOfertaPorTiquete.put(id, idOferta);
        }
        registrarEventoAuditoria(vendedorLogin, rolDe(vendedorGenerico), "CREAR_OFERTA",
                "Oferta#" + idOferta, "OK", "Tiquetes: " + validados.size());
        return oferta;
    }

    public List<OfertaReventa> listarOfertasActivas() {
        List<OfertaReventa> lista = new ArrayList<>();
        for (OfertaReventa oferta : this.ofertasReventa.values()) {
            if (oferta != null && oferta.getEstado() == OfertaReventa.Estado.PUBLICADA) {
                lista.add(oferta);
            }
        }
        lista.sort(Comparator.comparingInt(OfertaReventa::getId));
        return lista;
    }

    public List<OfertaReventa> listarOfertasDe(String vendedorLogin) {
        List<OfertaReventa> lista = new ArrayList<>();
        if (vendedorLogin == null) return lista;
        for (OfertaReventa oferta : this.ofertasReventa.values()) {
            if (oferta != null && vendedorLogin.equals(oferta.getIdVendedor())) {
                lista.add(oferta);
            }
        }
        lista.sort(Comparator.comparingInt(OfertaReventa::getId));
        return lista;
    }

    public boolean retirarOferta(int idOferta, String vendedorLogin) {
        OfertaReventa oferta = this.ofertasReventa.get(idOferta);
        if (oferta == null || oferta.getEstado() != OfertaReventa.Estado.PUBLICADA) {
            return false;
        }
        if (!Objects.equals(oferta.getIdVendedor(), vendedorLogin)) {
            return false;
        }
        oferta.setEstado(OfertaReventa.Estado.RETIRADA);
        liberarTiquetesDeOferta(oferta);
        cancelarContraofertasPendientes(idOferta);
        UsuarioGenerico vendedor = this.usuarios.get(vendedorLogin);
        registrarEventoAuditoria(vendedorLogin, rolDe(vendedor), "RETIRAR_OFERTA",
                "Oferta#" + idOferta, "OK", "Retiro voluntario");
        return true;
    }

    public boolean eliminarOfertaPorAdmin(int idOferta, String adminLogin) {
        OfertaReventa oferta = this.ofertasReventa.get(idOferta);
        if (oferta == null || oferta.getEstado() != OfertaReventa.Estado.PUBLICADA) {
            return false;
        }
        UsuarioGenerico admin = this.usuarios.get(adminLogin);
        if (!(admin instanceof Administrador)) {
            return false;
        }
        oferta.setEstado(OfertaReventa.Estado.ELIMINADA_ADMIN);
        liberarTiquetesDeOferta(oferta);
        cancelarContraofertasPendientes(idOferta);
        registrarEventoAuditoria(adminLogin, rolDe(admin), "ELIMINAR_OFERTA",
                "Oferta#" + idOferta, "OK", "Eliminada por administraci��n");
        return true;
    }

    public TransaccionReventa comprarOferta(int idOferta, String compradorLogin) {
        OfertaReventa oferta = this.ofertasReventa.get(idOferta);
        if (oferta == null || oferta.getEstado() != OfertaReventa.Estado.PUBLICADA) {
            return null;
        }
        UsuarioGenerico compradorGenerico = this.usuarios.get(compradorLogin);
        if (!(compradorGenerico instanceof Usuario)) {
            return null;
        }
        if (oferta.getIdVendedor().equals(compradorLogin)) {
            return null;
        }
        UsuarioGenerico vendedorGenerico = this.usuarios.get(oferta.getIdVendedor());
        if (!(vendedorGenerico instanceof Usuario)) {
            return null;
        }
        return cerrarVenta(oferta, (Usuario) vendedorGenerico, (Usuario) compradorGenerico,
                oferta.getPrecioPedido(), "VENTA_DIRECTA", null);
    }

    public Contraoferta crearContraoferta(int idOferta, String compradorLogin, double precioPropuesto) {
        if (precioPropuesto <= 0 || compradorLogin == null) {
            return null;
        }
        OfertaReventa oferta = this.ofertasReventa.get(idOferta);
        if (oferta == null || oferta.getEstado() != OfertaReventa.Estado.PUBLICADA) {
            return null;
        }
        if (compradorLogin.equals(oferta.getIdVendedor())) {
            return null;
        }
        UsuarioGenerico compradorGenerico = this.usuarios.get(compradorLogin);
        if (!(compradorGenerico instanceof Usuario)) {
            return null;
        }
        int idContra = siguienteIdContraoferta();
        Contraoferta contraoferta = new Contraoferta(idContra, idOferta, compradorLogin, precioPropuesto);
        this.contraofertas.put(idContra, contraoferta);
        registrarEventoAuditoria(compradorLogin, rolDe(compradorGenerico), "CREAR_CONTRAOFERTA",
                "Oferta#" + idOferta, "OK", "Precio: " + precioPropuesto);
        return contraoferta;
    }

    public boolean responderContraoferta(int idContraoferta, String vendedorLogin, boolean aceptar) {
        Contraoferta contraoferta = this.contraofertas.get(idContraoferta);
        if (contraoferta == null || contraoferta.getEstado() != Contraoferta.Estado.PENDIENTE) {
            return false;
        }
        OfertaReventa oferta = this.ofertasReventa.get(contraoferta.getIdOferta());
        if (oferta == null || oferta.getEstado() != OfertaReventa.Estado.PUBLICADA) {
            return false;
        }
        if (!Objects.equals(oferta.getIdVendedor(), vendedorLogin)) {
            return false;
        }
        UsuarioGenerico vendedorGenerico = this.usuarios.get(vendedorLogin);
        if (!(vendedorGenerico instanceof Usuario)) {
            return false;
        }
        if (!aceptar) {
            contraoferta.marcarRechazada();
            registrarEventoAuditoria(vendedorLogin, rolDe(vendedorGenerico), "RECHAZAR_CONTRAOFERTA",
                    "Contraoferta#" + idContraoferta, "OK", "Se rechaza propuesta");
            return true;
        }
        UsuarioGenerico compradorGenerico = this.usuarios.get(contraoferta.getIdComprador());
        if (!(compradorGenerico instanceof Usuario)) {
            return false;
        }
        TransaccionReventa tx = cerrarVenta(oferta, (Usuario) vendedorGenerico, (Usuario) compradorGenerico,
                contraoferta.getPrecioPropuesto(), "CONTRAOFERTA", contraoferta.getId());
        if (tx == null) {
            return false;
        }
        contraoferta.marcarAceptada();
        registrarEventoAuditoria(vendedorLogin, rolDe(vendedorGenerico), "ACEPTAR_CONTRAOFERTA",
                "Contraoferta#" + idContraoferta, "OK", "Precio: " + contraoferta.getPrecioPropuesto());
        return true;
    }

    public List<Contraoferta> contraofertasPendientesParaVendedor(String vendedorLogin) {
        List<Contraoferta> lista = new ArrayList<>();
        if (vendedorLogin == null) return lista;
        for (Contraoferta contra : this.contraofertas.values()) {
            OfertaReventa oferta = this.ofertasReventa.get(contra.getIdOferta());
            if (oferta == null) continue;
            if (contra.getEstado() == Contraoferta.Estado.PENDIENTE
                    && vendedorLogin.equals(oferta.getIdVendedor())) {
                lista.add(contra);
            }
        }
        lista.sort(Comparator.comparingInt(Contraoferta::getId));
        return lista;
    }

    public List<Contraoferta> contraofertasDeComprador(String compradorLogin) {
        List<Contraoferta> lista = new ArrayList<>();
        if (compradorLogin == null) return lista;
        for (Contraoferta contra : this.contraofertas.values()) {
            if (contra != null && compradorLogin.equals(contra.getIdComprador())) {
                lista.add(contra);
            }
        }
        lista.sort(Comparator.comparingInt(Contraoferta::getId));
        return lista;
    }

    public OfertaReventa obtenerOferta(int idOferta) {
        return this.ofertasReventa.get(idOferta);
    }

    public Contraoferta obtenerContraoferta(int idContraoferta) {
        return this.contraofertas.get(idContraoferta);
    }

    public List<TransaccionReventa> listarTransaccionesReventa() {
        List<TransaccionReventa> lista = new ArrayList<>(this.transaccionesReventa.values());
        lista.sort(Comparator.comparingInt(TransaccionReventa::getId));
        return lista;
    }

    private TransaccionReventa cerrarVenta(OfertaReventa oferta, Usuario vendedor, Usuario comprador,
                                           double precioFinal, String origen, Integer contraofertaAceptadaId) {
        if (oferta == null || vendedor == null || comprador == null || precioFinal <= 0) {
            return null;
        }
        double pagoExterno = debitarSaldo(comprador, precioFinal);
        vendedor.addSaldoVirtual(precioFinal);
        transferirPropiedadTiquetes(oferta.getTiqueteIds(), vendedor, comprador);
        oferta.setEstado(OfertaReventa.Estado.VENDIDA);
        liberarTiquetesDeOferta(oferta);
        cancelarContraofertasPendientes(oferta.getId(), contraofertaAceptadaId);
        TransaccionReventa tx = new TransaccionReventa(
                siguienteIdTransaccion(), oferta.getId(), oferta.getTiqueteIds(),
                vendedor.getLogin(), comprador.getLogin(), precioFinal);
        this.transaccionesReventa.put(tx.getId(), tx);
        registrarEventoAuditoria(vendedor.getLogin(), rolDe(vendedor), origen + "_VENDEDOR",
                "Oferta#" + oferta.getId(), "OK", "Precio " + precioFinal);
        registrarEventoAuditoria(comprador.getLogin(), rolDe(comprador), origen + "_COMPRADOR",
                "Oferta#" + oferta.getId(), "OK",
                pagoExterno > 0 ? "Pago externo " + pagoExterno : "Saldo virtual");
        return tx;
    }

    private void transferirPropiedadTiquetes(List<Integer> tiqueteIds, Usuario vendedor, Usuario comprador) {
        if (tiqueteIds == null) return;
        for (Integer id : tiqueteIds) {
            Tiquete tiquete = this.indiceTiquetes.get(id);
            if (tiquete == null) {
                continue;
            }
            if (vendedor != null && vendedor.getMisTiquetes() != null) {
                Iterator<Tiquete> it = vendedor.getMisTiquetes().iterator();
                while (it.hasNext()) {
                    if (it.next().getId() == id) {
                        it.remove();
                        break;
                    }
                }
            }
            if (comprador != null) {
                comprador.agregarTiquete(tiquete);
            }
            tiquete.setCliente(comprador);
            tiquete.setEstado("ACTIVO");
        }
    }

    private double debitarSaldo(Usuario usuario, double monto) {
        if (usuario == null || monto <= 0) {
            return 0.0;
        }
        double saldo = usuario.getSaldoVirtual();
        double aplicado = Math.min(saldo, monto);
        usuario.setSaldoVirtual(saldo - aplicado);
        return monto - aplicado;
    }

    private void cancelarContraofertasPendientes(int idOferta) {
        cancelarContraofertasPendientes(idOferta, null);
    }

    private void cancelarContraofertasPendientes(int idOferta, Integer contraofertaExcluida) {
        for (Contraoferta contra : this.contraofertas.values()) {
            if (contra == null) continue;
            if (contra.getIdOferta() == idOferta && contra.getEstado() == Contraoferta.Estado.PENDIENTE) {
                if (contraofertaExcluida != null && contra.getId() == contraofertaExcluida) {
                    continue;
                }
                contra.marcarRechazada();
            }
        }
    }

    private void liberarTiquetesDeOferta(OfertaReventa oferta) {
        if (oferta == null) return;
        for (Integer id : oferta.getTiqueteIds()) {
            Integer ofertaRegistrada = this.indiceOfertaPorTiquete.get(id);
            if (ofertaRegistrada != null && ofertaRegistrada == oferta.getId()) {
                this.indiceOfertaPorTiquete.remove(id);
            }
        }
    }

    private boolean tiqueteDisponibleParaReventa(Tiquete tiquete, Usuario vendedor) {
        if (tiquete == null || vendedor == null || tiquete.getCliente() == null) {
            return false;
        }
        if (!vendedor.getLogin().equals(tiquete.getCliente().getLogin())) {
            return false;
        }
        if (!tiquete.isTransferible() || tiquete.isUsado() || tiquete.isReembolsado()) {
            return false;
        }
        if (tiquete instanceof PaqueteDeluxe) {
            return false;
        }
        Evento evento = tiquete.getEvento();
        if (evento != null && evento.isCancelado()) {
            return false;
        }
        return !tiqueteCaducado(tiquete);
    }

    private boolean tiqueteCaducado(Tiquete tiquete) {
        Evento evento = tiquete.getEvento();
        if (evento == null) {
            return false;
        }
        String fecha = evento.getFecha();
        if (fecha == null || fecha.isEmpty()) {
            return false;
        }
        try {
            LocalDate fechaEvento = LocalDate.parse(fecha, ISO_DATE);
            return fechaEvento.isBefore(LocalDate.now());
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private String rolDe(UsuarioGenerico usuario) {
        if (usuario instanceof Administrador) return "ADMIN";
        if (usuario instanceof Organizador) return "ORGANIZADOR";
        if (usuario instanceof Usuario) return "CLIENTE";
        return "DESCONOCIDO";
    }

    public void reconstruirIndiceOfertas() {
        this.indiceOfertaPorTiquete.clear();
        for (OfertaReventa oferta : this.ofertasReventa.values()) {
            if (oferta == null || oferta.getEstado() != OfertaReventa.Estado.PUBLICADA) {
                continue;
            }
            for (Integer id : oferta.getTiqueteIds()) {
                if (id != null) {
                    this.indiceOfertaPorTiquete.put(id, oferta.getId());
                }
            }
        }
    }

    public Tiquete buscarTiquete(int id) {
        return indiceTiquetes.get(id);
    }

    // Utilidad para crear solicitudes de reembolso (prototipo)
    public SolicitudReembolso crearSolicitudReembolso(Tiquete tiquete, Usuario solicitante, String motivo) {
        if (tiquete == null || solicitante == null) return null;
        int id = siguienteIdSolicitud();
        SolicitudReembolso s = new SolicitudReembolso(id, tiquete, solicitante, motivo);
        double cuotaPorcentual = tiquete.getEvento() != null ? tiquete.getEvento().getCargoPorcentualServicio() : 0.0;
        double cuotaEmision = tiquete.getCuotaAdicionalEmision();
        double pagado = tiquete.getMontoPagado() > 0
                ? tiquete.getMontoPagado()
                : tiquete.calcularPrecioFinal(cuotaPorcentual, cuotaEmision);
        s.setMontoSolicitado(pagado);
        this.solicitudesReembolso.put(id, s);
        return s;
    }

    public boolean aprobarVenue(String nombre) {
        if (nombre == null) return false;
        String key = nombre.toUpperCase();
        Venue v = this.venues.get(key);
        if (v == null) {
            v = this.venuesPendientes.get(key);
        }
        if (v != null) {
            v.setAprobado(true);
            this.venues.put(key, v);
            refrescarVenuesPendientes();
            return true;
        }
        return false;
    }

    public boolean rechazarVenue(String nombre) {
        if (nombre == null) return false;
        String key = nombre.toUpperCase();
        Venue actual = this.venues.get(key);
        if (actual != null && !actual.isAprobado()) {
            this.venues.remove(key);
            refrescarVenuesPendientes();
            return true;
        }
        if (this.venuesPendientes.remove(key) != null) {
            return true;
        }
        return false;
    }

    public void registrarEventoCancelado(Evento evento) {
        marcarEventoCancelado(evento);
    }

    public void registrarSolicitudProcesada(SolicitudReembolso solicitud) {
        if (solicitud != null) {
            this.solicitudesReembolso.remove(solicitud.getId());
            this.solicitudesReembolsoProcesadas.put(solicitud.getId(), solicitud);
        }
    }
    
    public List<SolicitudReembolso> listarSolicitudesPendientes() {
        List<SolicitudReembolso> lista = new ArrayList<>(solicitudesReembolso.values());
        lista.sort(Comparator.comparingInt(SolicitudReembolso::getId));
        return lista;
    }

    public List<SolicitudReembolso> listarSolicitudesProcesadas() {
        List<SolicitudReembolso> lista = new ArrayList<>(solicitudesReembolsoProcesadas.values());
        lista.sort(Comparator.comparingInt(SolicitudReembolso::getId));
        return lista;
    }
	
	public void eliminarEvento(UsuarioGenerico admin, String nombreEvento){
		
		
		if (admin instanceof Administrador) {
			
			if (nombreEvento != null) {
                this.eventos.remove(nombreEvento.toUpperCase());
            }
			
		}else {
			System.out.println("No eres admin");
		}
	}
	
	public void viewEventos() {
		
		System.out.println("Eventos disponibles: ");
		
		int i = 1;
	
		
		for (Map.Entry<String, Evento> pareja : this.eventos.entrySet()) {
			
			String i_ = Integer.toString(i);
			
			String nombre = pareja.getKey();
			
			System.out.println(i_ + ". "+nombre);
			
			i++;
			
		}	
		
	}
	
	public Evento selectorEvento(String nombreEvento) {
        if (nombreEvento == null) return null;
		return this.eventos.get(nombreEvento.toUpperCase());
	}
	
	public void viewLocalidades(Evento evento) {
		
		Iterator<Localidad> it = evento.getLocalidades().values().iterator();
		
		int i = 1;
		
		while (it.hasNext()) {
			Localidad localidad = it.next();
			
			String i_ = Integer.toString(i);
			
			System.out.println(i_ + ". "+localidad.getNombreLocalidad());
			System.out.println("    Precio Tiquetes: "+localidad.getPrecioTiquetes());
			System.out.println("    Numerada? : "+localidad.isEsNumerada());
			
			i++;
		}
		
		
	}
	
	public boolean cargarUsuarios() {
		// TODO
		return false;
	}
	
	public boolean cargarEventos() {
		// TODO
		return false;
	}
	
	public boolean cargarVenues() {
		// TODO
		return false;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("costoPorEmision", this.costoPorEmision);
        json.put("secuenciaTiquetes", this.secuenciaTiquetes);
        json.put("secuenciaSolicitudes", this.secuenciaSolicitudes);
        json.put("secuenciaOfertas", this.secuenciaOfertas);
        json.put("secuenciaContraofertas", this.secuenciaContraofertas);
        json.put("secuenciaTransacciones", this.secuenciaTransacciones);
        json.put("secuenciaLogMarketplace", this.secuenciaLogMarketplace);

		JSONArray u = new JSONArray();
		for (UsuarioGenerico usuario : this.usuarios.values()) {
			u.put(usuario.toJSON());
		}
        json.put("usuarios", u);

		JSONArray ev = new JSONArray();
		for (Evento evento : this.eventos.values()) {
			ev.put(evento.toJSON());
		}
        json.put("eventos", ev);

        JSONArray vv = new JSONArray();
		for (Venue venue : this.venues.values()) {
			vv.put(venue.toJSON());
		}
        json.put("venues", vv);

        JSONArray vvPend = new JSONArray();
        for (Venue venuePendiente : this.venuesPendientes.values()) {
            vvPend.put(venuePendiente.toJSON());
        }
        json.put("venuesPendientes", vvPend);

        JSONArray solicitudes = new JSONArray();
        for (SolicitudReembolso s : this.solicitudesReembolso.values()) {
            solicitudes.put(s.toJSON());
        }
        json.put("solicitudesReembolso", solicitudes);
        JSONArray solicitudesProc = new JSONArray();
        for (SolicitudReembolso s : this.solicitudesReembolsoProcesadas.values()) {
            solicitudesProc.put(s.toJSON());
        }
        json.put("solicitudesReembolsoProcesadas", solicitudesProc);

        JSONArray ofertas = new JSONArray();
        for (OfertaReventa oferta : this.ofertasReventa.values()) {
            ofertas.put(oferta.toJSON());
        }
        json.put("ofertasReventa", ofertas);

        JSONArray contra = new JSONArray();
        for (Contraoferta c : this.contraofertas.values()) {
            contra.put(c.toJSON());
        }
        json.put("contraofertas", contra);

        JSONArray trans = new JSONArray();
        for (TransaccionReventa t : this.transaccionesReventa.values()) {
            trans.put(t.toJSON());
        }
        json.put("transaccionesReventa", trans);

        JSONArray log = new JSONArray();
        for (Integer id : this.auditoriaOrden) {
            AuditoriaMarketplaceEntry entry = this.auditoriaMarketplace.get(id);
            if (entry != null) {
                log.put(entry.toJSON());
            }
        }
        json.put("logMarketplace", log);

        JSONArray orgPend = new JSONArray();
        for (String loginPendiente : this.organizadoresPendientes) {
            orgPend.put(loginPendiente);
        }
        json.put("organizadoresPendientes", orgPend);

		return json;
	}
    
    public void proponerVenue(Venue venue) {
        if (venue == null || venue.getNombre() == null) return;
        venue.setAprobado(false);
        String key = venue.getNombre().toUpperCase();
        venue.setNombre(key);
        this.venues.put(key, venue);
        refrescarVenuesPendientes();
    }

    public void marcarEventoCancelado(Evento evento) {
        if (evento == null || evento.getNombre() == null) return;
        evento.setCancelado(true);
        this.eventos.put(evento.getNombre(), evento);
    }

    public void inicializarDemo() {
        this.usuarios.clear();
        this.eventos.clear();
        this.venues.clear();
        this.venuesPendientes.clear();
        this.solicitudesReembolso.clear();
        this.solicitudesReembolsoProcesadas.clear();
        this.indiceTiquetes.clear();
        this.secuenciaTiquetes = 1000;
        this.secuenciaSolicitudes = 1;
        this.secuenciaOfertas = 1;
        this.secuenciaContraofertas = 1;
        this.secuenciaTransacciones = 1;
        this.secuenciaLogMarketplace = 1;
        this.costoPorEmision = 5000;
        this.ofertasReventa.clear();
        this.contraofertas.clear();
        this.transaccionesReventa.clear();
        this.auditoriaMarketplace.clear();
        this.auditoriaOrden.clear();
        this.indiceOfertaPorTiquete.clear();
        this.organizadoresPendientes.clear();

        Administrador admin = new Administrador("admin1", "1234");
        this.usuarios.put(admin.getLogin(), admin);

        Organizador organizadorDemo = new Organizador("organizador1", "org123");
        organizadorDemo.setSaldoVirtual(0);
        organizadorDemo.setAprobado(true);
        this.usuarios.put(organizadorDemo.getLogin(), organizadorDemo);

        Organizador organizadorPendiente = new Organizador("organizador2", "org456");
        organizadorPendiente.setSaldoVirtual(0);
        organizadorPendiente.setAprobado(false);
        this.usuarios.put(organizadorPendiente.getLogin(), organizadorPendiente);
        registrarOrganizadorPendiente(organizadorPendiente);

        Natural compradorDemo = new Natural("cliente1", "cliente123");
        compradorDemo.setSaldoVirtual(300000);
        this.usuarios.put(compradorDemo.getLogin(), compradorDemo);

        Natural compradorDos = new Natural("cliente2", "cliente234");
        compradorDos.setSaldoVirtual(150000);
        this.usuarios.put(compradorDos.getLogin(), compradorDos);

        Venue venueDemo = new Venue();
        venueDemo.setNombre("ARENA_DEMO");
        venueDemo.setCapacidad(500);
        venueDemo.setUbicacion("Ciudad Demo");
        venueDemo.setOrganizador(organizadorDemo);
        venueDemo.setAprobado(true);
        this.venues.put(venueDemo.getNombre(), venueDemo);

        String fechaDemo = LocalDate.now().plusMonths(6).toString();
        Evento eventoDemo = new Evento("CONCIERTO_DEMO", "CONCIERTO", "GENERAL", 0, venueDemo, fechaDemo);
        eventoDemo.setOrganizador(organizadorDemo);
        eventoDemo.setCargoPorcentualServicio(0.1);
        venueDemo.addEvento(eventoDemo);
        organizadorDemo.addEvento(eventoDemo);

        Localidad localidadGeneral = new Localidad("GENERAL", 120000, false, eventoDemo);
        eventoDemo.addLocalidad(localidadGeneral);

        for (int i = 0; i < 10; i++) {
            TiqueteGeneral tiquete = new TiqueteGeneral(localidadGeneral.getPrecioTiquetes(), this.costoPorEmision,
                    eventoDemo.getFecha(), "20:00", 4, "GENERAL");
            tiquete.setEvento(eventoDemo);
            tiquete.setLocalidad(localidadGeneral.getNombreLocalidad());
            tiquete.setId(siguienteIdTiquete());
            localidadGeneral.addTiquete(tiquete);
            eventoDemo.addTiquete(tiquete);
            registrarTiquete(tiquete);
        }
        // Asigna dos tiquetes al cliente demo para habilitar el marketplace
        Iterator<Tiquete> iterator = new ArrayList<>(localidadGeneral.getTiquetes().values()).iterator();
        int asignados = 0;
        while (iterator.hasNext() && asignados < 2) {
            Tiquete tiquete = iterator.next();
            localidadGeneral.marcarVendido(tiquete);
            eventoDemo.marcarVendido(tiquete);
            tiquete.setCliente(compradorDemo);
            compradorDemo.agregarTiquete(tiquete);
            asignados++;
        }
        eventoDemo.setCantidadTiquetesDisponibles(localidadGeneral.getTiquetes().size());
        this.eventos.put(eventoDemo.getNombre(), eventoDemo);
        refrescarVenuesPendientes();

        // Oferta demo en el marketplace
        List<Integer> tiquetesDemo = new ArrayList<>();
        for (Tiquete t : compradorDemo.getMisTiquetes()) {
            tiquetesDemo.add(t.getId());
            break;
        }
        if (!tiquetesDemo.isEmpty()) {
            crearOfertaReventa(compradorDemo.getLogin(), tiquetesDemo, 140000);
        }
    }
	
}
