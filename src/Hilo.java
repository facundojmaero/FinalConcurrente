import java.util.List;

public class Hilo implements Runnable {

	GestorMonitor gestorMonitor;
	int transicionActual;
	List<Integer> transiciones = new MyLinkedList<Integer>();
	int tipoPieza = -1;
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
		
		for (int i = 0; i < 500; i++) {
//		while(true){
			int j = 0;
			while(j<transiciones.size()){
				
				if (gestorMonitor.dispararTransicion(transicionActual) == 0) {
					// la transicion se disparo, avanzo a la siguiente
					
					j++;
//					int transicionActualAux = transicionActual;
					siguienteTransicion();
					
//					gestorMonitor.writeToLog(transicionActualAux + " " + transicionActual + " " + Thread.currentThread().getName());
				}
			}
			
			terminarVuelta();
		}
		System.out.println("Terminando de correr hilo " + Thread.currentThread().getName());
		gestorPiezas.verProduccion();
	}
	
	
	private void terminarVuelta(){
		
		if(tipoPieza >= 0){
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
