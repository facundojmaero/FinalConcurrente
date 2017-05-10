import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorMonitor {
	
	final Semaphore entrada_monitor = new Semaphore(1);
	private int k;
	private RedPetri red;
	private Semaphore colas[];
	private List<Integer> sensibilizadas = new ArrayList<Integer>();
	private List<Integer> quienesEnCola = new ArrayList<Integer>();
	
	public GestorMonitor(int cantidadTransiciones, int I[][], int[] M){
		
		red = new RedPetri(cantidadTransiciones, I, M, entrada_monitor);
		colas = new Semaphore[cantidadTransiciones];
		for (int i=0;i<cantidadTransiciones;i++){
			this.colas[i] = new Semaphore(0);
		}
		for (int i = 0; i < cantidadTransiciones; i++) {
			this.quienesEnCola.add(0);
			this.sensibilizadas.add(0);
		}
	}
	

	
	public int dispararTransicion(int transicion){
		try {
			entrada_monitor.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		k = 1;
		while(k == 1){
			k = red.disparar(transicion);
			System.out.println(Thread.currentThread().getName() + " Intentando disparar transicion " + transicion);
			//Si no se cumple los invariantes
			try {
				red.revisarInvariantes();
			} catch (Exception e) {
				System.out.println("No se cumplen los invariantes. Finalizando programa");
				e.printStackTrace();
				System.exit(1);
			}
			
			if (k == 1){
				//Dispare una transicion correctamente
				System.out.println(Thread.currentThread().getName() + " Dispare transicion " + transicion);
//				verMarcado();
				sensibilizadas = red.get_sensibilizadas();
				//Actualizo quienes estan en la cola
				this.quienesEnCola(); 
				List<Integer> listasParaDisparar = this.andVectores(sensibilizadas, quienesEnCola); //Aca hay que hacer el and de sensibilizidas y listas para disparar
				if (listasParaDisparar.contains(1)){
					//A falta de politica despierto a los hilos de la primera transicion disponible
					int indiceDespertar = listasParaDisparar.indexOf(1);
					//Despierto a un hilo que esta esperando por esa transicion
					colas[indiceDespertar].release();
					return 0;
				}
				else{
					k = 0;
					//Me salgo del while
					entrada_monitor.release();
					return 0;
				}
			}
			
			else if(k == 0){
				System.out.println(Thread.currentThread().getName() + " transicion no sensibilizada" );
				//No dispare por no estar sensibilizada
				entrada_monitor.release();
				try {
					colas[transicion].acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {//if(k == -1){
				System.out.println(Thread.currentThread().getName() + " duermo, estoy antes del alfa" );
				//No dispare por no estar en ventana de tiempo
				//Igual que el caso anterior pero no espero en la cola sino que me voy
				return 1;
			}
		}
		entrada_monitor.release();
		return 0;
	}
	
	private void quienesEnCola(){
		for (int i=0;i<colas.length;i++){
			//Lleno quienesEnCola con "1" donde hay hilos esperando y "0" donde no los hay
			int cantidad_hilos_cola = colas[i].getQueueLength();
			if (cantidad_hilos_cola > 0){
				quienesEnCola.set(i, 1);
			}
			else{
				quienesEnCola.set(i, 0);
			}
		}
		return;
	}
	
	private List<Integer> andVectores (List<Integer> sensibilizadas, List<Integer> enCola){
		List<Integer> listas = new ArrayList<Integer>(sensibilizadas.size());
		
		for (int i = 0; i < sensibilizadas.size(); i++) {
			listas.add(sensibilizadas.get(i) & enCola.get(i));
		}
		
		return listas;
	}
	
	public void verMarcado(){
		System.out.print(Arrays.toString(red.getMarcado()));
		return;
	}
}
