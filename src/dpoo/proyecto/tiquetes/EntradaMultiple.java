package dpoo.proyecto.tiquetes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class EntradaMultiple extends Tiquete {

	private final List<String> componentes = new ArrayList<>();
	private final List<Boolean> componentesUsados = new ArrayList<>();
	private boolean transferenciaPorComponente = true;

	public EntradaMultiple(double precioOriginal, double cuotaAdicionalEmision, String fecha,
			String hora, int maximoTiquetesPorTransaccion, String tipo) {
		super(precioOriginal, cuotaAdicionalEmision, fecha, hora, maximoTiquetesPorTransaccion, tipo);
	}

	protected void registrarComponente(String descripcion) {
		componentes.add(descripcion);
		componentesUsados.add(Boolean.FALSE);
	}

	public List<String> getComponentes() {
		return Collections.unmodifiableList(componentes);
	}

	public List<Boolean> getComponentesUsados() {
		return Collections.unmodifiableList(componentesUsados);
	}

	public void setComponentes(List<String> nuevosComponentes) {
		componentes.clear();
		componentesUsados.clear();
		if (nuevosComponentes != null) {
			for (String componente : nuevosComponentes) {
				registrarComponente(componente);
			}
		}
	}

	public void setComponentesUsados(List<Boolean> usados) {
		for (int i = 0; i < componentesUsados.size() && i < usados.size(); i++) {
			componentesUsados.set(i, usados.get(i));
		}
	}

	public void usarComponente(int indice) {
		if (indice >= 0 && indice < componentesUsados.size()) {
			componentesUsados.set(indice, Boolean.TRUE);
			if (!componentesUsados.contains(Boolean.FALSE)) {
				marcarUsado();
			}
		}
	}

	public boolean isTransferenciaPorComponente() {
		return transferenciaPorComponente && isTransferible();
	}

	public void setTransferenciaPorComponente(boolean transferenciaPorComponente) {
		this.transferenciaPorComponente = transferenciaPorComponente;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		JSONArray comp = new JSONArray();
		for (String componente : componentes) {
			comp.put(componente);
		}
		json.put("componentes", comp);
		JSONArray usados = new JSONArray();
		for (Boolean usado : componentesUsados) {
			usados.put(usado);
		}
		json.put("componentesUsados", usados);
		json.put("transferenciaPorComponente", transferenciaPorComponente);
		return json;
	}
}
