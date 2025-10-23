package dpoo.proyecto.usuarios;

import dpoo.proyecto.tiquetes.Tiquete;
import org.json.JSONObject;

public class Natural extends Usuario {

	public Natural(String login, String password) {
		super(login, password);
	}

	public static Natural fromJSON(JSONObject json) {
		String login = json.getString("login");
		String password = json.getString("password");
		double saldo = json.optDouble("saldoVirtual", 0.0);
		Natural n = new Natural(login, password);
		n.setSaldoVirtual(saldo);
		return n;
	}

}
