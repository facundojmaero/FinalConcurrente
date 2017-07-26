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
		String fileTiempos = "data/tiemposs_tp.txt";
		String fileTipoPieza = "data/hiloPieza_tp.txt";
		String fileTransicionesPolitica = "data/transicionesPorPieza_tp.txt";

		int[][] I = readMatrix(fileMatrizI);
		int[] M = readVector(fileMarcado);
		int[][] invariantes = readMatrix(fileInvariantes);
		int[] resultadoInvariantes = generarEcuacionesInvariantes(invariantes, M);
		int[][] transicionesHilos = readMatrix(fileTransiciones);
		int[][] hiloPieza = readMatrix(fileTipoPieza);
		
		int[] tiempos;
		try {
			tiempos = readVector(fileTiempos);
		} catch (Exception e) {
			tiempos = new int[I[0].length];
		}
		
		int nroHilos = transicionesHilos.length;
		
		Hilo[] threadArray = new Hilo[nroHilos];
		GestorMonitor monitor = new GestorMonitor(I, M, invariantes, resultadoInvariantes, tiempos, hiloPieza[0][0]);
		GestorPiezas gestorPiezas = new GestorPiezas(hiloPieza[0][0]);
		
		Politicas politicas = new Politicas(hiloPieza[0][0]);
		
		ArrayList<ArrayList<Integer>> matrizPrioridades = new ArrayList<ArrayList<Integer>>();
		Scanner input = null;
		try {
			input = new Scanner(new File(fileTransicionesPolitica));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (input.hasNextLine()) {
			ArrayList<Integer> line = readLine(input.nextLine());
			matrizPrioridades.add(line);
		}
		input.close();
		
		politicas.setMatrizTransiciones(matrizPrioridades);
		
		monitor.getSemaforoEntrada().setMatrizTransiciones(matrizPrioridades);
		gestorPiezas.setEntradaMonitor(monitor.getSemaforoEntrada());
		
		monitor.setPoliticas(politicas);
		gestorPiezas.setPolitica(politicas);
		
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

	private static int[][] readMatrix(String file) {

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

		// Convierto de arrayList a array

		int[][] I = new int[matriz.size()][matriz.get(0).size()];
		for (int i = 0; i < matriz.size(); i++) {
			I[i] = convertIntegers(matriz.get(i));
		}
		return I;
	}

	private static int[] convertIntegers(List<Integer> integers) {
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
			System.out.println("Archivo con tiempos no encontrado " + e + "\n");
		}

		ArrayList<Integer> line = readLine(input.nextLine());
		input.close();

		// Convierto de arrayList a array

		int[] M = new int[line.size()];
		M = convertIntegers(line);
		return M;
	}
}
