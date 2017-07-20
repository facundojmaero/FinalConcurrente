import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MyEntradaMonitor {
	
	private boolean taken;
	private int[] prioridades;
	private List<ArrayList<Integer>> matrizTransiciones;
	private MySemaphore[] colasDeEntrada;

	public MyEntradaMonitor(int nroPiezas, int nroTransiciones){
		
		taken = false;
		prioridades = new int[nroPiezas];
		
		for (int i = 0; i < nroPiezas; i++) {
			prioridades[i] = i;
		}
		
//		prioridades[0] = 2;
//		prioridades[1] = 1;
//		prioridades[2] = 0;
		
		colasDeEntrada = new MySemaphore[nroTransiciones];
		for (int i = 0; i < colasDeEntrada.length; i++) {
			colasDeEntrada[i] = new MySemaphore();
		}
	}
	
	public void acquire(int t){
		
		if(taken){
			colasDeEntrada[t].acquire();
		}
		else{
			taken = true;
		}
		
	}
	
	public void release(){
		
		int hilosEsperando = getQueueLength();
		
		if(hilosEsperando == 0){
			taken = false;
//			System.out.println(Thread.currentThread().getName() + " release sin otros hilos en cola");
		}
		else{
			int hiloParaDespertar = cual();
//			System.out.println(Thread.currentThread().getName() + " voy a dejar entrar al hilo en transicion " + hiloParaDespertar);
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
		
		int a = 0, b = 0;
		for (int i = 0; i < transiciones.size(); i++) {
			if(transiciones.get(i) == 1){
				a++;
			}
		}
		
		if(a>1){
			b = 1;
		}
		
		List<List<Integer>> vectorOpciones = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < prioridades.length; i++) {
			vectorOpciones.add(andVectores(transiciones, matrizTransiciones.get(prioridades[i])));
			
			if(vectorOpciones.get(i).contains(1)){
				return vectorOpciones.get(i).indexOf(1);
			}
		}
		
		System.out.println("Error en politicas de entrada");
		return transiciones.indexOf(1);
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
