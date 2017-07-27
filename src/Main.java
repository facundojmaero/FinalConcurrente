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
	
	private static int[][] convertIntegersMatriz(ArrayList<ArrayList<Integer>> matriz){
		
		int[][] I = new int[matriz.size()][matriz.get(0).size()];
		for (int i = 0; i < matriz.size(); i++) {
			I[i] = convertIntegersVector(matriz.get(i));
		}
		return I;
		
	}

	private static int[] convertIntegersVector(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

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
