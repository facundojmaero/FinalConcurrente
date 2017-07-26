import java.util.ArrayList;
import java.util.List;

public class Politicas {
	
	private int[] prioridades;
	private List<ArrayList<Integer>> transicionesPorPieza;
	private int[][] matrizOrdenPrioridades;

	public Politicas(int nroPiezas){
		
		prioridades = new int[nroPiezas];
		
		for (int i = 0; i < nroPiezas; i++) {
			prioridades[i] = i;
		} 
	}

	public int cual(List<Integer> transiciones){
		
		if(matrizOrdenPrioridades == null){
			initMatrizPrioridades(transiciones.size());
		}
		
		int transicion = contarTransicionesSensibilizadas(transiciones);
		if(transicion != -1) { return transicion; }
		
		int[] transicionesSensibilizadas = ponerIndices(transiciones);
		
		transicionesSensibilizadas = multiplicarMatrices(matrizOrdenPrioridades, transicionesSensibilizadas);
		
		return getReturnIndex(transicionesSensibilizadas);
	}
	
	private int getReturnIndex(int[] transiciones){
		
		int index = 0;
		
		for (int i = 0; i < transiciones.length; i++) {
			
			if(transiciones[i] == -1) 	{ continue; }
			else 						{ index = transiciones[i]; break;}
		}
		
		return index;
	}
	
	private void initMatrizPrioridades(int size) {
		
		matrizOrdenPrioridades = new int[size][size];
		
		ordenarMatrizPrioridades(prioridades);
		
	}
	
	private int llenarMatrizConFor(int inicio, ArrayList<Integer> vector){
		
		int j = inicio;
		
		for (int i = 0; i < vector.size(); i++) {
			
			matrizOrdenPrioridades[j][vector.get(i)] = 1;
			j++;
		}	
		
		return j;
	}
	
	private void ordenarMatrizPrioridades(int[] prioridades){
		
		for(int i = 0; i < matrizOrdenPrioridades.length; i++){
			for(int j = 0; j < matrizOrdenPrioridades[0].length; j++){
				matrizOrdenPrioridades[i][j] = 0;
			}
		}
		
		int inicio = 0;
		
		for (int i = 0; i < transicionesPorPieza.size(); i++) {
			inicio = llenarMatrizConFor(inicio, transicionesPorPieza.get(prioridades[i]));
		}
		
//		for(int i = 0; i < matrizOrdenPrioridades.length; i++){
//			for(int j = 0; j < matrizOrdenPrioridades[0].length; j++){
//				System.out.print(matrizOrdenPrioridades[i][j] + "\t");
//			}
//			System.out.println(" ");
//		}
		
	}

	private int[] ponerIndices(List<Integer> transiciones) {

		int[] transicionesConIndices = new int[transiciones.size()];
		
		for (int i = 0; i < transicionesConIndices.length; i++) {
			
			if(transiciones.get(i) == 1) 	{ transicionesConIndices[i] = i; }
			else 							{ transicionesConIndices[i] = -1; }
		}
		
		return transicionesConIndices;
	}

	private int[] multiplicarMatrices (int[][] firstarray,int[] secondarray){
		int [] result = new int[firstarray.length];
		
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < firstarray[0].length; j++) {
				result[i] += firstarray[i][j] * secondarray[j];
			}
		}
		return result;
	}
	
	private int contarTransicionesSensibilizadas(List<Integer> transiciones){
		
		int cant = 0;
		for (int i = 0; i < transiciones.size(); i++) {
			if(transiciones.get(i) == 1)
				cant++;
		}
		
		if(cant == 1) 	{ return transiciones.indexOf(1); }
		else			{ return -1; }

	}
	
	public List<ArrayList<Integer>> getMatrizTransiciones() { return transicionesPorPieza; }
	public void setMatrizTransiciones(ArrayList<ArrayList<Integer>> matrizPrioridades) { this.transicionesPorPieza = matrizPrioridades; }
	
	public void setPrioridades(int[] newPrioridades) { 
		prioridades = newPrioridades; 
		ordenarMatrizPrioridades(newPrioridades);
	}
	
	public int[] getPrioridades() {return prioridades; }
	
}
