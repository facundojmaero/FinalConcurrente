import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {

	public static void main(String[] args) {
		
		final int NUMTRAN = 4;
		final int NUMPLAZAS = 6;
		List<Integer> transiciones_hilo_1 = new MyLinkedList<Integer>();
		List<Integer> transiciones_hilo_2 = new MyLinkedList<Integer>();
		
		transiciones_hilo_1.add(0);
		transiciones_hilo_1.add(1);
//		transiciones_hilo_1.add(-1);
//		transiciones_hilo_1.add(-1);
		
//		transiciones_hilo_2.add(-1);
//		transiciones_hilo_2.add(-1);
		transiciones_hilo_2.add(3);
		transiciones_hilo_2.add(2);
		
		GestorMonitor monitor = new GestorMonitor(NUMTRAN);
		Hilo hilo1 = new Hilo(transiciones_hilo_1,monitor);
		Hilo hilo2 = new Hilo(transiciones_hilo_2,monitor);
		Thread thread1 = new Thread(hilo1);
		Thread thread2 = new Thread(hilo2);
		
		thread1.start();
		thread2.start();


	}
}
