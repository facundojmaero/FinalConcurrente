import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class SystemTest {
	
	/**
	 * Comprueba que el hilo duerme correctamente en la cola
	 *  
	 * @throws Exception
	 */
	@Test
	public void testHiloDurmiendo() throws Exception {
		
		String fileMatrizI = "data/red_tp.txt";
		String fileMarcado = "data/marcado_tp.txt";
		String fileInvariantes = "data/invariantes_tp.txt";
		String fileTiempos = "data/tiempos_tp.txt";
		String fileTipoPieza = "data/hiloPieza_tp.txt";

		
		//Leo los archivos
		int[][] I = readMatrix(fileMatrizI);
		int[] M = readVector(fileMarcado);
		int[][] invariantes = readMatrix(fileInvariantes);
		int[] resultadoInvariantes = generarEcuacionesInvariantes(invariantes, M);
		int[][] hiloPieza = readMatrix(fileTipoPieza);
		
		int[] tiempos;
		try {
			tiempos = readVector(fileTiempos);
		} catch (Exception e) {
			tiempos = null;
		}
		
		//Creacion de objetos necesarios
		GestorMonitor monitor = new GestorMonitor(I, M, invariantes, resultadoInvariantes, tiempos, hiloPieza[0][0]);
		GestorPiezas gestorPiezas = new GestorPiezas(3);
		Politicas politicas = new Politicas(3);
		
		monitor.setPoliticas(politicas);
		gestorPiezas.setPolitica(politicas);
		
		MyLinkedList<Integer> listaTransiciones = new MyLinkedList<Integer>();
		listaTransiciones.add(5);
		listaTransiciones.add(6);
		listaTransiciones.add(7);
		listaTransiciones.add(8);
		listaTransiciones.add(13);

		Hilo hilo1 = new Hilo(listaTransiciones, monitor, gestorPiezas);
		
		Thread thread = new Thread(hilo1);
		thread.start();
		
		Thread.currentThread().sleep(100);
		
		assertTrue(monitor.quienesEnCola.get(5) == 1);
		
	}
	
	/**
	 * Comprueba el correcto funcionamiento del disparo de una transicion.
	 * Dispara varias transiciones en la red del practico, y comprueba que 
	 * el valor de retorno concuerda con el esperado, sea un valor exitoso
	 * (se pudo disparar), o no (no estaba sensibilizada).
	 *  
	 * @throws Exception
	 */
	@Test
	public void testPoliticas() throws Exception {
		
		String fileTransicionesPolitica = "data/transicionesPorPieza_tp.txt";
		
		ArrayList<ArrayList<Integer>> matriz = new ArrayList<ArrayList<Integer>>();
		Scanner input = null;
		try {
			input = new Scanner(new File(fileTransicionesPolitica));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (input.hasNextLine()) {
			ArrayList<Integer> line = readLine(input.nextLine());
			matriz.add(line);
		}
		input.close();
		
		Politicas politica = new Politicas(3);
		politica.setMatrizTransiciones(matriz);
		
		int[] prioridades = new int[3];
		List<Integer> transiciones = new ArrayList<Integer>();
		
		for (int i = 0; i < 20; i++) {
			transiciones.add(0);
		}
		
		transiciones.set(1, 1);
		transiciones.set(0, 1);
		transiciones.set(19, 1);
		
		prioridades[0] = 1;
		prioridades[1] = 0;
		prioridades[2] = 2;
		
		politica.setPrioridades(prioridades);
		assertEquals(0,politica.cual(transiciones));
		
		for (int i = 0; i < 20; i++) {
			transiciones.set(i,0);
		}
		
		transiciones.set(1, 1);
		transiciones.set(0, 1);
		transiciones.set(19, 1);
		
		prioridades[0] = 2;
		prioridades[1] = 1;
		prioridades[2] = 0;
		
		politica.setPrioridades(prioridades);
		assertEquals(19,politica.cual(transiciones));
		
		for (int i = 0; i < 20; i++) {
			transiciones.set(i,0);
		}
		
		transiciones.set(1, 1);
		transiciones.set(0, 1);
		transiciones.set(19, 1);
		
		prioridades[0] = 0;
		prioridades[1] = 2;
		prioridades[2] = 1;
		
		politica.setPrioridades(prioridades);
		assertEquals(1,politica.cual(transiciones));
		
	}
	
	
	
	/**
	 * Comprueba el correcto funcionamiento del disparo de una transicion.
	 * Dispara varias transiciones en la red del practico, y comprueba que 
	 * el valor de retorno concuerda con el esperado, sea un valor exitoso
	 * (se pudo disparar), o no (no estaba sensibilizada).
	 *  
	 * @throws Exception
	 */
	@Test
	public void testDisparo() throws Exception {
		RedPetri red = crearNuevaRedTP();
		
		assertEquals(1,red.disparar(0));
		assertEquals(-3,red.disparar(0));
		
	}
	
	/**
	 * Comprueba el correcto funcionamiento del disparo de una transicion con tiempo.
	 * Dispara varias transiciones en la red del practico, y comprueba que 
	 * el valor de retorno concuerda con el esperado, sea un valor exitoso
	 * (se pudo disparar), o no (no estaba sensibilizada). Se realiza sobre una red con tiempo
	 *  
	 * @throws Exception
	 */
	@Test
	public void testDisparoTiempo() throws Exception {
		RedPetri red = crearNuevaRedTPTiempo();
		
		assertEquals(1,red.disparar(0));
		assertEquals(-3,red.disparar(0));
		assertEquals(1, red.disparar(5));
		assertEquals(-1, red.disparar(6));
		
	}

	/**
	 * Testea el marcado de la red del practico.
	 * Dispara un conjunto de transiciones y revisa los invariantes
	 * en cada disparo. 
	 * @throws Exception
	 */
	@Test
	public void testMarcado() throws Exception {
		RedPetri red = crearNuevaRedTP();
		
		int[] transiciones = {0,19,5,0,6,5,7,0,1,18,8,2,17,13,16,3,4,6,5};
		
		for (int i = 0; i < transiciones.length; i++) {
			red.disparar(transiciones[i]);
			assertTrue("Invariantes", red.revisarInvariantes());
			
		}
	}
	
	/**
	 * Testea el marcado de la red del practico.
	 * Dispara un conjunto de transiciones y revisa que no se ingrese en estados invalidos. 
	 * @throws Exception
	 */
	@Test
	public void testEstadosInvalidos() throws Exception {
		RedPetri red = crearNuevaRedTP();
		
		int[] transiciones = {0,19,5,0,6,5,7,0,1,18,8,2,17,13,16,3,4,6,5,7};
		
		for (int i = 0; i < transiciones.length; i++) {
			red.disparar(transiciones[i]);
		}
			int[] marcado = red.getMarcado();

			assertTrue("Plazas 2 y 11", marcado[2] + marcado[11] < 2);
			assertTrue("Plazas 1 y 10", marcado[1] + marcado[10] < 2);
			assertTrue("Plazas 15 y 25", marcado[15] + marcado[25] < 2);
			assertTrue("Plazas 10 y 15", marcado[10] + marcado[15] < 2);
			assertTrue("Plazas 2, 10, 14, 23", marcado[2] + marcado[10] + marcado[14] + marcado[23] == 1);
			
	}
	
	/**
	 * Testea las transiciones sensibilizadas en la red luego de varios disparos.
	 * Dispara un conjunto de transiciones, revisa el numero de posibles disparos, 
	 * y que transiciones son.
	 *
	 */
	@Test
	public void testSensibilizadas1() {
		RedPetri red = crearNuevaRedTP();
		
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
	
	
	/**
	 * Testea las transiciones sensibilizadas en la red luego de varios disparos.
	 * Dispara un conjunto de transiciones, revisa el numero de posibles disparos, 
	 * y que transiciones son.
	 *
	 */
	@Test
	public void testSensibilizadas2() {
		RedPetri red = crearNuevaRedTP();
		
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
	
	/**
	 * Comprueba el funcionamiento de la linked list circular usada por los hilos
	 * Crea una lista circular con 3 elementos, hace avanzar el indice actual y comprueba
	 * que se cumpla el recorrido circular.
	 */
	@Test
	public void testMyLinkedList() {
		
		MyLinkedList<Integer> list = new MyLinkedList<Integer>();
		
		list.add(4);
		list.add(3);
		list.add(2);
		
		assertEquals(4, list.getActual().intValue());
		list.avanzar();
		list.avanzar();
		list.avanzar();
		assertEquals(4, list.getActual().intValue());
	}
	
	
	/**
	 * Crea una nueva Red de Petri, con el marcado inicial y los invariantes usados en el proyecto.
	 * Inicializa una nueva red, para ser utilizada en tests de comprobacion de plazas 
	 * y transiciones.
	 * 
	 * @return red La red inicializada
	 * 
	 * @see testMarcado
	 * @see testSensibilizadas1()
	 * @see testSensibilizadas2()
	 */
	public RedPetri crearNuevaRedTP(){
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
	
	/**
	 * Crea una nueva Red de Petri con tiempo, con el marcado inicial y los invariantes usados en el proyecto.
	 * Inicializa una nueva red, para ser utilizada en tests de comprobacion de plazas 
	 * y transiciones.
	 * 
	 * @return red La red con tiempo inicializada
	 * 
	 * @see testMarcado
	 * @see testSensibilizadas1()
	 * @see testSensibilizadas2()
	 */
	public RedPetri crearNuevaRedTPTiempo(){
		String fileMatrizI = "data/red_tp.txt";
		String fileMarcado = "data/marcado_tp.txt";
		String fileInvariantes = "data/invariantes_tp.txt";
		String fileTiempos = "data/tiempos_tp.txt";

		int[][] I = readMatrix(fileMatrizI);
		int[] M = readVector(fileMarcado);
		int[][] invariantes = readMatrix(fileInvariantes);
		int[] resultadoInvariantes = generarEcuacionesInvariantes(invariantes, M);
		int[] tiempos = readVector(fileTiempos);

		
		RedPetri red = new RedPetri(I[0].length, I, M, invariantes, resultadoInvariantes, tiempos);
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
