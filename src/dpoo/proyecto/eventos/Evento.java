package dpoo.proyecto.eventos;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Organizador;

public class Evento {
	
	private List<Tiquete> tiquetes = new ArrayList<Tiquete>();
	private List<Localidad> localidades = new ArrayList<Localidad>();
	private Organizador organizador;
	private List<Tiquete> tiquetesVendidos = new ArrayList<Tiquete>();
	
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
	
    public List<Tiquete> getTiquetes() {
        return tiquetes;
    }

    public List<Localidad> getLocalidades() {
        return this.localidades;
    }

    public void addLocalidad(Localidad localidad) {
        if (!this.localidades.contains(localidad)) {
            this.localidades.add(localidad);
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

	public void setTiquetes(List<Tiquete> tiquetes) {
		this.tiquetes = tiquetes;
	}

	public void setLocalidades(List<Localidad> localidades) {
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
	public List<Tiquete> getTiquetesVendidos() {
		return tiquetesVendidos;
	}

	public void setTiquetesVendidos(List<Tiquete> tiquetesVendidos) {
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
            for (Tiquete t : this.tiquetesVendidos) {
                double pagado = t.calcularPrecioFinal(cuotaPorcentual, cuotaEmision);
                total += (pagado - t.getPrecioOriginal());
            }
        }
        this.ganancias = total;
        return total;
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

}
