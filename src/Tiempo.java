import java.util.ArrayList;
import java.util.List;

public class Tiempo {
	
	List<Long> transicionesConTiempo = new ArrayList<Long>();
	int esperando[];
	long alfa[];
	long beta[];
	
	public Tiempo(){
		
	}
	
	public boolean testVentanaTiempo(int transicion){
		long current_time = System.currentTimeMillis();
		long tiempo = current_time - transicionesConTiempo.get(transicion);
		if (tiempo > alfa[transicion] && tiempo < beta[transicion]){
			return true;
		}
		else{
			return false;
		}
	}
	
	public long getTimeSleep(int transicion){
		long sleep_time = transicionesConTiempo.get(transicion) + alfa[transicion];
		sleep_time -= System.currentTimeMillis();
		return sleep_time;
	}
	
	public boolean alguienEsperando(int transicion){
		//Si hay alguien esperando
		if (esperando[transicion] != 0){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public void resetEsperando(int transicion){
		esperando[transicion] = 0;
		return;
	}
	
	public void setNuevoTimeStamp(List<Integer> newSensibilizadas){
		for(int i=0;i<newSensibilizadas.size();i++){
			//Si es una nueva transicion sensibilizada
			if (newSensibilizadas.get(i) == 1){
				transicionesConTiempo.set(i,System.currentTimeMillis());
			}
		}
	}
}
