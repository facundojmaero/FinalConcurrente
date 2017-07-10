import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		String fileMatrizI = "red_tp.txt";
		String fileMarcado = "marcado_tp.txt";

		int[][] I = readMatrix(fileMatrizI);
		int[] M = readMarcado(fileMarcado);
		int[][] invariantes = readMatrix("invariantes_tp.txt");

		int[] resultadoInvariantes = generarEcuacionesInvariantes(invariantes, M);

		List<Integer> transiciones_hilo_1 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_2 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_3 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_4 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_5 = new MyLinkedList<Integer>();

//		Collections.addAll(transiciones_hilo_1, 0, 1);
//		Collections.addAll(transiciones_hilo_2, 3, 2);

		 Collections.addAll(transiciones_hilo_1, 1,2,3,4);
		 Collections.addAll(transiciones_hilo_2, 5,6,7,8,13);
		 Collections.addAll(transiciones_hilo_3, 9,10,11,12,13);
		 Collections.addAll(transiciones_hilo_4, 19,18,17,16,15,14);
		 Collections.addAll(transiciones_hilo_5, 0);

		GestorMonitor monitor = new GestorMonitor(I, M, invariantes, resultadoInvariantes);

		Hilo hilo1 = new Hilo(transiciones_hilo_1, monitor);
		Hilo hilo2 = new Hilo(transiciones_hilo_2, monitor);
		Hilo hilo3 = new Hilo(transiciones_hilo_3,monitor);
		Hilo hilo4 = new Hilo(transiciones_hilo_4,monitor);
		Hilo hilo5 = new Hilo(transiciones_hilo_5,monitor);
		Thread thread1 = new Thread(hilo1);
		Thread thread2 = new Thread(hilo2);
		Thread thread3 = new Thread(hilo3);
		Thread thread4 = new Thread(hilo4);
		Thread thread5 = new Thread(hilo5);

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
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

	private static int[] readMarcado(String file) {

		Scanner input = null;
		try {
			input = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ArrayList<Integer> line = readLine(input.nextLine());
		input.close();

		// Convierto de arrayList a array

		int[] M = new int[line.size()];
		M = convertIntegers(line);
		return M;
	}
}
