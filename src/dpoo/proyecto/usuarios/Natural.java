package dpoo.proyecto.usuarios;

import dpoo.proyecto.tiquetes.Tiquete;

public class Natural<T extends Tiquete> extends Cliente<T> {

	public Natural(String login, String password) {
		super(login, password);
	}

}
