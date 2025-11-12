package dpoo.proyecto.tiquetes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaqueteDeluxe extends EntradaMultiple {
	
	private final List<String> beneficios = new ArrayList<>();
	private final List<String> cortesias = new ArrayList<>();
	private String descripcionPrincipal;

	public PaqueteDeluxe(double precioOriginal, double cuotaAdicionalEmision, String fecha,
			String hora, int maximoTiquetesPorTransaccion, String tipo, String descripcionPrincipal,
			List<String> beneficios, List<String> cortesias) {
		super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
		this.descripcionPrincipal = descripcionPrincipal;
		if (beneficios != null) {
			for (String beneficio : beneficios) {
				agregarBeneficio(beneficio);
			}
		}
		if (cortesias != null) {
			for (String cortesia : cortesias) {
				agregarCortesia(cortesia);
			}
		}
		setTransferible(false);
		setTransferenciaPorComponente(false);
	}

	public String getDescripcionPrincipal() {
		return descripcionPrincipal;
	}

	public void setDescripcionPrincipal(String descripcionPrincipal) {
		this.descripcionPrincipal = descripcionPrincipal;
	}

	public List<String> getBeneficios() {
		return beneficios;
	}

	public List<String> getCortesias() {
		return cortesias;
	}

	public void agregarBeneficio(String beneficio) {
		if (beneficio != null && !beneficio.isEmpty()) {
			this.beneficios.add(beneficio);
			registrarComponente("Beneficio: " + beneficio);
		}
	}

	public void agregarCortesia(String cortesia) {
		if (cortesia != null && !cortesia.isEmpty()) {
			this.cortesias.add(cortesia);
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		json.put("descripcionPrincipal", descripcionPrincipal);
		JSONArray ben = new JSONArray();
		for (String beneficio : beneficios) {
			ben.put(beneficio);
		}
		json.put("beneficios", ben);
		JSONArray cor = new JSONArray();
		for (String cortesia : cortesias) {
			cor.put(cortesia);
		}
		json.put("cortesias", cor);
		return json;
	}

	public static PaqueteDeluxe fromJSON(JSONObject json) {
		double precio = json.optDouble("precioOriginal", 0.0);
		double emision = json.optDouble("cuotaAdicionalEmision", 0.0);
		String fecha = json.optString("fecha", "");
		String hora = json.optString("hora", "");
		int max = json.optInt("maximoTiquetesPorTransaccion", 0);
		String tipo = json.optString("tipo", "");
		String descripcion = json.optString("descripcionPrincipal", "");
		List<String> beneficios = new ArrayList<>();
		JSONArray ben = json.optJSONArray("beneficios");
		if (ben != null) {
			for (int i = 0; i < ben.length(); i++) {
				beneficios.add(ben.optString(i));
			}
		}
		List<String> cortesias = new ArrayList<>();
		JSONArray cor = json.optJSONArray("cortesias");
		if (cor != null) {
			for (int i = 0; i < cor.length(); i++) {
				cortesias.add(cor.optString(i));
			}
		}
		PaqueteDeluxe p = new PaqueteDeluxe(precio, emision, fecha, hora, max, tipo, descripcion, beneficios, cortesias);
		JSONArray comp = json.optJSONArray("componentes");
		if (comp != null) {
			List<String> componentes = new ArrayList<>();
			for (int i = 0; i < comp.length(); i++) {
				componentes.add(comp.optString(i));
			}
			p.setComponentes(componentes);
		}
		JSONArray usados = json.optJSONArray("componentesUsados");
		if (usados != null) {
			List<Boolean> componentesUsados = new ArrayList<>();
			for (int i = 0; i < usados.length(); i++) {
				componentesUsados.add(usados.optBoolean(i, false));
			}
			p.setComponentesUsados(componentesUsados);
		}
		p.setTransferible(json.optBoolean("transferible", false));
		p.setTransferenciaPorComponente(json.optBoolean("transferenciaPorComponente", false));
		return p;
	}
}
