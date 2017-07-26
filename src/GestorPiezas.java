import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorPiezas {
	
	private int[] piezasTerminadas;
	private int[] proporcionesProduccion;
	private int[] prioridades;
	private Politicas politica;
	private MyEntradaMonitor entradaMonitor;
	int indiceMenorProporcion;
	
	PrintWriter writer;

	public GestorPiezas(int numeroPiezas){
		
		piezasTerminadas = new int[numeroPiezas];
		
		//por defecto se produce el mismo numero de piezas de cada tipo
		//primeramente las prioridades son 0 1 2
		proporcionesProduccion = new int[numeroPiezas];
		prioridades = new int[numeroPiezas];
		
		for (int i = 0; i < numeroPiezas; i++) {
			proporcionesProduccion[i] = 1;
			prioridades[i] = i;
		} 
//		proporcionesProduccion[0] = 2;
//		proporcionesProduccion[1] = 1;
//		proporcionesProduccion[2] = 1;
		
//		proporcionesProduccion[0] = 2;
//		proporcionesProduccion[1] = 3;
//		proporcionesProduccion[2] = 1;
		
		indiceMenorProporcion = getMenorIndiceProporcion(proporcionesProduccion);
		
		try{
		    writer = new PrintWriter("log.txt", "UTF-8");
		} catch (IOException e) {
		   // do something
		}
	}
	
	public synchronized void contarPieza(int tipoPieza){
		
		piezasTerminadas[tipoPieza]++;
		
		ArrayList<Double> produccionNormalizada = normalizarProduccion();
		double promedio = calcularPromedio(produccionNormalizada);
		produccionNormalizada = dividirPorPromedio(produccionNormalizada, promedio);
		
		printArray(produccionNormalizada, piezasTerminadas, indiceMenorProporcion);
		
		prioridades = ordenarPrioridades(produccionNormalizada);
		
		if(politica != null){
			politica.setPrioridades(prioridades);
			entradaMonitor.setPrioridades(prioridades);
		}
		
	}
	
	private int getMenorIndiceProporcion(int[] proporcionesProduccion2) {
		
		int indice = 0;
		
		for (int i = 0; i < proporcionesProduccion2.length; i++) {
			if(proporcionesProduccion2[i] <= proporcionesProduccion2[indice]){
				indice = i;
			}
		}
		
		return indice;
	}

	private int[] ordenarPrioridades(ArrayList<Double> array){
		int[] prioridades = new int[array.size()];
		
		ArrayList<Double> arrayCopia = new ArrayList<Double>(array);
		arrayCopia.sort(null);
		
		for (int i = 0; i < prioridades.length; i++) {
			prioridades[i] = array.indexOf(arrayCopia.get(i));
			array.set(prioridades[i], (double) -1);
		}
		
		return prioridades;
	}
	
	private void printArray(ArrayList<Double> array, int[] array2, int indexMenorProporcion){
		
		for (int i = 0; i < array.size(); i++) {
			System.out.printf("%.2f  ", array.get(i));
		}
		
		System.out.print("	");
		
		for (int i = 0; i < array2.length; i++) {
			System.out.printf("%d  ", array2[i]);
		}
		
		System.out.print("		");
		
		for (int i = 0; i < array2.length; i++) {
			System.out.printf("%.2f  ", (double)array2[i] / array2[indexMenorProporcion]);
		}
		
		System.out.print("	");
		
		for (int i = 0; i < array2.length; i++) {
			System.out.printf("%.2f  ", (double)array2[i] / array2[indexMenorProporcion]);
			writer.printf("%.2f  ", (double)array2[i] / array2[indexMenorProporcion]);
		}
		
		System.out.println();
		writer.println();
	}
	
	public void verProduccion(){
		
		for (int i = 0; i < piezasTerminadas.length; i++) {
			System.out.println("Pieza " + i + ": " + piezasTerminadas[i]);
		}
		
		System.out.printf("Politica: ");
		
		for (int i = 0; i < proporcionesProduccion.length; i++) {
			System.out.printf("%d ", proporcionesProduccion[i]);
		} 
		
		writer.close();
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
