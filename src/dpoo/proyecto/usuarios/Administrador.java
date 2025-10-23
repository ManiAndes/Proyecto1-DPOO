package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import org.json.JSONObject;


public class Administrador extends UsuarioGenerico {

	private double CostoPorcentualServicio;


	

	public Administrador(String login, String password) {
		super(login, password);
	}
	

	public boolean aprobacionVenue() {
		// TODO
		return false;
	}
	
	public double getCostoPorcentualEmision() {
		return CostoPorcentualServicio;
	}


	public void setCostoPorcentualEmision(double costoPorcentualServicio) {
		CostoPorcentualServicio = costoPorcentualServicio;
	}


	public boolean aprobacionReembolso() {
		// TODO
		return false;
	}
	
	private void reembolsar(Usuario cliente, double precio) {
		
	
			
			double saldoOriginal = cliente.getSaldoVirtual();
			
			cliente.setSaldoVirtual(precio + saldoOriginal);
		
		
	}

	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("costoPorcentualServicio", this.CostoPorcentualServicio);
		return json;
	}

	public static Administrador fromJSON(JSONObject json) {
		String login = json.getString("login");
		String password = json.getString("password");
		double saldo = json.optDouble("saldoVirtual", 0.0);
		double cps = json.optDouble("costoPorcentualServicio", 0.0);
		Administrador a = new Administrador(login, password);
		a.setSaldoVirtual(saldo);
		a.setCostoPorcentualEmision(cps);
		return a;
	}
    
    
    
    public void cancelarEvento(Evento evento, int tipoReembolso) {
		String nombre = evento.cancelar();
		
		
		Map<Integer, Tiquete> tiquetes = evento.getTiquetesVendidos();
		
		double cuotaPorcentual = evento.getCargoPorcentualServicio();
		double cuotaEmision = evento.getCuotaAdicionalEmision();
		

		for (Map.Entry<Integer, Tiquete> entry : tiquetes.entrySet()) {
			Usuario cliente = entry.getValue().getCliente();
			
			double precio = entry.getValue().calcularPrecioFinal(cuotaPorcentual, cuotaEmision);
			
			double reembolso = precio - entry.getValue().getPrecioOriginal();
			
			if (tipoReembolso == 1) {
				reembolso = precio - cuotaEmision;
    }
			}
		}

    // === Aprobación de Venues ===
    public boolean aprobarVenue(MasterTicket sistema, String nombreVenue) {
        if (sistema.getVenuesPendientes().containsKey(nombreVenue)) {
            sistema.getVenues().put(nombreVenue, sistema.getVenuesPendientes().get(nombreVenue));
            sistema.getVenuesPendientes().remove(nombreVenue);
            return true;
        }
        return false;
    }

    public boolean rechazarVenue(MasterTicket sistema, String nombreVenue) {
        if (sistema.getVenuesPendientes().containsKey(nombreVenue)) {
            sistema.getVenuesPendientes().remove(nombreVenue);
            return true;
        }
        return false;
    }

    // === Procesamiento de solicitudes de reembolso ===
    public boolean aprobarReembolso(MasterTicket sistema, int idSolicitud, int tipoReembolso) {
        SolicitudReembolso s = sistema.getSolicitudesReembolso().get(idSolicitud);
        if (s == null || !"PENDIENTE".equals(s.getEstado())) return false;

        Tiquete t = s.getTiquete();
        Evento e = t.getEvento();
        double cuotaPorcentual = e.getCargoPorcentualServicio();
        double cuotaEmision = e.getCuotaAdicionalEmision();
        double pagado = t.calcularPrecioFinal(cuotaPorcentual, cuotaEmision);

        double reembolso = pagado - t.getPrecioOriginal();
        if (tipoReembolso == 1) { // Reembolsar total sin emisión
            reembolso = pagado - cuotaEmision;
        } else if (tipoReembolso == 2) { // Solo precio base
            reembolso = t.getPrecioOriginal();
        }

        reembolsar(s.getSolicitante(), reembolso);
        s.setEstado("APROBADA");
        return true;
    }

    public boolean rechazarReembolso(MasterTicket sistema, int idSolicitud) {
        SolicitudReembolso s = sistema.getSolicitudesReembolso().get(idSolicitud);
        if (s == null || !"PENDIENTE".equals(s.getEstado())) return false;
        s.setEstado("RECHAZADA");
        return true;
    }
			
		

	// === Finanzas de la plataforma (ganancias por sobrecargos) ===
	// Agrupa por fecha del evento
	public Map<String, Double> verFinanzasPorFecha(MasterTicket sistema) {
		Map<String, Double> porFecha = new HashMap<>();
		for (Evento e : sistema.getEventos().values()) {
			double g = e.calcularGanancias();
			String fecha = e.getFecha();
			if (fecha == null) fecha = "SIN_FECHA";
			porFecha.put(fecha, porFecha.getOrDefault(fecha, 0.0) + g);
		}

		return porFecha;
	}

	// Agrupa por evento (nombre del evento)
	public Map<String, Double> verFinanzasPorEvento(MasterTicket sistema) {
		Map<String, Double> porEvento = new HashMap<>();
		for (Evento e : sistema.getEventos().values()) {
			double g = e.calcularGanancias();
			String nombre = e.getNombre();
			if (nombre == null) nombre = "SIN_NOMBRE";
			porEvento.put(nombre, porEvento.getOrDefault(nombre, 0.0) + g);
		}
		return porEvento;
	}

	// Agrupa por organizador (login del organizador)
	public Map<String, Double> verFinanzasPorOrganizador(MasterTicket sistema) {
		Map<String, Double> porOrg = new HashMap<>();
		for (Evento e : sistema.getEventos().values()) {
			double g = e.calcularGanancias();
			String org = (e.getOrganizador() != null && e.getOrganizador().getLogin() != null)
					? e.getOrganizador().getLogin()
					: "SIN_ORGANIZADOR";
			porOrg.put(org, porOrg.getOrDefault(org, 0.0) + g);
		}
		return porOrg;
	}

	
	
	

}
