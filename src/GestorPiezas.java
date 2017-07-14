
public class GestorPiezas {
	
	int[] piezasTerminadas;
	
	public GestorPiezas(int numeroPiezas){
		
		piezasTerminadas = new int[numeroPiezas];
		
	}
	
	public synchronized void contarPieza(int tipoPieza){
		
		piezasTerminadas[tipoPieza]++;
		
	}
	
	public void verProduccion(){
		
		for (int i = 0; i < piezasTerminadas.length; i++) {
			System.out.println("Pieza " + i + ": " + piezasTerminadas[i]);
		}
		System.exit(0);
	}
	
}
