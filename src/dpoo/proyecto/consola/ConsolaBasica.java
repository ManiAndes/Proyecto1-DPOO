package dpoo.proyecto.consola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dpoo.proyecto.eventos.Evento;

public class ConsolaBasica {
	
	protected String pedirCadena(String mensaje) {
		
		try
        {
            System.out.print( mensaje + ": " );
            BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
            String input = reader.readLine( );
            return input;
        }
        catch( IOException e )
        {
            System.out.println( "Error leyendo de la consola" );
        }
        return "error";
		
	}
	
	
	//ADMIN
	protected void viewEventoAdmin(Evento evento) {
		System.out.println("Información de "+evento.getNombre());
		System.out.println("====================");
		System.out.println("Fecha: "+evento.getFecha());
		System.out.println("Tipo: "+evento.getTipoEvento());
		System.out.println("Cantidad tiquetes: "+evento.getTiquetes().size());
		System.out.println("Tiquetes vendidos: "+evento.getTiquetesVendidos().size());
		System.out.println("Recaudos: "+evento.getGanancias());
	}
	
	
	//CLIENTE
	protected void viewEventoCliente(Evento evento) {
		System.out.println("Información de "+evento.getNombre());
		System.out.println("====================");
		System.out.println("Fecha: "+evento.getFecha());
		System.out.println("Tipo: "+evento.getTipoEvento());
		System.out.println("Cargo porcentual: "+evento.getCargoPorcentualServicio());
		System.out.println("Cargo por Emision: "+evento.getCuotaAdicionalEmision());
		
	}
	
	
	//ORGANIZADOR
	protected void viewEventoOrg(Evento evento) {
		System.out.println("Información de "+evento.getNombre());
		System.out.println("====================");
		System.out.println("Fecha: "+evento.getFecha());
		System.out.println("Tipo: "+evento.getTipoEvento());
		System.out.println("Cantidad tiquetes: "+evento.getTiquetes().size());
		System.out.println("Tiquetes vendidos: "+evento.getTiquetesVendidos().size());
		System.out.println("Recaudos: "+evento.getGanancias());
	}

}
