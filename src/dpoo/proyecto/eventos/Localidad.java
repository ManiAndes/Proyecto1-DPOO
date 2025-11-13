package dpoo.proyecto.eventos;

import dpoo.proyecto.tiquetes.Tiquete;

import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;

public class Localidad {
	
	private String nombreLocalidad;
	private double precioTiquetes;
	private boolean esNumerada;
    private Evento evento;
    private Map<Integer, Tiquete> tiquetes = new HashMap<Integer, Tiquete>();
    private Map<Integer, Tiquete> tiquetesVendidos = new HashMap<Integer, Tiquete>();
    private double descuento = 0.0; // porcentaje simple (0-100)
	
	public Localidad(String nombreLocalidad, double precioTiquetes, boolean esNumerada, Evento evento) {
		this.nombreLocalidad = nombreLocalidad;
		this.precioTiquetes = precioTiquetes;
		this.esNumerada = esNumerada;
		this.evento = evento;
	}

	public Map<Integer, Tiquete> getTiquetesVendidos() {
        return this.tiquetesVendidos;
    }

    public String getNombreLocalidad() {
		return nombreLocalidad;
	}

	public double getPrecioTiquetes() {
		return precioTiquetes;
	}

    public boolean isEsNumerada() {
        return esNumerada;
    }
    // Alias usado en otras clases
    public boolean getEsNumerada() {
        return esNumerada;
    }

	public Evento getEvento() {
		return evento;
	}

    public Map<Integer, Tiquete> getTiquetes() {
        return tiquetes;
    }
    // Alias para compatibilidad con ConsolaUsuario
    public Map<Integer, Tiquete> getTiquetesDisponibles() {
        return tiquetes;
    }

    public void addTiquete(Tiquete tiquete) {
        if (tiquete != null) {
            tiquetes.put(tiquete.getId(), tiquete);
        }
    }
    
    public void addTiqueteVendido(Tiquete tiquete) {
        if (tiquete != null) {
            tiquetesVendidos.put(tiquete.getId(), tiquete);
        }
    }

	public void setTiquetesVendidos(Map<Integer, Tiquete> tiquetesVendidos) {
        this.tiquetesVendidos = tiquetesVendidos;
    }
    
    public void setNombreLocalidad(String nombreLocalidad) {
		this.nombreLocalidad = nombreLocalidad;
	}

	public void setPrecioTiquetes(double precioTiquetes) {
		this.precioTiquetes = precioTiquetes;
	}

	public void setEsNumerada(boolean esNumerada) {
		this.esNumerada = esNumerada;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

    public void setTiquetes(Map<Integer, Tiquete> tiquetes) {
        this.tiquetes = tiquetes;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    	public void marcarVendido(Tiquete tiquete) {
		
		this.tiquetes.remove(tiquete.getId());
		this.tiquetesVendidos.put(tiquete.getId(), tiquete);
		
	}

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("nombreLocalidad", this.nombreLocalidad);
        json.put("precioTiquetes", this.precioTiquetes);
        json.put("esNumerada", this.esNumerada);
        json.put("descuento", this.descuento);
        json.put("tiquetesDisponibles", this.tiquetes.size());
        json.put("tiquetesVendidos", this.tiquetesVendidos.size());
        return json;
    }

    public static Localidad fromJSON(JSONObject json, Evento evento) {
        String nombre = json.getString("nombreLocalidad");
        double precio = json.optDouble("precioTiquetes", 0.0);
        boolean numerada = json.optBoolean("esNumerada", false);
        Localidad l = new Localidad(nombre, precio, numerada, evento);
        l.setDescuento(json.optDouble("descuento", 0.0));
        return l;
    }

}
