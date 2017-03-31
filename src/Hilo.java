import java.util.ArrayList;
import java.util.List;

public class Hilo implements Runnable {
	
	GestorMonitor gestor;
	int transicionActual;
	List<Integer> transiciones = new ArrayList<Integer>();
	
	public Hilo(List<Integer> transiciones, GestorMonitor gestor){
		this.gestor = gestor;
		this.transiciones = transiciones;
		for (int i = 0; i < transiciones.size(); i++) {
			if (transiciones.get(i) != -1){
				transicionActual = i;
				break;
			}
		} 
		 
	}

	@Override
	public void run() {
		while(true){
			gestor.dispararTransicion(transicionActual);
			siguienteTransicion();
		}
	}
	
	public int getTransicionActual(){
		return transicionActual;
	}
	
	public void siguienteTransicion(){
		transicionActual = transiciones.get(transicionActual);
	}

}
