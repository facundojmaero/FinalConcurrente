import java.util.List;

public class Hilo implements Runnable {

	GestorMonitor gestorMonitor;
	int transicionActual;
	List<Integer> transiciones = new MyLinkedList<Integer>();
	int tipoPieza;
	GestorPiezas gestorPiezas;
	int cantPiezas;
	

	public Hilo(List<Integer> transiciones, GestorMonitor gestor, GestorPiezas gestorPiezas) {
		this.gestorMonitor = gestor;
		this.transiciones = transiciones;
		transicionActual = ((MyLinkedList<Integer>) transiciones).getActual();
		this.gestorPiezas = gestorPiezas;
		cantPiezas = 0;
	}

	@Override
	public void run() {
		
		for (int i = 0; i < 10; i++) {
//		while(true){
			
			for (int j = 0; j < transiciones.size(); j++) {
				
				if (gestorMonitor.dispararTransicion(transicionActual) == 0) {
					// la transicion se disparo, avanzo a la siguiente
					
					int transicionActualAux = transicionActual;
					siguienteTransicion();
					
					gestorMonitor.writeToLog(transicionActualAux + " " + transicionActual + " " + Thread.currentThread().getName());
				}
			}
			
			terminarVuelta();
		}
		System.out.println("Terminando de correr hilo " + Thread.currentThread().getName());
		gestorPiezas.verProduccion();
	}
	
	
	private void terminarVuelta(){
		
		if(tipoPieza >= 0){
//			cantPiezas++;
//			System.out.println(Thread.currentThread().getName() + " produje " + cantPiezas + " del tipo " + tipoPieza);
			gestorPiezas.contarPieza(tipoPieza);
		}
	}
	
	public int getTipoPieza() { return tipoPieza; }
	public void setTipoPieza(int newTipoPieza) { tipoPieza = newTipoPieza; }

	public int getTransicionActual() { return transicionActual; }
	public void setTransicionActual(int newTransicion) { transicionActual = newTransicion; }
	
	public void siguienteTransicion() {
		((MyLinkedList<Integer>) transiciones).avanzar();
		setTransicionActual( ((MyLinkedList<Integer>) transiciones).getActual() );
	}

}
