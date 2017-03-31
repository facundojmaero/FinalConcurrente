
public class Hilo implements Runnable {
	
	GestorMonitor gestor;
	int transicionActual;
	int[] transiciones;
	
	public Hilo(int[] transiciones, GestorMonitor gestor){
		this.gestor = gestor;
		this.transiciones = transiciones;
		for (int i = 0; i < transiciones.length; i++) {
			if (transiciones[i] != -1){
				transicionActual = i;
				break;
			}
		} 
		 
	}

	@Override
	public void run() {
		gestor.dispararTransicion(transicionActual);
		siguienteTransicion();
	}
	
	public int getTransicionActual(){
		return transicionActual;
	}
	
	public void siguienteTransicion(){
		transicionActual = transiciones[transicionActual];
	}

}
