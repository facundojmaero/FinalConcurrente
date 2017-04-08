import java.util.Collections;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		final int NUMTRAN = 4;
		final int NUMPLAZAS = 6;
		List<Integer> transiciones_hilo_1 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_2 = new MyLinkedList<Integer>();

		Collections.addAll(transiciones_hilo_1, 0,1);
		Collections.addAll(transiciones_hilo_2, 3,2);
		
//		List<Integer> transiciones_hilo_1 = new MyLinkedList<Integer>();
//		List<Integer> transiciones_hilo_2 = new MyLinkedList<Integer>();
//		List<Integer> transiciones_hilo_3 = new MyLinkedList<Integer>();
//		List<Integer> transiciones_hilo_4 = new MyLinkedList<Integer>();
//		List<Integer> transiciones_hilo_5 = new MyLinkedList<Integer>();
//		List<Integer> transiciones_hilo_6 = new MyLinkedList<Integer>();
		
//		Collections.addAll(transiciones_hilo_1, 1,2,3,4);
//		Collections.addAll(transiciones_hilo_2, 5,6,7,8);
//		Collections.addAll(transiciones_hilo_3, 0);
//		Collections.addAll(transiciones_hilo_4, 13);
//		Collections.addAll(transiciones_hilo_5, 9,10,11,12);
//		Collections.addAll(transiciones_hilo_6, 19,18,17,16,15,14);
		
		GestorMonitor monitor = new GestorMonitor(NUMTRAN);
		Hilo hilo1 = new Hilo(transiciones_hilo_1,monitor);
		Hilo hilo2 = new Hilo(transiciones_hilo_2,monitor);
		Thread thread1 = new Thread(hilo1);
		Thread thread2 = new Thread(hilo2);
		
		thread2.start();
		thread1.start();
	}
}
