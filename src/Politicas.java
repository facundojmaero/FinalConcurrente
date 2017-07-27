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
