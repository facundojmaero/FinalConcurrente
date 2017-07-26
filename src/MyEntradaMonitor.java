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
			int hiloParaDespertar = cual();
			
			if(hiloParaDespertar == -1){
				return;
			}
			colasDeEntrada[hiloParaDespertar].release();
//			System.out.println(Thread.currentThread().getName() + " dejando entrar al hilo en transicion " + hiloParaDespertar);
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

	public int cual(){
		
		List<Integer> transiciones = getVectorHilos();
		
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


	public void setPolitica(Politicas politica) { this.politica = politica; }
	
}
