package dpoo.proyecto.usuarios;

import org.json.JSONObject;

public abstract class UsuarioGenerico {
	
	private String login;
	private String password;
	private double saldoVirtual = 0;
	
	public UsuarioGenerico(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public UsuarioGenerico(String login, String password, double saldoVirtual) {
		this.login = login;
		this.password = password;
		this.saldoVirtual = saldoVirtual;
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
	
	public void setLogin(String login) {
		this.login = login;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setSaldoVirtual(double saldoVirtual) {
		this.saldoVirtual = saldoVirtual;
	}

	public void addSaldoVirtual(double saldo) {
		this.saldoVirtual += saldo;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("type", this.getClass().getSimpleName());
		json.put("login", this.login);
		json.put("password", this.password);
		json.put("saldoVirtual", this.saldoVirtual);
		return json;
	}

}
