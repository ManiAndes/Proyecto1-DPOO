package persistencia;

import dpoo.proyecto.app.MasterTicket;

public class CentralPersistencia {
	
	public static final String JSON = "JSON";
	
	public static IPersistenciaMasterticket getPersistenciaMasterticket( String tipoArchivo )
    {
		try {
			
			if( JSON.equals( tipoArchivo ) )
	            return new PersistenciaMasterticket( );
	        
			
		}catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    	
    }

	public void loadDefault(MasterTicket sistemaBoleteria) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'loadDefault'");
	}

    public void saveDefault(MasterTicket sistemaBoleteria) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveDefault'");
    }
	
	

}
