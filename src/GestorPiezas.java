import java.util.ArrayList;

public class GestorPiezas {
	
	private int[] piezasTerminadas;
	private int[] proporcionesProduccion;
	private int[] prioridades;
	private Politicas politica;
	private MyEntradaMonitor entradaMonitor;

	public GestorPiezas(int numeroPiezas){
		
		piezasTerminadas = new int[numeroPiezas];
		
		//por defecto se produce el mismo numero de piezas de cada tipo
		//primeramente las prioridades son 0 1 2
		proporcionesProduccion = new int[numeroPiezas];
		prioridades = new int[numeroPiezas];
		
		for (int i = 0; i < numeroPiezas; i++) {
//			proporcionesProduccion[i] = 1;
			prioridades[i] = i;
		} 
		proporcionesProduccion[0] = 1;
		proporcionesProduccion[1] = 1;
		proporcionesProduccion[2] = 1;
	}
	
	public synchronized void contarPieza(int tipoPieza){
		
		piezasTerminadas[tipoPieza]++;
		
		ArrayList<Double> produccionNormalizada = normalizarProduccion();
		double promedio = calcularPromedio(produccionNormalizada);
		produccionNormalizada = dividirPorPromedio(produccionNormalizada, promedio);
		
		printArray(produccionNormalizada);
//		produccionNormalizada.sort(null);
		
		prioridades = ordenarPrioridades(produccionNormalizada);
		
		if(politica != null){
			politica.setPrioridades(prioridades);
			entradaMonitor.setPrioridades(prioridades);
			System.out.print("Nuevas prioridades: ");
			for (int i = 0; i < piezasTerminadas.length; i++) {
				System.out.print(prioridades[i] + " ");
			}
			System.out.println();
		}
		
	}
	
	private void verPiezasTerminadas(){
		for (int i = 0; i < piezasTerminadas.length; i++) {
			System.out.print(piezasTerminadas[i] + " ");
		}
		System.out.println();
	}
	
	private int[] ordenarPrioridades(ArrayList<Double> array){
		int[] prioridades = new int[array.size()];
		
		ArrayList<Double> arrayCopia = (ArrayList<Double>) array.clone();
		arrayCopia.sort(null);
		
		for (int i = 0; i < prioridades.length; i++) {
			prioridades[i] = array.indexOf(arrayCopia.get(i));
			array.set(prioridades[i], (double) -1);
		}
		
		return prioridades;
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
		
		System.out.printf("Politica: ");
		
		for (int i = 0; i < proporcionesProduccion.length; i++) {
			System.out.printf("%d ", proporcionesProduccion[i]);
		} 
		
		System.exit(0);
	}
	
	public int[] getProporciones() { return proporcionesProduccion; }
	public void setProporciones(int[] newPolitica) {proporcionesProduccion = newPolitica; }
	
	public Politicas getPolitica() { return politica; }
	public void setPolitica(Politicas politica) { this.politica = politica; }
	
	public void setEntradaMonitor(MyEntradaMonitor newEntradaMonitor) { entradaMonitor = newEntradaMonitor; }
	
	private ArrayList<Double> normalizarProduccion(){
		
		ArrayList<Double> produccionNormalizada = new ArrayList<Double>();
		
		for (int i = 0; i < proporcionesProduccion.length; i++) {
			produccionNormalizada.add((double) (piezasTerminadas[i] / proporcionesProduccion[i]));
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
