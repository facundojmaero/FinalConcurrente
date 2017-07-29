import java.util.List;

public class Hilo implements Runnable {

	GestorMonitor gestorMonitor;
	int transicionActual;
	List<Integer> transiciones = new MyLinkedList<Integer>();
	int tipoPieza = -1;
	GestorPiezas gestorPiezas;
	

	public Hilo(List<Integer> transiciones, GestorMonitor gestor, GestorPiezas gestorPiezas) {
		this.gestorMonitor = gestor;
		this.transiciones = transiciones;
		transicionActual = ((MyLinkedList<Integer>) transiciones).getActual();
		this.gestorPiezas = gestorPiezas;
	}

	
	/**
	 * Dispara las transiciones que se le asignan en la red, por medio del Monitor
	 * Metodo principal del hilo. Intenta disparar transiciones en un bucle
	 * por medio del gestor del monitor.
	 * Si pudo disparar correctamente, avanza a la transicion siguiente en su lista.
	 * Avisa al gestor de piezas si se finalizo la construccion de una pieza.
	 * Guarda en el log las proporciones de produccion.
	 */
	@Override
	public void run() {
		
//		for (int i = 0; i < 500; i++) {
		while(true){
			int j = 0;
			while(j<transiciones.size()){
				
				if (gestorMonitor.dispararTransicion(transicionActual) == 0) {
					// la transicion se disparo, avanzo a la siguiente
					
					j++;
					siguienteTransicion();
				}
			}
			
			terminarVuelta();
		}
//		System.out.println("Terminando de correr hilo " + Thread.currentThread().getName());
//		gestorPiezas.verProduccion();
	}
	
	/**
	 * Avisa al gestor de piezas que se termino de fabricar una pieza, si corresponde.
	 *
	 */
	private void terminarVuelta(){
		
		if(tipoPieza >= 0){
			gestorPiezas.contarPieza(tipoPieza);
		}
	}
	
	public int getTipoPieza() { return tipoPieza; }
	public void setTipoPieza(int newTipoPieza) { tipoPieza = newTipoPieza; }

	public int getTransicionActual() { return transicionActual; }
	public void setTransicionActual(int newTransicion) { transicionActual = newTransicion; }
	
	
	/**
	 * Avanza a la siguiente transicion en la lista del hilo.
	 * 
	 */
	public void siguienteTransicion() {
		((MyLinkedList<Integer>) transiciones).avanzar();
		setTransicionActual( ((MyLinkedList<Integer>) transiciones).getActual() );
	}

}
