package persistencia;

import dpoo.proyecto.app.MasterTicket;

public interface IPersistenciaMasterticket {

	
	public abstract void salvarMasterTicket(String archivo, MasterTicket masterticket);
		
	public abstract MasterTicket cargarMasterTicket (String archivo);
	

}
