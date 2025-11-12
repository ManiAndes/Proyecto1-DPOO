package dpoo.proyecto.tiquetes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TiqueteMultipleEntrada extends EntradaMultiple {
	
	private String localidadAsociada;
	private String eventoPrincipal;
	private int entradasIncluidas;

	public TiqueteMultipleEntrada(double precioOriginal, double cuotaAdicionalEmision, String fecha,
			String hora, int maximoTiquetesPorTransaccion, String tipo,
			String eventoPrincipal, String localidadAsociada, int entradasIncluidas) {
		super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
		this.eventoPrincipal = eventoPrincipal;
		this.localidadAsociada = localidadAsociada;
		this.entradasIncluidas = entradasIncluidas;
		for (int i = 1; i <= entradasIncluidas; i++) {
			registrarComponente(localidadAsociada + " - ENTRADA " + i);
		}
	}

	public String getLocalidadAsociada() {
		return localidadAsociada;
	}

	public void setLocalidadAsociada(String localidadAsociada) {
		this.localidadAsociada = localidadAsociada;
	}

	public String getEventoPrincipal() {
		return eventoPrincipal;
	}

	public void setEventoPrincipal(String eventoPrincipal) {
		this.eventoPrincipal = eventoPrincipal;
	}

	public int getEntradasIncluidas() {
		return entradasIncluidas;
	}

	public void setEntradasIncluidas(int entradasIncluidas) {
		this.entradasIncluidas = entradasIncluidas;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("localidadAsociada", localidadAsociada);
		json.put("eventoPrincipal", eventoPrincipal);
		json.put("entradasIncluidas", entradasIncluidas);
		return json;
	}

	public static TiqueteMultipleEntrada fromJSON(JSONObject json) {
		double precio = json.optDouble("precioOriginal", 0.0);
		double emision = json.optDouble("cuotaAdicionalEmision", 0.0);
		String fecha = json.optString("fecha", "");
		String hora = json.optString("hora", "");
		int max = json.optInt("maximoTiquetesPorTransaccion", 0);
		String tipo = json.optString("tipo", "");
		String evento = json.optString("eventoPrincipal", "");
		String localidad = json.optString("localidadAsociada", "");
		int entradas = json.optInt("entradasIncluidas", 0);
		TiqueteMultipleEntrada t = new TiqueteMultipleEntrada(precio, emision, fecha, hora, max, tipo, evento, localidad, entradas);
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
