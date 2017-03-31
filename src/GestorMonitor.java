import java.util.concurrent.Semaphore;

public class GestorMonitor {
	
	final Semaphore entrada_monitor = new Semaphore(1);
	private boolean k;
	private RedPetri red = new RedPetri();
	
	public void dispararTransicion(){
		
	}
}
