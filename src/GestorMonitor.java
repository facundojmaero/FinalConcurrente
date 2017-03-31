import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorMonitor {
	
	final Semaphore entrada_monitor = new Semaphore(1);
	private boolean k;
	private RedPetri red = new RedPetri();
	private Semaphore colas[];
	private List<Integer> sensibilizadas = new ArrayList<Integer>();
	
	public GestorMonitor(int cantidadTransiciones){
		for (int i=0;i<cantidadTransiciones;i++){
			colas[i] = new Semaphore(1);
		}
	}
	
	public void dispararTransicion(int transicion){
		try {
			entrada_monitor.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		k = true;
		while(k == true){
			k = red.disparar();
			
			if (k==true){
				sensibilizadas = red.get_sensibilizadas();
				int quienes_estan_en_la_cola[]; //Como veo quienes estan en la cola?
				int listasParaDisparar[] = null; //Aca hay que hacer el and de sensibilizidas y listas para disparar
				List<int[]> list = Arrays.asList(listasParaDisparar);
				if (list.contains(1)){
					//Le pregunto a la politica cuales disparar y libero de la cola correspondiente
					/*falta*
					 * --------
					 */
					//Salgo del monitor
					entrada_monitor.release();
					return;
				}
				else{
					k = false;
					//Me salgo del while
					return;
				}
				
			}
			
			else{
				entrada_monitor.release();
				//Me voy a una de las colas a dormir
				try {
					colas[transicion].acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}
	}
}
