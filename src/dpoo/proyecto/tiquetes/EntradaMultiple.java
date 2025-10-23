package dpoo.proyecto.tiquetes;

public abstract class EntradaMultiple extends Tiquete {

	public EntradaMultiple(double precioOriginal, int id, int maximoTiquetesPorTransaccion) {
		super(precioOriginal, id, maximoTiquetesPorTransaccion);
	}
	
	private double precioGrupal;
	private double precioIndividual;
	
}
