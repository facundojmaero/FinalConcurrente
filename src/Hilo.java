import java.util.List;

public class Hilo implements Runnable {
	
	GestorMonitor gestor;
	int transicionActual;
	List<Integer> transiciones = new MyLinkedList<Integer>();
	
	public Hilo(List<Integer> transiciones, GestorMonitor gestor){
		this.gestor = gestor;
		this.transiciones = transiciones;
		transicionActual = ((MyLinkedList<Integer>) transiciones).getActual();
//		for (int i = 0; i < transiciones.size(); i++) {
//			if (transiciones.get(i) != -1){
//				transicionActual = i;
//				break;
//			}
//		} 
		 
	}

	@Override
	public void run() {
		for (int i = 0; i < 20; i++) {
			gestor.dispararTransicion(transicionActual);
			siguienteTransicion();
			try {
				Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Terminando de correr hilo");
		gestor.verMarcado();
	}
	
	public int getTransicionActual(){
		return transicionActual;
	}
	
	public void siguienteTransicion(){
		((MyLinkedList<Integer>) transiciones).avanzar();
		transicionActual = ((MyLinkedList<Integer>) transiciones).getActual();
	}

}
