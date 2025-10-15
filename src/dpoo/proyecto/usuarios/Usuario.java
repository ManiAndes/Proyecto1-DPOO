package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;

public abstract class Usuario<T extends Tiquete> {
	
	private String login;
	private String password;
	private double saldoVirtual;
	private List<T> misTiquetes = new ArrayList<T>();
	
	public Usuario(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public Usuario(String login, String password, double saldoVirtual, List<T> misTiquetes) {
		this.login = login;
		this.password = password;
		this.saldoVirtual = saldoVirtual;
		this.misTiquetes = misTiquetes;
	}

	public String getLogin() {
		return login;
	}
	public String getPassword() {
		return password;
	}
	public double getSaldoVirtual() {
		return saldoVirtual;
	}
	public List<T> getMisTiquetes() {
		return misTiquetes;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setSaldoVirtual(double saldoVirtual) {
		this.saldoVirtual = saldoVirtual;
	}
	public void setMisTiquetes(List<T> misTiquetes) {
		this.misTiquetes = misTiquetes;
	}
	
	
	
}
