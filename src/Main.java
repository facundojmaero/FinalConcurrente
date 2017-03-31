import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.media.jai.opimage.FormatCRIF;

public class Main {

	public static void main(String[] args) {
		
		final int NUMTRAN = 2;
		final int NUMPLAZAS = 3;
		List<Integer> transiciones = new ArrayList<Integer>();
		transiciones.add(1);
		transiciones.add(0);
		
		GestorMonitor monitor = new GestorMonitor(NUMTRAN);
		Hilo hilo = new Hilo(transiciones,monitor);
		hilo.run();


	}
}
