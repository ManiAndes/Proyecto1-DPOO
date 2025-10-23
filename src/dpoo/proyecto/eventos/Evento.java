package dpoo.proyecto.eventos;

import java.util.*;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;
import org.json.JSONArray;
import org.json.JSONObject;

public class Evento {
	
	private Map<Integer, Tiquete> tiquetes = new HashMap<Integer, Tiquete>();
	private Map<Integer, Tiquete> tiquetesVendidos = new HashMap<Integer, Tiquete>();
	private Map<String, Localidad> localidades = new HashMap<String, Localidad>();
	private Organizador organizador;
	
	private double cargoPorcentualServicio;
	private String nombre;
	private String tipoEvento;
	private String tipoTiquetes;
	private int cantidadTiquetesDisponibles;
	private Venue venue;
	private String fecha;
	private boolean cancelado;
    private double ganancias = 0;

	public Evento(String nombre, String tipoEvento, String tipoTiquetes, int cantidadTiquetesDisponibles, Venue venue, String fecha) {
		super();
		this.nombre = nombre;
		this.tipoEvento = tipoEvento;
		this.tipoTiquetes = tipoTiquetes;
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
		this.venue = venue;
		this.fecha = fecha;
		this.cancelado = false;
	}

	// ??? pq uno sin nombre
	public Evento(Organizador organizador, String tipoEvento, String tipoTiquetes, int cantidadTiquetesDisponibles,
			Venue venue, String fecha) {
		this.organizador = organizador;
		this.tipoEvento = tipoEvento;
		this.tipoTiquetes = tipoTiquetes;
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
		this.venue = venue;
		this.fecha = fecha;
		this.cancelado = false;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
    public Map<Integer, Tiquete> getTiquetes() {
        return tiquetes;
    }

    public Map<String, Localidad> getLocalidades() {
        return this.localidades;
    }

    public void addLocalidad(Localidad localidad) {
        if (this.localidades.get(localidad.getNombreLocalidad()) == null) {
            this.localidades.put(localidad.getNombreLocalidad(), localidad);
        }
    }

	public Organizador getOrganizador() {
		return organizador;
	}

	public String getTipoEvento() {
		return tipoEvento;
	}

	public String getTipoTiquetes() {
		return tipoTiquetes;
	}

	public int getCantidadTiquetesDisponibles() {
		return cantidadTiquetesDisponibles;
	}

	public String getFecha() {
		return fecha;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setTiquetes(Map<Integer, Tiquete> tiquetes) {
		this.tiquetes = tiquetes;
	}

	public void setLocalidades(Map<String, Localidad> localidades) {
		this.localidades = localidades;
	}

	public void setOrganizador(Organizador organizador) {
		this.organizador = organizador;
	}

	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public void setTipoTiquetes(String tipoTiquetes) {
		this.tipoTiquetes = tipoTiquetes;
	}

	public void setCantidadTiquetesDisponibles(int cantidadTiquetesDisponibles) {
		this.cantidadTiquetesDisponibles = cantidadTiquetesDisponibles;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public Venue getVenue() {
		return venue;
	}
	
	public void setVenue(Venue venue) {
		this.venue = venue;
		this.venue.addEvento(this);
		this.venue.setOrganizador(organizador);
	}
	public Map<Integer, Tiquete> getTiquetesVendidos() {
		return tiquetesVendidos;
	}

	public void setTiquetesVendidos(Map<Integer, Tiquete> tiquetesVendidos) {
		this.tiquetesVendidos = tiquetesVendidos;
	}
	
	public double getCargoPorcentualServicio() {
		return cargoPorcentualServicio;
	}

	public void setCargoPorcentualServicio(double cargoPorcentualServicio) {
		this.cargoPorcentualServicio = cargoPorcentualServicio;
	}

    public double getCuotaAdicionalEmision() {
        if (this.tiquetes == null || this.tiquetes.isEmpty()) {
            return 0.0;
        }
        return this.tiquetes.get(0).getCuotaAdicionalEmision();
    }

    public double getGanancias() {
        return ganancias;
    }

	public void addTiquete (Tiquete tiquete) {
		if (tiquete != null) {
            tiquetes.put(tiquete.getId(), tiquete);
        }
	}

    public void setGanancias(double ganancias) {
        this.ganancias = ganancias;
    }

    // Calcula y actualiza las ganancias del evento como
    // la suma de (precioPagado - precioOriginal) sobre los tiquetes vendidos.
    // precioPagado = calcularPrecioFinal(cargoPorcentual, cuotaEmision)
    public double calcularGanancias() {
        double total = 0.0;
        double cuotaPorcentual = getCargoPorcentualServicio();
        double cuotaEmision = getCuotaAdicionalEmision();
        if (this.tiquetesVendidos != null) {
            
			for (Map.Entry<Integer, Tiquete> entry: this.tiquetesVendidos.entrySet()) {
                double pagado = entry.getValue().calcularPrecioFinal(cuotaPorcentual, cuotaEmision);
                total += (pagado - entry.getValue().getPrecioOriginal());
            }
        }
        this.ganancias = total;
        return total;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("nombre", this.nombre);
        json.put("tipoEvento", this.tipoEvento);
        json.put("tipoTiquetes", this.tipoTiquetes);
        json.put("cantidadTiquetesDisponibles", this.cantidadTiquetesDisponibles);
        json.put("fecha", this.fecha);
        json.put("ganancias", this.ganancias);
        json.put("cancelado", this.cancelado);
        json.put("cargoPorcentualServicio", this.cargoPorcentualServicio);
        if (this.venue != null) json.put("venueName", this.venue.getNombre());
        if (this.organizador != null) json.put("organizadorLogin", this.organizador.getLogin());
        JSONArray locs = new JSONArray();
        for (Localidad l : this.localidades) {
            locs.put(l.toJSON());
        }
        json.put("localidades", locs);
        JSONArray tiqs = new JSONArray();
        for (Tiquete t : this.tiquetes) {
            tiqs.put(t.toJSON());
        }
        json.put("tiquetes", tiqs);
        JSONArray vendidos = new JSONArray();
        for (Tiquete t : this.tiquetesVendidos) {
            vendidos.put(t.toJSON());
        }
        json.put("tiquetesVendidos", vendidos);
        return json;
    }

    public static Evento fromJSON(JSONObject json) {
        String nombre = json.getString("nombre");
        String tipoEvento = json.optString("tipoEvento", "");
        String tipoTiquetes = json.optString("tipoTiquetes", "");
        int cant = json.optInt("cantidadTiquetesDisponibles", 0);
        Venue v = new Venue();
        String fecha = json.optString("fecha", "");
        Evento e = new Evento(nombre, tipoEvento, tipoTiquetes, cant, v, fecha);
        e.setGanancias(json.optDouble("ganancias", 0.0));
        e.setCargoPorcentualServicio(json.optDouble("cargoPorcentualServicio", 0.0));
        if (json.optBoolean("cancelado", false)) {
            e.cancelar();
        }
        return e;
    }
	
    public String cancelar() {
        this.cancelado = true;
        return this.nombre;
    }
	
	public String habilitar() {
		this.cancelado = false;
		return this.nombre;
	}
	
    // Nota: manejo de marcar vendido se definirá junto con inventario de tiquetes
	public void marcarVendido(Tiquete tiquete) {
		
		this.tiquetes.remove(tiquete.getId());
		this.tiquetesVendidos.put(tiquete.getId(), tiquete);
		
	}

}
