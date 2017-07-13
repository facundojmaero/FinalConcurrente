import java.util.List;

public class Hilo implements Runnable {

	GestorMonitor gestor;
	int transicionActual;
	List<Integer> transiciones = new MyLinkedList<Integer>();

	public Hilo(List<Integer> transiciones, GestorMonitor gestor) {
		this.gestor = gestor;
		this.transiciones = transiciones;
		transicionActual = ((MyLinkedList<Integer>) transiciones).getActual();
	}

	@Override
	public void run() {
//		for (int i = 0; i < 100; i++) {
		while(true){
			if (gestor.dispararTransicion(transicionActual) == 0) {
				// la transicion se disparo, avanzo a la siguiente
				
				int transicionActualAux = transicionActual;
				siguienteTransicion();
				
				gestor.writeToLog(transicionActualAux + " " + transicionActual + " " + Thread.currentThread().getName());
			}
		}
//		System.out.println("Terminando de correr hilo " + Thread.currentThread().getName());
//		gestor.verMarcado();
	}

	public int getTransicionActual() {
		return transicionActual;
	}

	public void siguienteTransicion() {
		((MyLinkedList<Integer>) transiciones).avanzar();
		transicionActual = ((MyLinkedList<Integer>) transiciones).getActual();
	}

}
