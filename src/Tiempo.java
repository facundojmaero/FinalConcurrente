import java.util.ArrayList;
import java.util.List;

public class Tiempo {

	List<Long> transicionesConTiempo;
	int esperando[];
	long alfa[];
	long beta[];
	int[] tiempos;

	public Tiempo(List<Integer> sensibilizadas_iniciales, int[] tiempos) {
		
		transicionesConTiempo = new ArrayList<Long>();
		alfa = new long[sensibilizadas_iniciales.size()];
		beta = new long[sensibilizadas_iniciales.size()];
		
		for (int i = 0; i < tiempos.length; i++) {
			transicionesConTiempo.add((long) 0);
			alfa[i] = tiempos[i];
			beta[i] = 100000;
		}
		
		esperando = new int[sensibilizadas_iniciales.size()];
		setNuevoTimeStamp(sensibilizadas_iniciales);
	}

	public int testVentanaTiempo(int transicion) {
		
		long current_time = System.currentTimeMillis();
		long tiempo = current_time - transicionesConTiempo.get(transicion);
		
		if (tiempo < alfa[transicion]) 		{ return -1; } 
		else if (tiempo > beta[transicion]) { return -2; } 
		else 								{ return 1; }
	}

	public long getTimeSleep(int transicion) {
		long sleepTime = (alfa[transicion] - (System.currentTimeMillis() - transicionesConTiempo.get(transicion)));
		
		if (sleepTime > 0) 	{ return sleepTime; } 
		else 				{ return 0; }
	}

	public boolean alguienEsperando(int transicion) {
		// Si hay alguien esperando
		
		if (esperando[transicion] != 0) {
			return true;
		} else {
			return false;
		}
	}

	public void resetEsperando(int transicion) { esperando[transicion] = 0; }
	public void setEsperando(int transicion) { esperando[transicion] = 1; }

	public void setNuevoTimeStamp(List<Integer> newSensibilizadas) {
		
		for (int i = 0; i < newSensibilizadas.size(); i++) {
			// Si es una nueva transicion sensibilizada
			
			if (newSensibilizadas.get(i) == 1 && alguienEsperando(i) == false) {
				transicionesConTiempo.set(i, (System.currentTimeMillis()));
			}
		}
	}
}
