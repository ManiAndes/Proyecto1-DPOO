package dpoo.proyecto.usuarios;

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
	

	public double getCostoPorcentualEmision() {
		return CostoPorcentualServicio;
	}


	public void setCostoPorcentualEmision(double costoPorcentualServicio) {
		CostoPorcentualServicio = costoPorcentualServicio;
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
    
    public void cancelarEvento(Evento evento, int tipoReembolso, MasterTicket sistemaBoleteria) {
		if (evento == null) return;
		evento.cancelar();
		Map<Integer, Tiquete> tiquetes = evento.getTiquetesVendidos();
		double cuotaPorcentual = evento.getCargoPorcentualServicio();
		double cuotaEmision = evento.getCuotaAdicionalEmision();

		for (Map.Entry<Integer, Tiquete> entry : tiquetes.entrySet()) {
			Tiquete tiquete = entry.getValue();
			if (tiquete == null || tiquete.isReembolsado()) {
				continue;
			}
			Usuario cliente = (Usuario) tiquete.getCliente();
			double pagado = tiquete.getMontoPagado() > 0
					? tiquete.getMontoPagado()
					: tiquete.calcularPrecioFinal(cuotaPorcentual, cuotaEmision);
			double reembolso;
			
			if (tipoReembolso == 1) {
				reembolso = Math.max(0, pagado - tiquete.getCuotaAdicionalEmision());

			} else {
				reembolso = tiquete.getPrecioOriginal();
			}

			if (cliente != null) {
				cliente.addSaldoVirtual(reembolso);
			}
			tiquete.setReembolsado(true);
			tiquete.setEstado("REEMBOLSADO");
		}

		sistemaBoleteria.marcarEventoCancelado(evento);

	}

    // === Aprobación de Venues ===
    public boolean aprobarVenue(MasterTicket sistema, String nombreVenue) {
        if (sistema == null) return false;
        return sistema.aprobarVenue(nombreVenue);
    }

    public boolean rechazarVenue(MasterTicket sistema, String nombreVenue) {
        if (sistema == null) return false;
        return sistema.rechazarVenue(nombreVenue);
    }

    // === Procesamiento de solicitudes de reembolso ===
    public boolean aprobarReembolso(MasterTicket sistema, int idSolicitud, int tipoReembolso) {
        SolicitudReembolso s = sistema.getSolicitudesReembolso().get(idSolicitud);
        if (s == null || !"PENDIENTE".equals(s.getEstado())) return false;

        Tiquete t = s.getTiquete();
        if (t == null) return false;
        Evento e = t.getEvento();
        double cuotaPorcentual = e != null ? e.getCargoPorcentualServicio() : 0.0;
        double cuotaEmision = t.getCuotaAdicionalEmision();
        double pagado = t.getMontoPagado() > 0
                ? t.getMontoPagado()
                : t.calcularPrecioFinal(cuotaPorcentual, cuotaEmision);

        double reembolso = t.getPrecioOriginal();
        if (tipoReembolso == 1) {
            reembolso = Math.max(0, pagado - cuotaEmision);
        } else if (tipoReembolso == 3) {
            reembolso = pagado;
        }

        Usuario solicitante = s.getSolicitante();
        if (solicitante != null) {
            reembolsar(solicitante, reembolso);
        }
        s.setEstado("APROBADA");
        s.setMontoAprobado(reembolso);
        s.setObservacionAdmin("Aprobada por " + getLogin());
        t.setReembolsado(true);
        t.setEstado("REEMBOLSADO");
        if (sistema != null) {
            sistema.registrarSolicitudProcesada(s);
        }
        return true;
    }

    public boolean rechazarReembolso(MasterTicket sistema, int idSolicitud) {
        SolicitudReembolso s = sistema.getSolicitudesReembolso().get(idSolicitud);
        if (s == null || !"PENDIENTE".equals(s.getEstado())) return false;
        s.setEstado("RECHAZADA");
        s.setObservacionAdmin("Rechazada por " + getLogin());
        if (sistema != null) {
            sistema.registrarSolicitudProcesada(s);
        }
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
