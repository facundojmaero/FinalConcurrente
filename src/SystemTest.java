import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class SystemTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMarcado() throws Exception {
		RedPetri red = crearNuevaRed();
		
		int[] transiciones = {0,1,2,19,5,0,18,17};
		
		for (int i = 0; i < transiciones.length; i++) {
			red.disparar(transiciones[i]);
		
			int[] marcado = red.getMarcado();
			
			assertTrue("Invariantes", red.revisarInvariantes());
			assertTrue("Plazas 2 y 11", marcado[2] + marcado[11] < 2);
			assertTrue("Plazas 1 y 10", marcado[1] + marcado[10] < 2);
			assertTrue("Plazas 15 y 25", marcado[15] + marcado[25] < 2);
			assertTrue("Plazas 10 y 15", marcado[10] + marcado[15] < 2);
			assertTrue("Plaza 4", marcado[4] > 0);
			assertTrue("Plaza 17", marcado[17] > 0);
			assertTrue("Plaza 27", marcado[27] > 0);
			assertTrue("Plazas 25, 26, 21, 20", marcado[25] + marcado[26] + marcado[21] + marcado[20] == 2);
			
		}
	}
	
	@Test
	public void testSensibilizadas1() throws Exception {
		RedPetri red = crearNuevaRed();
		
		int[] transiciones = {0,1,2,19,5,0,18,17};
		
		for (int i = 0; i < transiciones.length; i++) {
			red.disparar(transiciones[i]);
		}
		
		List<Integer> sensib = red.get_sensibilizadas();
		int sensibilizadas = 0;
		
		for (int i = 0; i < sensib.size(); i++) {
			if (sensib.get(i) == 1)
				sensibilizadas++;
		}
		
		assertEquals("Numero de transiciones sensibilizadas", 1, sensibilizadas);	
		assertEquals("Indice de transicion sensibilizada", 16, sensib.indexOf(1));
	}
	
	@Test
	public void testSensibilizadas2() throws Exception {
		RedPetri red = crearNuevaRed();
		
		int[] transiciones = {1,0,19,18,5,2,17,16,3,4,6,7,0,5,6};
		
		for (int i = 0; i < transiciones.length; i++) {
			red.disparar(transiciones[i]);
		}
		
		List<Integer> sensib = red.get_sensibilizadas();
		int sensibilizadas = 0;
		int[] indicesSensibilizadas = new int[3];
		
		for (int i = 0; i < sensib.size(); i++) {
			if (sensib.get(i) == 1){
				indicesSensibilizadas[sensibilizadas] = i;
				sensibilizadas++;
			}
		}
		
		assertEquals("Numero de transiciones sensibilizadas", 3, sensibilizadas);

		assertArrayEquals(new int[]{0,8,15}, indicesSensibilizadas);
	}
	
	
	
	public RedPetri crearNuevaRed(){
		String fileMatrizI = "data/red_tp.txt";
		String fileMarcado = "data/marcado_tp.txt";
		String fileInvariantes = "data/invariantes_tp.txt";

		int[][] I = readMatrix(fileMatrizI);
		int[] M = readVector(fileMarcado);
		int[][] invariantes = readMatrix(fileInvariantes);
		int[] resultadoInvariantes = generarEcuacionesInvariantes(invariantes, M);

		
		RedPetri red = new RedPetri(I[0].length, I, M, invariantes, resultadoInvariantes, null);
		return red;
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
