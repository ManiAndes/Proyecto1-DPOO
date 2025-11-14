package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Usuario extends UsuarioGenerico {
	
	private List<Tiquete> misTiquetes = new ArrayList<Tiquete>();
	private List<Integer> tiquetesEnReventa = new ArrayList<Integer>();
	
	public Usuario(String login, String password) {
		super(login, password);
	}
	
	public Usuario(String login, String password, List<Tiquete> misTiquetes) {
		super(login, password);
		this.misTiquetes = misTiquetes;
	}

	public List<Tiquete> getMisTiquetes() {
		return misTiquetes;
	}
	
	public void setMisTiquetes(List<Tiquete> misTiquetes) {
		this.misTiquetes = misTiquetes;
	}

	public void agregarTiquete(Tiquete tiquete) {
		if (tiquete != null) {
			this.misTiquetes.add(tiquete);
		}
	}
	
	public void removerTiquete(Tiquete tiquete) {
		if (tiquete != null) {
			this.misTiquetes.remove(tiquete);
		}
	}

	public List<Integer> getTiquetesEnReventa() {
		return tiquetesEnReventa;
	}

	public void setTiquetesEnReventa(List<Integer> tiquetesEnReventa) {
		this.tiquetesEnReventa = tiquetesEnReventa != null ? tiquetesEnReventa : new ArrayList<>();
	}

	public void registrarTiqueteEnReventa(int tiqueteId) {
		if (!this.tiquetesEnReventa.contains(tiqueteId)) {
			this.tiquetesEnReventa.add(tiqueteId);
		}
	}

	public void removerTiqueteEnReventa(int tiqueteId) {
		this.tiquetesEnReventa.remove(Integer.valueOf(tiqueteId));
	}

	public boolean estaTiqueteEnReventa(int tiqueteId) {
		return this.tiquetesEnReventa.contains(tiqueteId);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject base = super.toJSON();
		JSONArray arr = new JSONArray();
		// Nota: serialización básica de tiquetes; las subclases de Tiquete deben implementar toJSON
		for (Tiquete t : misTiquetes) {
			if (t != null) {
				arr.put(t.toJSON());
			}
		}
		base.put("misTiquetes", arr);
		JSONArray enVenta = new JSONArray();
		for (Integer id : tiquetesEnReventa) {
			enVenta.put(id);
		}
		base.put("tiquetesEnReventa", enVenta);
		return base;
	}
	
}
