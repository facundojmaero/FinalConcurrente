import java.util.ArrayList;

public class GestorPiezas {
	
	private int[] piezasTerminadas;
	private int[] politica;
	
	public GestorPiezas(int numeroPiezas){
		
		piezasTerminadas = new int[numeroPiezas];
		
		//por defecto se produce el mismo numero de piezas de cada tipo
		politica = new int[numeroPiezas];
		for (int i = 0; i < numeroPiezas; i++) {
			politica[i] = 1;
		} 
		
	}
	
	public synchronized void contarPieza(int tipoPieza){
		
		piezasTerminadas[tipoPieza]++;
		
		ArrayList<Double> produccionNormalizada = normalizarProduccion();
		double promedio = calcularPromedio(produccionNormalizada);
		produccionNormalizada = dividirPorPromedio(produccionNormalizada, promedio);
		
		printArray(produccionNormalizada);
//		produccionNormalizada.sort(null);
	}
	
	private void printArray(ArrayList<Double> array){
		
		for (int i = 0; i < array.size(); i++) {
			System.out.printf("%.2f  ", array.get(i));
		}
		System.out.println();
	}
	
	public void verProduccion(){
		
		for (int i = 0; i < piezasTerminadas.length; i++) {
			System.out.println("Pieza " + i + ": " + piezasTerminadas[i]);
		}
		System.exit(0);
	}
	
	public int[] getPolitica() { return politica; }
	public void setPolitica(int[] newPolitica) {politica = newPolitica; }
	
	private ArrayList<Double> normalizarProduccion(){
		
		ArrayList<Double> produccionNormalizada = new ArrayList<Double>();
		
		for (int i = 0; i < politica.length; i++) {
			produccionNormalizada.add((double) (piezasTerminadas[i] / politica[i]));
		}
		return produccionNormalizada;
	}
	
	private double calcularPromedio(ArrayList<Double> produccion){
		
		double promedio = 0;
		for (int i = 0; i < produccion.size(); i++) {
			promedio += produccion.get(i);
		}
		return promedio / produccion.size();
	}
	
	private ArrayList<Double> dividirPorPromedio(ArrayList<Double> produccion, double promedio){
		
		ArrayList<Double> returnArray = new ArrayList<Double>();
		
		for (int i = 0; i < produccion.size(); i++) {
			returnArray.add(i, produccion.get(i) / promedio);
		}
		
		return returnArray;
	}
	
}
