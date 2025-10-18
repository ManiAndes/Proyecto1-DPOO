package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;

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
		

	
	public void cancelarEvento(Evento evento, int tipoReembolso) {
		String nombre = evento.cancelar();
		
		
		List<Tiquete> tiquetes = evento.getTiquetesVendidos();
		
		double cuotaPorcentual = evento.getCargoPorcentualServicio();
		double cuotaEmision = evento.getCuotaAdicionalEmision();
		

		for (Tiquete tiquete: tiquetes) {
			Usuario cliente = tiquete.getCliente();
			
			double precio = tiquete.calcularPrecioFinal(cuotaPorcentual, cuotaEmision);
			
			double reembolso = precio - tiquete.getPrecioOriginal();
			
			if (tipoReembolso == 1) {
				reembolso = precio - cuotaEmision;
			}
			
			reembolsar(cliente, reembolso);
			
			
		}
		
		
		
		
		
	}

	
	
	

}
