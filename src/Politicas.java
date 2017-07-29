import java.util.ArrayList;
import java.util.List;

public class Politicas {
	
	private int[] prioridades;
	private List<ArrayList<Integer>> matrizTransiciones; 

	public Politicas(int nroPiezas){
		
		prioridades = new int[nroPiezas];
		
		for (int i = 0; i < nroPiezas; i++) {
			prioridades[i] = i;
		} 
	}

	/**
	 * Decide entre un grupo de transiciones la que debe ser disparada.
	 * Permite elegir la transicion a disparar entre un conjunto de opciones.
	 *
	 * @param  	transiciones	Vector con transiciones posibles a ser disparadas actualmente.
 	 * @return 	Indice de la transicion a disparar
	 */
	public int cual(List<Integer> transiciones){
		
		int cant = 0;
		for (int i = 0; i < transiciones.size(); i++) {
			if(transiciones.get(i) == 1)
				cant++;
		}
		
		if(cant == 1){
			return transiciones.indexOf(1);
		}
		
		for (int i = 0; i < prioridades.length; i++) {
			
			int transicionElegida = buscarTransicion(transiciones, matrizTransiciones.get(prioridades[i]));	
			if(transicionElegida != -1) {return transicionElegida;}
			
		}
		
		return transiciones.indexOf(1);
	}

	/**
	 * Busca entre las alternativas dadas la transicion mas importante a disparar despues
	 * Recibe opciones de disparo, y las prioridades para considerarlas, y comprueba las mismas.
	 * Retorna el indice de la transicion que considera debe ser disparada luego, por orden de
	 * importancia.
	 *
	 * @param  	opciones		Vector con las posibles transiciones a disparar
 	 * @param  	prioridades	Vector con el orden en el que debe comprobarse el arreglo de opciones
	 * @return 	Indice de la transicion siguiente a ser disparada
	 */
	private int buscarTransicion(List<Integer> opciones, ArrayList<Integer> prioridades) {
		
		for (int i = 0; i < prioridades.size(); i++) {
			if( opciones.get(prioridades.get(i)) == 1){
				return prioridades.get(i);
			}
		}
		
		return -1;
	}
	
	public List<ArrayList<Integer>> getMatrizTransiciones() { return matrizTransiciones; }
	public void setMatrizTransiciones(ArrayList<ArrayList<Integer>> matrizPrioridades) { this.matrizTransiciones = matrizPrioridades; }
	
	public void setPrioridades(int[] newPrioridades) { prioridades = newPrioridades; }
	public int[] getPrioridades() {return prioridades; }
	
}
