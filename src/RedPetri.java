import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class RedPetri {
	int M0[] = {1,0,0};					//marcado inicial
	int I[][] = {{-1,1},{1,-1},{0,1}};	//red
	int S[];	//semaforos
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
		int MTemp[] = sumar(M0, multiplicar(I, S));
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
		List<Integer> sensibilizadas = new ArrayList<Integer>(transiciones);
		sensibilizadas.add(0, 0);
		sensibilizadas.add(1, 0);
		int[] tr = new int[transiciones], resultado;
		for (int index = 0; index < transiciones; index++)
		{
		    //compruebo una a una si las transiciones estan sensibilizadas
			//en funcion del marcado actual
			
			cycleTransicion(index, tr);
			
			//pongo un 1 en la transicion index previamente. Si llegara a no estar sensibilizada
			//pongo un 0 fijandome
			sensibilizadas.set(index, 1);
			//multiplico la matriz por el vector que trata de disparar una transicion dada
			//si resultado tiene un elemento menor a 0 la transicion no puede dispararse
			resultado = multiplicar(I, tr);
			for (int j = 0; j < resultado.length; j++) {
				if(resultado[j]<0){
					sensibilizadas.set(index, 0);
				}
			}
			
		}
		return sensibilizadas;
	}
	
	//va moviendo un 1 por el vector de transiciones, el resto son 0
	//se usa para saber que transiciones estan sensibilizadas
	private int[] cycleTransicion(int index, int[] vector){
		
		//leno de 0 vector
		for (int i = 0; i < vector.length; i++) {
			vector[i]=0;
		}
		//pongo un 1 donde corresponda
		vector[index] = 1;
		
		return vector;
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

	public int[] getMarcado(){
		return M0;
	}
}

