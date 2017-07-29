import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorPiezas {
	
	private int[] piezasTerminadas;
	private int[] proporcionesProduccion;
	private int[] prioridades;
	private Politicas politica;
	int indiceReferencia;
	
	private PrintWriter writer;
	private int[] piezasIniciales;
	
	private boolean test = false;

	public GestorPiezas(int numeroPiezas){
		
		piezasTerminadas = new int[numeroPiezas];
//		piezasTerminadas[0] = 200;
		piezasIniciales = piezasTerminadas.clone();
		
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
		
		proporcionesProduccion[0] = 2;
		proporcionesProduccion[1] = 3;
		proporcionesProduccion[2] = 1;
		
		indiceReferencia = getMenorIndiceProporcion(proporcionesProduccion);
		
		try{
		    writer = new PrintWriter("log.txt", "UTF-8");
		} catch (IOException e) {}
		
	}
	
	/**
	 * Contabiliza la fabricacion de una pieza.
	 * Cuando un hilo finaliza la produccion de una pieza, se cuenta la misma,
	 * se recalculan los indices de produccion, muestran resultados por consola, 
	 * y reordenan las prioridades.
	 * Se avisa a la politica del nuevo orden de importancia para la produccion.
	 * Es un metodo synchronized, para evitar corrupcion de los datos.
	 *
	 * @param  tipoPieza	Tipo de pieza producida
	 */
	public synchronized void contarPieza(int tipoPieza){
		
		piezasTerminadas[tipoPieza]++;
		
		ArrayList<Double> produccionNormalizada = calcularIndices();
		
		printArray(produccionNormalizada, piezasTerminadas, indiceReferencia);
		
		prioridades = ordenarPrioridades(produccionNormalizada);
		
		if(politica != null){ 
			politica.setPrioridades(prioridades);
		}
		
		if(test)
			testPolitica(proporcionesProduccion, piezasTerminadas, indiceReferencia);
		
		int total = 0;
		for (int i = 0; i < piezasTerminadas.length; i++) {
			total += piezasTerminadas[i];
		}
		
		if(total == 2000) {
			verProduccion();
		}
		
	}
	
	/**
	 * Toma la produccion actual de las piezas y calcula que tan lejos o cerca estan de la proporcion deseada
	 *
	 *@return Vector de indices que representan la produccion en el momento actual
	 */
	private ArrayList<Double> calcularIndices() {
		
		ArrayList<Double> produccionNormalizada = normalizarProduccion();
		double promedio = calcularPromedio(produccionNormalizada);
		produccionNormalizada = dividirPorPromedio(produccionNormalizada, promedio);
		
		return produccionNormalizada;
	}

	private void testPolitica(int[] proporcionesProduccion, int[] piezasTerminadas, int indiceDivision) {
		
		int fin = 0;
		
		for (int i = 0; i < proporcionesProduccion.length; i++) {
			
			if( piezasTerminadas[indiceDivision] == 0)
				continue;
			
			if((double)piezasTerminadas[i] / piezasTerminadas[indiceDivision] == proporcionesProduccion[i]){
				fin++;
			}
		}
		
		if(fin == proporcionesProduccion.length){
			System.out.println("Test exitoso");
			writer.println("Test exitoso");
			System.out.println("Piezas iniciales: ");
			writer.println("Piezas iniciales: ");
			for (int i = 0; i < piezasIniciales.length; i++) {
				System.out.println("Pieza " + i + " " + piezasIniciales[i]);
				writer.println("Pieza " + i + " " + piezasIniciales[i]);
			}
			System.out.println("\nPiezas al finalizar: ");
			writer.println("\nPiezas al finalizar: ");
			verProduccion();
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

	/**
	 * Reordena las prioridades de produccion en funcion de los indices calculados.
	 * Dados los indices que representan la produccion de las piezas, ordena un vector
	 * dando mas importancia al tipo de pieza que mas se aleje del valor deseado.
	 *
	 * @param  array	Vector con indices de produccion calculados
	 * @return 			Vector con orden de prioridad para producir cada pieza
	 */
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
	
	/**
	 * Muestra por consola y en el log el estado actual de la produccion
	 * Muestra en consola los indices de produccion, las piezas construidas de cada tipo,
	 * y las proporciones entre ellas.
	 * Guarda en el log las proporciones de produccion.
	 *
	 * @param  array				Arreglo 1 a imprimir
 	 * @param  array2				Arreglo 2 a imprimir
	 * @param  indiceReferencia		Indice de la pieza tomada como referencia
	 */
	private void printArray(ArrayList<Double> array, int[] array2, int indiceReferencia){
		
		for (int i = 0; i < array.size(); i++) {
			System.out.printf("%.2f  ", array.get(i));
		}
		
		System.out.print("	");
		
		for (int i = 0; i < array2.length; i++) {
			System.out.printf("%d  ", array2[i]);
		}
		
		System.out.print("	");
		
		for (int i = 0; i < array2.length; i++) {
			System.out.printf("%.2f  ", (double)array2[i] / array2[indiceReferencia]);
			writer.printf("%.2f  ", (double)array2[i] / array2[indiceReferencia]);
		}
		
		System.out.print("	");
		
		for (int i = 0; i < array2.length; i++) {
			System.out.printf("%d  ", prioridades[i]);
		}
		
		System.out.println();
		writer.println();
	}
	
	/**
	 * 	Muestra la produccion de piezas por consola y en el log, y termina el programa
	 * 	Imprime en consola la cantidad de piezas producidas de cada tipo, asi como en el log
	 * 	del programa, y finaliza la ejecucion.
	 */
	public void verProduccion(){
		
		for (int i = 0; i < piezasTerminadas.length; i++) {
			System.out.println("Pieza " + i + ": " + piezasTerminadas[i]);
			writer.println("Pieza " + i + ": " + piezasTerminadas[i]);
		}
		
		System.out.printf("Politica: ");
		
		for (int i = 0; i < proporcionesProduccion.length; i++) {
			System.out.printf("%d ", proporcionesProduccion[i]);
			writer.printf("%d ", proporcionesProduccion[i]);
		} 
		
		writer.close();
		System.exit(0);
	}
	
	public int[] getProporciones() { return proporcionesProduccion; }
	public void setProporciones(int[] newPolitica) {proporcionesProduccion = newPolitica; }
	
	public Politicas getPolitica() { return politica; }
	public void setPolitica(Politicas politica) { this.politica = politica; }
	
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
