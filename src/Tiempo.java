import java.util.ArrayList;

public class Tiempo {
	
	ArrayList<Long> transicionesConTiempo = new ArrayList<Long>();
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
	
	public int getTimeSleep(){
		return 0;
	}
	
	public boolean alguienEsperando(){
		return false;
	}
	
	public void resetEsperando(){
		
	}
	
	public void setNuevoTimeStamp(){
		
	}
}
