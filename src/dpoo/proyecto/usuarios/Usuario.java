package dpoo.proyecto.usuarios;

import java.util.List;
import java.util.ArrayList;

import dpoo.proyecto.tiquetes.Tiquete;

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
	
}
