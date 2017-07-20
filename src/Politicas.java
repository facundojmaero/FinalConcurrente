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
		
//		prioridades[0] = 2;
//		prioridades[1] = 1;
//		prioridades[2] = 0;
	}
	
	public int cual(List<Integer> transiciones){
		
		
		int cant = 0;
		for (int i = 0; i < transiciones.size(); i++) {
			if(transiciones.get(i) == 1)
				cant++;
		}
		if(cant >1){
//			System.out.println(transiciones);
		}
		
//		System.out.println(transiciones);
		
		List<List<Integer>> vectorOpciones = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < prioridades.length; i++) {
			vectorOpciones.add(andVectores(transiciones, matrizTransiciones.get(prioridades[i])));
			
			if(vectorOpciones.get(i).contains(1)){
				return vectorOpciones.get(i).indexOf(1);
			}
		}
		
		System.out.println("Error en politicas");
		return transiciones.indexOf(1);
	}
	
	private List<Integer> andVectores(List<Integer> vector1, List<Integer> vector2) {
		List<Integer> result = new ArrayList<Integer>(vector1.size());

		for (int i = 0; i < vector1.size(); i++) {
			result.add(vector1.get(i) & vector2.get(i));
		}

		return result;
	}
	
	public List<ArrayList<Integer>> getMatrizTransiciones() { return matrizTransiciones; }
	public void setMatrizTransiciones(ArrayList<ArrayList<Integer>> matrizPrioridades) { this.matrizTransiciones = matrizPrioridades; }
	
	public void setPrioridades(int[] newPrioridades) { prioridades = newPrioridades; }
	public int[] getPrioridades() {return prioridades; }
	
}
