import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedPetri {
	int M0[] = {1,0,0};
	int I[][] = {{-1,1},{1,-1},{0,1}};
	int S[];
	int transiciones;

	
	public RedPetri(int transiciones){
		this.transiciones = transiciones;
		S = new int[transiciones];
	}
	
	public boolean disparar(int transicion){
		// Pongo 0 en todas las transiciones que no quiero disparar y 1 en la que si voy a disparar
		for (int i=0;i<transiciones;i++){
			S[i] = 0;
		}
		S[transicion] = 1;
		//int MTemp[] =  this.sumar(M0, this.multiplicar(I, S));
		int MTemp[] = this.sumar(M0, this.multiplicar(I, S));
		boolean disparar = true;
		for (int i = 0; i < MTemp.length; i++) {
			if (MTemp[i]<0){
				disparar = false;
			}
		}
		if(disparar){
			M0 = MTemp;
		}
		
		return disparar;
	}
	public List<Integer> get_sensibilizadas(){
		List<Integer> sensibilizadas = new ArrayList<Integer>();
		for (int index = 0; index < M0.length; index++)
		{
		    sensibilizadas.add(M0[index]);
		}
		return sensibilizadas;
	}
	
	private int[] multiplicar (int[][] firstarray,int[] secondarray){
		/* Create another 2d array to store the result using the original arrays' lengths on row and column respectively. */
		int [] result = new int[firstarray.length];
		
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < firstarray[0].length; j++) {
				result[i] += firstarray[i][j] * secondarray[j];
			}
		}
//
//		/* Loop through each and get product, then sum up and store the value */
//		for (int i = 0; i < firstarray.length; i++) { 
//		    for (int j = 0; j < secondarray.length; j++) { 
//		        for (int k = 0; k < firstarray[0].length; k++) { 
//		            result[i] += firstarray[i][k] * secondarray[k];
//		        }
//		    }
//		}
		return result;
	}
	
	private int[] sumar (int[] firstarray, int[] secondarray){
		int [] resultado = new int[secondarray.length];
		for (int i = 0; i < secondarray.length; i++) {
			resultado[i] = firstarray[i] + secondarray[i];
		}
		return resultado;
	}


}

