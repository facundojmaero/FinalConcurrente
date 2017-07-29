import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MyEntradaMonitor {

	private MySemaphore[] colasDeEntrada;
	private Semaphore semaforo;
	private Politicas politica;

	public MyEntradaMonitor(int nroTransiciones){
		
		semaforo = new Semaphore(1,true);
		
		colasDeEntrada = new MySemaphore[nroTransiciones];
		for (int i = 0; i < colasDeEntrada.length; i++) {
			colasDeEntrada[i] = new MySemaphore();
		}
	}
	
	/**
	 * Intenta obtener la entrada al mutex
	 * Dada una transicion a disparar, se intenta obtener el mutex con la operacion
	 * tryAcquire() sobre un semaforo.
	 * Si tiene exito retorna.
	 * Si no tiene exito espera en una cola correspondiente a la transicion t
	 *
	 * @param  t	Transicion actual a disparar
	 */
	public void acquire(int t){
		
		if(semaforo.tryAcquire() == false){
			colasDeEntrada[t].acquire();
		}
	}
	
	/**
	 * Libera la entrada del mutex, o despierta al hilo siguiente
	 * Si no hay nadie esperando en la entrada, libera el mutex.
	 * Si hay varios hilos, elige el que debe despertar, y se va.
	 *
	 */
	public void release(){
		
		int hilosEsperando = getQueueLength();
		
		if(hilosEsperando == 0){
			semaforo.release();
		}
		else {
			
			List<Integer> transiciones = getVectorHilos();
			colasDeEntrada[cual(transiciones)].release();
		}
		
	}
	
	/**
	 * Libera la entrada del mutex, o despierta al hilo siguiente, teniendose en cuenta a si mismo.
	 * Metodo similar a release(), con la diferencia que el hilo actual tambien se tiene en cuenta
	 * a la hora de elegir quien debe entrar luego.
	 *
	 * @param  t	Transicion del hilo actual
	 * @see	release()
	 */
	public void tryRelease(int t){
		
		int hilosEsperando = getQueueLength();
		
		if(hilosEsperando == 0){
			semaforo.release();
		}
		
		else {
			
			List<Integer> transiciones = getVectorHilos();
			transiciones.set(t, 1);
			
			int hiloParaDespertar = cual(transiciones);
			
			if(hiloParaDespertar == t){
				semaforo.release();
				return;
			}

			colasDeEntrada[hiloParaDespertar].release();
		}
	}
	
	/**
	 * Calcula la cantidad de hilos esperando
	 *
	 * @return	Cantidad de hilos esperando en la entrada
	 */
	private int getQueueLength(){
		
		int count = 0;
		
		for (int i = 0; i < colasDeEntrada.length; i++) {
			if(colasDeEntrada[i].isTaken()){
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Obtiene un vector con los hilos esperando (por transicion) para entrar en el mutex
	 *
	 * @return	Lista con los hilos esperando para entrar en el mutex
	 */
	private List<Integer> getVectorHilos(){
		
		List<Integer> vector = new ArrayList<Integer>();
		
		for (int i = 0; i < colasDeEntrada.length; i++) {
			
			if(colasDeEntrada[i].isTaken())	{ vector.add(1); }
			else 							{ vector.add(0); }		
		}
		
		return vector;
	}

	private int cual(List<Integer> transiciones){ return politica.cual(transiciones); }
	public void setPoliticas(Politicas newPoliticas) { politica = newPoliticas; }

}
