import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		String fileMatrizI = "red.txt";
		String fileMarcado = "marcado.txt";
		int[][] I = readMatrix(fileMatrizI);
		int[] M = readMarcado(fileMarcado);
		
		List<Integer> transiciones_hilo_1 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_2 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_3 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_4 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_5 = new MyLinkedList<Integer>();

//		Collections.addAll(transiciones_hilo_1, 0,1);
//		Collections.addAll(transiciones_hilo_2, 3,2);
		
		Collections.addAll(transiciones_hilo_1, 1,2,3,4);
		Collections.addAll(transiciones_hilo_2, 5,6,7,8,13);
		Collections.addAll(transiciones_hilo_3, 9,10,11,12,13);
		Collections.addAll(transiciones_hilo_4, 19,18,17,16,15,14);
		Collections.addAll(transiciones_hilo_5, 0);
		
		GestorMonitor monitor = new GestorMonitor(I, M);
		
		Hilo hilo1 = new Hilo(transiciones_hilo_1,monitor);
		Hilo hilo2 = new Hilo(transiciones_hilo_2,monitor);
		Hilo hilo3 = new Hilo(transiciones_hilo_3,monitor);
		Hilo hilo4 = new Hilo(transiciones_hilo_4,monitor);
		Hilo hilo5 = new Hilo(transiciones_hilo_5,monitor);
		Thread thread1 = new Thread(hilo1);
		Thread thread2 = new Thread(hilo2);
		Thread thread3 = new Thread(hilo3);
		Thread thread4 = new Thread(hilo4);
		Thread thread5 = new Thread(hilo5);
		
//		thread2.start();
//		thread1.start();
//		thread3.start();
//		thread4.start();
//		thread5.start();
	}
	
	private static int[][] readMatrix(String file){
		
		ArrayList<ArrayList<Integer>> matriz = new ArrayList<ArrayList<Integer>>();
		Scanner input = null;
		Scanner colReader = null;
		try {
			input = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(input.hasNextLine())
		{
		    colReader = new Scanner(input.nextLine());
		    colReader.next();
		    ArrayList<Integer> col = new ArrayList<Integer>();
		    while(colReader.hasNextInt())
		    {
		        col.add(colReader.nextInt());
		    }
		    matriz.add(col);
		}
		input.close();
		colReader.close();
		
		//Convierto de arrayList a array
		
		int[][] I=new int[matriz.size()][matriz.get(0).size()];
		for (int i=0;i<matriz.size();i++){
			I[i] = convertIntegers(matriz.get(i));
		}
		return  I;
	}
	
	private static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    return ret;
	}
	
	private static int[] readMarcado(String file){

		Scanner input = null;
		Scanner colReader = null;
		try {
			input = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    colReader = new Scanner(input.nextLine());
	    colReader.next();
	    ArrayList<Integer> col = new ArrayList<Integer>();
	    while(colReader.hasNextInt())
	    {
	        col.add(colReader.nextInt());
	    }
		input.close();
		colReader.close();
		
		//Convierto de arrayList a array
		
		int[] M = new int[col.size()];
		M = convertIntegers(col);
		return  M;
	}
}
