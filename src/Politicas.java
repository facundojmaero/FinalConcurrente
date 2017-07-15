import java.util.List;

public class Politicas {
	
	int[] prioridades;
	
	public Politicas(){
		
	}
	
	public int cual(List<Integer> transiciones){
		return transiciones.indexOf(1);
	}
	
	public void setPrioridades(int[] newPrioridades) { prioridades = newPrioridades; }
	public int[] getPrioridades() {return prioridades; }
}
