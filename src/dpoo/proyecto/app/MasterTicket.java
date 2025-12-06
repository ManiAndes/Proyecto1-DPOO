package dpoo.proyecto.app;

import dpoo.proyecto.usuarios.*;
import dpoo.proyecto.eventos.*;
import dpoo.proyecto.marketplace.MarketplaceReventa;
import dpoo.proyecto.tiquetes.*;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MasterTicket {
	
	private double costoPorEmision;
	
	// Mapa de los usuarios registrados
	private Map<String, UsuarioGenerico> usuarios;
	
	// Mapa de todos los eventos
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
    private MarketplaceReventa marketplaceReventa;
	

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
        this.marketplaceReventa = new MarketplaceReventa();
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

    public MarketplaceReventa getMarketplaceReventa() {
        return marketplaceReventa;
    }

    public void setMarketplaceReventa(MarketplaceReventa marketplaceReventa) {
        this.marketplaceReventa = marketplaceReventa != null ? marketplaceReventa : new MarketplaceReventa();
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

    public int siguienteIdTiquete() {
        return ++secuenciaTiquetes;
    }

    public int siguienteIdSolicitud() {
        return ++secuenciaSolicitudes;
    }

    private Administrador obtenerAdministradorPrincipal() {
        for (UsuarioGenerico usuario : this.usuarios.values()) {
            if (usuario instanceof Administrador) {
                return (Administrador) usuario;
            }
        }
        return null;
    }

    private boolean loginExiste(String login) {
        if (login == null) return false;
        for (String key : this.usuarios.keySet()) {
            if (key.equalsIgnoreCase(login)) {
                return true;
            }
        }
        return false;
    }

    public boolean esLoginDisponible(String login) {
        if (login == null) return false;
        return !loginExiste(login) && !esLoginPendienteOrganizador(login);
    }

    public boolean esLoginPendienteOrganizador(String login) {
        return obtenerLoginPendiente(login) != null;
    }

    private String obtenerLoginPendiente(String login) {
        Administrador admin = obtenerAdministradorPrincipal();
        if (admin == null) return null;
        return admin.buscarLoginSolicitud(login);
    }

    public boolean registrarSolicitudOrganizador(String login, String password) {
        if (login == null || password == null) return false;
        String limpio = login.trim();
        if (limpio.isEmpty()) return false;
        if (loginExiste(limpio) || esLoginPendienteOrganizador(limpio)) {
            return false;
        }
        Administrador admin = obtenerAdministradorPrincipal();
        if (admin == null) {
            return false;
        }
        admin.agregarSolicitudOrganizador(limpio, password);
        return true;
    }

    public boolean aprobarSolicitudOrganizador(String login) {
        Administrador admin = obtenerAdministradorPrincipal();
        if (admin == null) return false;
        String loginReal = obtenerLoginPendiente(login);
        if (loginReal == null) {
            return false;
        }
        String password = admin.obtenerPasswordSolicitud(loginReal);
        admin.removerSolicitudOrganizador(loginReal);
        if (password == null) {
            return false;
        }
        if (loginExiste(loginReal)) {
            return false;
        }
        Organizador nuevo = new Organizador(loginReal, password);
        this.usuarios.put(loginReal, nuevo);
        return true;
    }

    public boolean rechazarSolicitudOrganizador(String login) {
        Administrador admin = obtenerAdministradorPrincipal();
        if (admin == null) return false;
        String loginReal = obtenerLoginPendiente(login);
        if (loginReal == null) return false;
        return admin.removerSolicitudOrganizador(loginReal) != null;
    }

    public void registrarTiquete(Tiquete tiquete) {
        if (tiquete != null) {
            indiceTiquetes.put(tiquete.getId(), tiquete);
            if (tiquete.getId() > secuenciaTiquetes) {
                secuenciaTiquetes = tiquete.getId();
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
    
    public void proponerVenue(Venue venue) {
        if (venue == null || venue.getNombre() == null) return;
        venue.setAprobado(false);
        String key = venue.getNombre().toUpperCase();
        venue.setNombre(key);
        this.venues.put(key, venue);
        refrescarVenuesPendientes();
    }

    public void marcarEventoCancelado(Evento evento) {
        if (evento == null || evento.getNombre() == null) {
            return;
        }
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
        this.costoPorEmision = 5000;
        this.marketplaceReventa = new MarketplaceReventa();

        Administrador admin = new Administrador("admin1", "1234");
        this.usuarios.put(admin.getLogin(), admin);

        Organizador organizadorDemo = new Organizador("organizador1", "org123");
        organizadorDemo.setSaldoVirtual(0);
        this.usuarios.put(organizadorDemo.getLogin(), organizadorDemo);

        Natural compradorDemo = new Natural("cliente1", "cliente123");
        compradorDemo.setSaldoVirtual(300000);
        this.usuarios.put(compradorDemo.getLogin(), compradorDemo);

        Venue venueDemo = new Venue();
        venueDemo.setNombre("ARENA_DEMO");
        venueDemo.setCapacidad(500);
        venueDemo.setUbicacion("Ciudad Demo");
        venueDemo.setOrganizador(organizadorDemo);
        venueDemo.setAprobado(true);
        this.venues.put(venueDemo.getNombre(), venueDemo);

        Evento eventoDemo = new Evento("CONCIERTO_DEMO", "CONCIERTO", "GENERAL", 0, venueDemo, "2024-12-01");
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
        eventoDemo.setCantidadTiquetesDisponibles(localidadGeneral.getTiquetes().size());
        this.eventos.put(eventoDemo.getNombre(), eventoDemo);
        refrescarVenuesPendientes();
    }

    public JSONObject toJSON() {

		JSONObject json = new JSONObject();
		json.put("costoPorEmision", this.costoPorEmision);
        json.put("secuenciaTiquetes", this.secuenciaTiquetes);
        json.put("secuenciaSolicitudes", this.secuenciaSolicitudes);

		JSONArray u = new JSONArray();
		for (UsuarioGenerico usuario : this.usuarios.values()) {
            if (usuario instanceof Administrador) {
                u.put(((Administrador) usuario).toJSON());
            } else if (usuario instanceof Organizador) {
                u.put(((Organizador) usuario).toJSON());
            } else if (usuario instanceof Natural) {
                u.put(((Natural) usuario).toJSON());
            } else {
                u.put(usuario.toJSON());
            }
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
        if (this.marketplaceReventa != null) {
            json.put("marketplace", this.marketplaceReventa.toJSON());
        }

        return json;
    }
	
}
