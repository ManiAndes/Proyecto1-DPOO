package dpoo.proyecto.tiquetes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TiqueteMultipleEvento extends EntradaMultiple {
	
	private final List<String> eventosIncluidos = new ArrayList<>();

	public TiqueteMultipleEvento(double precioOriginal, double cuotaAdicionalEmision, String fecha,
			String hora, int maximoTiquetesPorTransaccion, String tipo, List<String> eventos) {
		super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
		if (eventos != null) {
			for (String evento : eventos) {
				agregarEvento(evento);
			}
		}
	}

	public List<String> getEventosIncluidos() {
		return eventosIncluidos;
	}

	public void agregarEvento(String evento) {
		if (evento != null && !evento.isEmpty()) {
			this.eventosIncluidos.add(evento);
			registrarComponente("Acceso evento: " + evento);
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		JSONArray evs = new JSONArray();
		for (String evento : eventosIncluidos) {
			evs.put(evento);
		}
		json.put("eventosIncluidos", evs);
		return json;
	}

	public static TiqueteMultipleEvento fromJSON(JSONObject json) {
		double precio = json.optDouble("precioOriginal", 0.0);
		double emision = json.optDouble("cuotaAdicionalEmision", 0.0);
		String fecha = json.optString("fecha", "");
		String hora = json.optString("hora", "");
		int max = json.optInt("maximoTiquetesPorTransaccion", 0);
		String tipo = json.optString("tipo", "");
		JSONArray evs = json.optJSONArray("eventosIncluidos");
		List<String> eventos = new ArrayList<>();
		if (evs != null) {
			for (int i = 0; i < evs.length(); i++) {
				eventos.add(evs.optString(i));
			}
		}
		TiqueteMultipleEvento t = new TiqueteMultipleEvento(precio, emision, fecha, hora, max, tipo, eventos);
		JSONArray comp = json.optJSONArray("componentes");
		if (comp != null) {
			List<String> componentes = new ArrayList<>();
			for (int i = 0; i < comp.length(); i++) {
				componentes.add(comp.optString(i));
			}
			t.setComponentes(componentes);
		}
		JSONArray usados = json.optJSONArray("componentesUsados");
		if (usados != null) {
			List<Boolean> componentesUsados = new ArrayList<>();
			for (int i = 0; i < usados.length(); i++) {
				componentesUsados.add(usados.optBoolean(i, false));
			}
			t.setComponentesUsados(componentesUsados);
		}
		t.setTransferenciaPorComponente(json.optBoolean("transferenciaPorComponente", true));
		return t;
	}
}
