package dpoo.proyecto.usuarios;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
		JSONArray enVenta = json.optJSONArray("tiquetesEnReventa");
		if (enVenta != null) {
			List<Integer> ids = new ArrayList<>();
			for (int i = 0; i < enVenta.length(); i++) {
				ids.add(enVenta.optInt(i));
			}
			n.setTiquetesEnReventa(ids);
		}
		return n;
	}

}
