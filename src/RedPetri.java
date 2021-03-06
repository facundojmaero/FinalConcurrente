import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedPetri {
	private static final Exception IllegalStateException = null;
	
	int M0[];	//Marcado inicial
	int I[][]; 	//red de petri
	int S[];	//vector de disparo
	int transiciones;
	int[][] invariantes;
	int[] resultadoInvariantes;
	
	private Tiempo tiempo;
	
	public RedPetri(int transiciones, int I[][], int M[], int[][] invariantes, int[] resultadoInvariantes, int[] tiempos){
		this.transiciones = transiciones;
		this.invariantes = invariantes;
		this.resultadoInvariantes = resultadoInvariantes;
		S = new int[transiciones];
		this.I = I;
		M0 = M;
		
		if(tiempos != null) { tiempo = new Tiempo(this.get_sensibilizadas(), tiempos); }
		else				{ tiempo = new Tiempo(this.get_sensibilizadas(), new int[transiciones]); }
		
	}
	
	private void setTransicionEnVector(int transicion){
		// Pongo 0 en todas las transiciones que no quiero disparar y 1 en la que si voy a disparar
		for (int i=0;i<transiciones;i++){
			S[i] = 0;
		}
		S[transicion] = 1;
	}
	
	private boolean estaSensibilizada(int transicion){
		List<Integer> sensibilizadas = get_sensibilizadas();
		if(sensibilizadas.get(transicion) == 1)	{return true;}
		else 									{return false;}
	}
	
	
	/**
	 * Intenta disparar una transicion dada e informa del resultado.
	 * 
	 * Si el disparo no se puede hacer (porque la transicion no esta sensibilizada), se revierte el disparo
	 * y se informa del error.
	 * Si la transicion puede dispararse, se comprueba la ventana de tiempo.
	 * Si todo es correcto se dispara. Sino no se hace nada, y se informa del resultado
	 *
	 * @param  	transicion	Transicion a disparar
	 * @return	Resultado del disparo
	 */
	public int disparar(int transicion){
		
		setTransicionEnVector(transicion);
		boolean sensib = estaSensibilizada(transicion);
		
		if(sensib){
			int testTiempo = tiempo.testVentanaTiempo(transicion);
			
			switch (testTiempo){
				case -1:
					//estoy antes del alfa, tengo que dormir y salir del monitor
					
					tiempo.setEsperando(transicion);
					break;
				
				case -2:
					//estoy despues del beta
					break;
					
				case 1:
					//estoy en la ventana correcta, sigo la ejecucion
					List<Integer> oldSensibilizadas = get_sensibilizadas();
					M0 = sumarVectores(M0, multiplicarMatrices(I, S));
					List<Integer> actualSensibilizadas = get_sensibilizadas();
					tiempo.setNuevoTimeStamp(calcularNewSensibilizadas(oldSensibilizadas, actualSensibilizadas));
					tiempo.resetEsperando(transicion);
					break;
					
				default:
					break;
			}
			return testTiempo;
		}
		else{
			//transicion no sensibilizada, el hilo se va a esperar a la cola
			return -3;
		}
	}
	
	/**
	 * Calcula las transiciones sensibilizadas en la red
	 *
	 * @return	Vector con las transiciones sensibilizadas
	 */
	public List<Integer> get_sensibilizadas(){
		
		List<Integer> sensibilizadas = new ArrayList<Integer>(transiciones);
		
		for (int i = 0; i < transiciones; i++) { sensibilizadas.add(0); }
		
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
			
			resultado = sumarVectores(M0,multiplicarMatrices(I, tr));
			
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
		
		//lleno de 0 el vector
		for (int i = 0; i < vector.length; i++) {
			vector[i]=0;
		}
		//pongo un 1 donde corresponda
		vector[index] = 1;
		
		return vector;
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
	
	private int[] sumarVectores (int[] firstarray, int[] secondarray){
		
		int [] resultado = new int[secondarray.length];
		for (int i = 0; i < secondarray.length; i++) {
			resultado[i] = firstarray[i] + secondarray[i];
		}
		return resultado;
	}
	
	/**
	 * Revisa los invariantes de tokens en la red
	 *
	 * @return	Revision exitosa o si hubo un error
	 * @throws	Exception
	 */
	public boolean revisarInvariantes() throws Exception{
		for (int i = 0; i < invariantes.length; i++) {
			
			int check = 0;
			
			for (int j = 0; j < invariantes[0].length; j++) {
				
				if(invariantes[i][j] != 0)
					check += M0[j];
			}
			if(check != resultadoInvariantes[i]){
				printError(i);
				throw IllegalStateException;
			}
		}
		return true;
	}
	
	private void printError(int ecuacion){
		System.out.println("Error de invariantes, ecuacion " + ecuacion + " no se cumple");
		System.out.print("Suma de tokens en plazas ");
		
		int suma = 0;
		
		for (int i = 0; i < invariantes[0].length; i++) {
			if(invariantes[ecuacion][i] != 0){
				System.out.print(i + " ");
				suma += M0[i];
			}
		}
		System.out.println("debe ser " + resultadoInvariantes[ecuacion] + ", pero es " + suma);
		System.out.println("Marcado: "+ Arrays.toString(getMarcado()));
		
	}
	
	/**
	 * Calcula las transiciones sensibilizadas a partir de las anteriores y posteriores a un disparo dado
	 *
	 * @param  	oldSensibilizadas	Transiciones sensibilizadas antes del disparo
	 * @param  	newSensibilizadas	Transiciones sensibilizadas despues del disparo
	 * @return	Vector con las transiciones que se sensibilizaron luego del disparo
	 */
	private List<Integer> calcularNewSensibilizadas(List<Integer> oldSensibilizadas,List<Integer> actualSensibilizadas){
		
		List<Integer> newSensibilizadas = new ArrayList<Integer>();
		
		for(int i=0;i<oldSensibilizadas.size();i++){
			int sensibilizada = 0;
			sensibilizada = actualSensibilizadas.get(i) - oldSensibilizadas.get(i);
			newSensibilizadas.add(sensibilizada);
		}
		
		return newSensibilizadas;
	}

	public int[] getMarcado(){ return M0; }
	public long getTimeSleep(int transicion) { return tiempo.getTimeSleep(transicion); }
}


