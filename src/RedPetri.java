import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class RedPetri {
	private static final Exception IllegalStateException = null;
	
	int M0[];//Marcado inicial
	int I[][]; //red de petri
	int S[];	//vector de disparo
	int transiciones;
	Tiempo tiempo;
	Semaphore entradaMonitor;
	
	public RedPetri(int transiciones, int I[][], int M[], Semaphore entradaMonitor){
		this.transiciones = transiciones;
		S = new int[transiciones];
		this.I = I;
		M0 = M;
		this.tiempo = new Tiempo(this.get_sensibilizadas());
		this.entradaMonitor = entradaMonitor;
	}
	
	public int disparar(int transicion){
		// Pongo 0 en todas las transiciones que no quiero disparar y 1 en la que si voy a disparar
		for (int i=0;i<transiciones;i++){
			S[i] = 0;
		}
		S[transicion] = 1;
	
		int result = tiempo.testVentanaTiempo(transicion);
		
		if(result == -1){
			//estoy antes del alfa, tengo que dormir y salir del monitor
			tiempo.setEsperando(transicion);
			entradaMonitor.release();
			try {
//				System.out.println(Thread.currentThread().getName() + " Antes del alfa, durmiendo " + tiempo.getTimeSleep(transicion) + " ms");
				Thread.sleep(tiempo.getTimeSleep(transicion));
			} catch (InterruptedException e) {
//				System.out.print("Error hilo esperando alfa");
				e.printStackTrace();
			}
			return -1;
		}
//		else if (result == 1){
//			//estoy despues del beta
//		}
		else if (result == 0){
			//estoy en la ventana correcta, sigo la ejecucion
			
			List<Integer> oldSensibilizadas = get_sensibilizadas();
			int MTemp[] = sumar(M0, multiplicar(I, S));
			int disparar = 1;
			for (int i = 0; i < MTemp.length; i++) {
				if (MTemp[i]<0){
					disparar = 0;
				}
			}
			if(disparar == 1){
				M0 = MTemp;
				List<Integer> actualSensibilizadas = get_sensibilizadas();
				tiempo.setNuevoTimeStamp(calcularNewSensibilizadas(oldSensibilizadas, actualSensibilizadas));
			}
			
			return disparar;
		}
		return -1;
	}
	
	
	//Devuelve un ArrayList con 1 donde la transicion esta sensibilizada
	public List<Integer> get_sensibilizadas(){
		List<Integer> sensibilizadas = new ArrayList<Integer>(transiciones);
//		sensibilizadas.add(0, 0);
//		sensibilizadas.add(1, 0);
		for (int i = 0; i < transiciones; i++) {
			sensibilizadas.add(0);
		}
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
			resultado = sumar(M0,multiplicar(I, tr));
			for (int j = 0; j < resultado.length; j++) {
				if(resultado[j]<0){
					sensibilizadas.set(index, 0);
				}
			}
//			sensibilizadas.set(index, 0);
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
	
	public boolean revisarInvariantes() throws Exception{
		boolean invariante = true;
		if (!(M0[0] + M0[1] == 1)){
			invariante = false;
		}
		if (!(M0[1] + M0[2] + M0[4] + M0[5] == 1)){
			invariante = false;
		}
		if(!(M0[3] + M0[4] == 1)){
			invariante = false;
		}
		
		if (!invariante){
			throw IllegalStateException;
		}
		
		return invariante;
	
	}
	
	//Devuelve un vector con las transiciones nuevas que se sensibilizaron
	private List<Integer> calcularNewSensibilizadas(List<Integer> oldSensibilizadas,List<Integer> actualSensibilizadas){
		List<Integer> newSensibilizadas = new ArrayList<Integer>();
		for(int i=0;i<oldSensibilizadas.size();i++){
			int sensibilizada = 0;
			sensibilizada = actualSensibilizadas.get(i) - oldSensibilizadas.get(i);
			newSensibilizadas.add(sensibilizada);
		}
		return newSensibilizadas;
	}
}

