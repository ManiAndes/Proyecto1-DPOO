package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Usuario extends UsuarioGenerico {
	
	private List<Tiquete> misTiquetes = new ArrayList<Tiquete>();
	
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
		return base;
	}
	
}
