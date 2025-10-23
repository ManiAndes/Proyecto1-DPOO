package persistencia;



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
	
	

}
