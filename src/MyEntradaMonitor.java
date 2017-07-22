import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MyEntradaMonitor {

	private int[] prioridades;
	private List<ArrayList<Integer>> matrizTransiciones;
	private MySemaphore[] colasDeEntrada;
	private Semaphore semaforo;

	public MyEntradaMonitor(int nroPiezas, int nroTransiciones){
		
		prioridades = new int[nroPiezas];
		semaforo = new Semaphore(1,true);
		
		for (int i = 0; i < nroPiezas; i++) {
			prioridades[i] = i;
		}
		
		colasDeEntrada = new MySemaphore[nroTransiciones];
		for (int i = 0; i < colasDeEntrada.length; i++) {
			colasDeEntrada[i] = new MySemaphore();
		}
	}
	
	public void acquire(int t){
		
		if(semaforo.tryAcquire() == false){
			colasDeEntrada[t].acquire();
		}
		
	}
	
	public void release(){
		
		int hilosEsperando = getQueueLength();
		
		if(hilosEsperando == 0){
			semaforo.release();
//			System.out.println(Thread.currentThread().getName() + " release sin otros hilos en cola");
		}
		else{
			int hiloParaDespertar = cual();
//			System.out.println(Thread.currentThread().getName() + " voy a dejar entrar al hilo en transicion " + hiloParaDespertar);
			
			if(hiloParaDespertar == -1){
				return;
			}
			colasDeEntrada[hiloParaDespertar].release();
		}
		
	}
	
	
	private int getQueueLength(){
		
		int count = 0;
		
		for (int i = 0; i < colasDeEntrada.length; i++) {
			if(colasDeEntrada[i].isTaken()){
				count++;
			}
		}
		
		return count;
	}
	
	private int cual(){
		
		List<Integer> transiciones = getVectorHilos();
		
		int cant = 0;
		for (int i = 0; i < transiciones.size(); i++) {
			if(transiciones.get(i) == 1){
				cant++;
			}
		}
		
		if(cant==1){
			return transiciones.lastIndexOf(1);
		}
		
		List<Integer> vectorOpciones = new ArrayList<Integer>();
		
		for (int i = 0; i < prioridades.length; i++) {
			vectorOpciones = andVectores(transiciones, matrizTransiciones.get(prioridades[i]));
			
			if(vectorOpciones.indexOf(1) != -1){
				return vectorOpciones.lastIndexOf(1);
			}
		}
		
//		System.out.println("Error en politicas de entrada");
		
		return transiciones.lastIndexOf(1);
	}
	
	private List<Integer> andVectores(List<Integer> vector1, List<Integer> vector2) {
		List<Integer> result = new ArrayList<Integer>(vector1.size());

		for (int i = 0; i < vector1.size(); i++) {
			result.add(vector1.get(i) & vector2.get(i));
		}

		return result;
	}
	
	private List<Integer> getVectorHilos(){
		
		List<Integer> vector = new ArrayList<Integer>();
		
		for (int i = 0; i < colasDeEntrada.length; i++) {
			
			if(colasDeEntrada[i].isTaken())	{ vector.add(1); }
			else 							{ vector.add(0); }
		
		}
		
		return vector;
	}
	
	public List<ArrayList<Integer>> getMatrizTransiciones() { return matrizTransiciones; }
	public void setMatrizTransiciones(ArrayList<ArrayList<Integer>> matrizPrioridades) { this.matrizTransiciones = matrizPrioridades; }
	
	public void setPrioridades(int[] newPrioridades) { prioridades = newPrioridades; }
	public int[] getPrioridades() {return prioridades; }
	
}
