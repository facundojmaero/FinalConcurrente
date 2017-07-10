import java.util.ArrayList;
import java.util.List;

public class Tiempo {

	List<Long> transicionesConTiempo;
	int esperando[];
	long alfa[];// = {250,0,0,0};
	long beta[];// = {1000000000,1000000000,1000000000,1000000000};

	public Tiempo(List<Integer> sensibilizadas_iniciales) {
		
		transicionesConTiempo = new ArrayList<Long>();
		alfa = new long[sensibilizadas_iniciales.size()];
		beta = new long[sensibilizadas_iniciales.size()];
		
		for (int i = 0; i < sensibilizadas_iniciales.size(); i++) {
			transicionesConTiempo.add((long) 0);
			alfa[i] = 50;
			beta[i] = 10000;
		}
		
		setNuevoTimeStamp(sensibilizadas_iniciales);
		esperando = new int[sensibilizadas_iniciales.size()];
	}

	public int testVentanaTiempo(int transicion) {
		
		String t = Thread.currentThread().getName();
		
		long current_time = System.currentTimeMillis();
		long tiempo = current_time - transicionesConTiempo.get(transicion);
		
		System.out.println(t + " transicion "
		+ transicion + " sensibilizada por " + tiempo + " ms");
		
		if (tiempo < alfa[transicion]) {
			return -1;
		} else if (tiempo > beta[transicion]) {
			return -2;
		} else
			return 1;
	}

	public long getTimeSleep(int transicion) {
		long sleepTime = (alfa[transicion] - (System.currentTimeMillis() - transicionesConTiempo.get(transicion)));
		if (sleepTime > 0) {
			return sleepTime;
		} else {
			return 0;
		}
	}

	public boolean alguienEsperando(int transicion) {
		// Si hay alguien esperando
		if (esperando[transicion] != 0) {
			return true;
		} else {
			return false;
		}
	}

	public void resetEsperando(int transicion) {
		esperando[transicion] = 0;
		return;
	}

	public void setEsperando(int transicion) {
		esperando[transicion] = 1;
		return;
	}

	public void setNuevoTimeStamp(List<Integer> newSensibilizadas) {
		
		for (int i = 0; i < newSensibilizadas.size(); i++) {
			// Si es una nueva transicion sensibilizada
			if (newSensibilizadas.get(i) == 1) {
				transicionesConTiempo.set(i, (System.currentTimeMillis()));
			}
		}
	}
}
