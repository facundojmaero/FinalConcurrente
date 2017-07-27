import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MyEntradaMonitor {

	private MySemaphore[] colasDeEntrada;
	private Semaphore semaforo;
	private Politicas politica;

	public MyEntradaMonitor(int nroPiezas, int nroTransiciones){
		
		semaforo = new Semaphore(1,true);
		
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
		}
		else{
			
			List<Integer> transiciones = getVectorHilos();
			int hiloParaDespertar = cual(transiciones);
			
			if(hiloParaDespertar == -1){
				semaforo.release();
				return;
			}
			colasDeEntrada[hiloParaDespertar].release();
		}
		
	}
	
	public void tryRelease(int t){
		
		int hilosEsperando = getQueueLength();
		
		if(hilosEsperando == 0){
			semaforo.release();
		}
		else{
			
			List<Integer> transiciones = getVectorHilos();
			transiciones.set(t, 1);
			
			int hiloParaDespertar = cual(transiciones);
			
			if(hiloParaDespertar == -1){
				semaforo.release();
				return;
			}
			
			if(hiloParaDespertar == t){
				semaforo.release();
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
	
	private int cual(List<Integer> transiciones){
		
		int cual = politica.cual(transiciones);
		
		return cual;
	}
	
	private List<Integer> getVectorHilos(){
		
		List<Integer> vector = new ArrayList<Integer>();
		
		for (int i = 0; i < colasDeEntrada.length; i++) {
			
			if(colasDeEntrada[i].isTaken())	{ vector.add(1); }
			else 							{ vector.add(0); }
		
		}
		
		return vector;
	}

	public void setPoliticas(Politicas newPoliticas) {
		politica = newPoliticas;
		
	}
	
	
	
}
