import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		String fileMatrizI = "data/red_tp.txt";
		String fileMarcado = "data/marcado_tp.txt";
		String fileInvariantes = "data/invariantes_tp.txt";
		String fileTransiciones = "data/transicionesHilos_tp.txt";
		String fileTiempos = "data/tiempos_tp.txt";
		String fileTipoPieza = "data/hiloPieza_tp.txt";
		String fileTransicionesPolitica = "data/transicionesPorPieza_tp.txt";

		
		//Leo los archivos
		int[][] I = convertIntegersMatriz(readMatrix(fileMatrizI));
		int[] M = readVector(fileMarcado);
		int[][] invariantes = convertIntegersMatriz(readMatrix(fileInvariantes));
		int[] resultadoInvariantes = generarEcuacionesInvariantes(invariantes, M);
		int[][] transicionesHilos = convertIntegersMatriz(readMatrix(fileTransiciones));
		int[][] hiloPieza = convertIntegersMatriz(readMatrix(fileTipoPieza));
		ArrayList<ArrayList<Integer>> matrizPrioridades = readMatrix(fileTransicionesPolitica);
		
		int[] tiempos;
		try {
			tiempos = readVector(fileTiempos);
		} catch (Exception e) {
			tiempos = null;
		}
		
		int nroHilos = transicionesHilos.length;
		int piezasDistintas = hiloPieza[0][0];
		
		//Creacion de objetos necesarios
		Hilo[] threadArray = new Hilo[nroHilos];
		GestorMonitor monitor = new GestorMonitor(I, M, invariantes, resultadoInvariantes, tiempos, hiloPieza[0][0]);
		GestorPiezas gestorPiezas = new GestorPiezas(piezasDistintas);
		Politicas politicas = new Politicas(piezasDistintas);
		
		politicas.setMatrizTransiciones(matrizPrioridades);
		
		monitor.setPoliticas(politicas);
		gestorPiezas.setPolitica(politicas);
		
		//Creacion e inicializacion de hilos
		for (int i = 0; i < nroHilos; i++) {
			MyLinkedList<Integer> listaTransiciones = new MyLinkedList<Integer>();
			for (int j = 0; j < transicionesHilos[i].length; j++) {
				listaTransiciones.add(transicionesHilos[i][j]);
			}
			threadArray[i] = new Hilo(listaTransiciones, monitor, gestorPiezas);
			threadArray[i].setTipoPieza(hiloPieza[1][i]);
			
			Thread thread = new Thread(threadArray[i]);
			thread.start();
		}

	}

	/**
	 * Calcula el resultado de los invariantes de la red
	 * A partir de las ecuaciones de P-Invariantes de la red, y
	 * su marcado, calcula el resultado de las ecuaciones de invariantes.
	 *
	 * @param  	invariantes		Conjunto de transiciones que conforman los P-Invariantes de la red
	 * @param	marcado			Marcado actual de la red
	 * @return      			Resultado de cada una de las ecuaciones de invariantes
	 */
	private static int[] generarEcuacionesInvariantes(int[][] invariantes, int[] marcado) {

		int[] resultadoPlazas = new int[invariantes.length];

		for (int i = 0; i < invariantes.length; i++) {
			int suma = 0;
			for (int j = 0; j < invariantes[0].length; j++) {
				if (invariantes[i][j] != 0)
					suma += marcado[j];
			}
			resultadoPlazas[i] = suma;
		}

		return resultadoPlazas;
	}

	/**
	 * Lee un archivo de texto en un vector de Integers
	 * A partir de la ruta de un documento, lee una fila del mismo
	 * y guarda los datos en un ArrayList de Integers
	 *
	 * @param  cadena	Direccion del archivo a leer en el filesystem
	 * @return      	Vector de Integer leido
	 */
	private static ArrayList<Integer> readLine(String cadena) {

		Scanner colReader = null;
		colReader = new Scanner(cadena);
		ArrayList<Integer> line = new ArrayList<Integer>();
		while (colReader.hasNextInt()) {
			line.add(colReader.nextInt());
		}
		colReader.close();
		return line;
	}

	/**
	 * Lee un archivo de texto en una matriz 2d de Integers
	 * A partir de la ruta de un documento, lee fila a fila del mismo
	 * y guarda los datos en un ArrayList en dos dimensiones de Integers
	 *
	 * @param  file		Direccion del archivo a leer en el filesystem
	 * @return      	Matriz de Integer leida
	 * @see         	readLine(String cadena)
	 */
	private static ArrayList<ArrayList<Integer>> readMatrix(String file) {

		ArrayList<ArrayList<Integer>> matriz = new ArrayList<ArrayList<Integer>>();
		Scanner input = null;
		try {
			input = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (input.hasNextLine()) {
			ArrayList<Integer> line = readLine(input.nextLine());
			matriz.add(line);
		}
		input.close();
		
		return matriz;
	}
	
	
	/**
	 * Transforma un ArrayList de Integers de dos dimensiones en una matriz de ints 
	 * Dado un ArrayList de ArrayLists de Integers, lo convierte en una
	 * matriz de dos dimensiones de enteros (int)
	 *
	 * @param  matriz	ArrayList de ArrayList de Integers a convertir
	 * @return      	Matriz de enteros (int)
	 * @see         	convertIntegersVector(List<Integer> integers)
	 */
	private static int[][] convertIntegersMatriz(ArrayList<ArrayList<Integer>> matriz){
		
		int[][] I = new int[matriz.size()][matriz.get(0).size()];
		for (int i = 0; i < matriz.size(); i++) {
			I[i] = convertIntegersVector(matriz.get(i));
		}
		return I;
	}

	
	/**
	 * Convierte una lista de Integers en un arreglo de enteros (int) 
	 *
	 * @param  integers Lista de Integers a convertir
	 * @return      	Arreglo unidimensional de enteros (int)
	 */
	private static int[] convertIntegersVector(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}
	
	
	/**
	 * Lee un vector de números de un archivo de texto plano.
	 * Dada una ruta en el sistema de archivos, lee la primer linea del documento
	 * y guarda los resultados en un arreglo de enteros.
	 *
	 * @param  file Dirección del archivo a leer
	 * @return      Arreglo de enteros con el contenido del archivo
	 */
	private static int[] readVector(String file) {

		Scanner input = null;
		try {
			input = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			System.out.println("Archivo no encontrado " + e + "\n");
		}

		ArrayList<Integer> line = readLine(input.nextLine());
		input.close();

		// Convierto de arrayList a array

		int[] M = new int[line.size()];
		M = convertIntegersVector(line);
		return M;
	}
}
