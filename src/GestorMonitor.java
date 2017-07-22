import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorMonitor {

//	final Semaphore entrada_monitor = new Semaphore(1, true);
	
	public MyEntradaMonitor entrada_monitor;
	
	private int k;
	private RedPetri red;
//	private MySemaphore colas[];
	private Semaphore colas[];
	private List<Integer> sensibilizadas = new ArrayList<Integer>();
	private List<Integer> quienesEnCola = new ArrayList<Integer>();
	private Politicas politicas = null;
	
	private List<String> log = new ArrayList<String>();

	public GestorMonitor(int I[][], int[] M, int[][] invariantes, int[] resultadoInvariantes, int[] tiempos, int nroPiezas) {

		
		entrada_monitor = new MyEntradaMonitor(nroPiezas, countTransitions(I));
		
		red = new RedPetri(countTransitions(I), I, M, entrada_monitor, invariantes, resultadoInvariantes, tiempos);
//		colas = new MySemaphore[countTransitions(I)];
		colas = new Semaphore[countTransitions(I)];
		for (int i = 0; i < countTransitions(I); i++) {
//			colas[i] = new MySemaphore();
			colas[i] = new Semaphore(0);
		}
		for (int i = 0; i < countTransitions(I); i++) {
			quienesEnCola.add(0);
			sensibilizadas.add(0);
		}
	}

	public int dispararTransicion(int transicion){
		
		String t = Thread.currentThread().getName();
		
		entrada_monitor.acquire(transicion);
		System.out.println(t + " obtuve la entrada al monitor");
		k = 1;
		while (k == 1) {
			k = red.disparar(transicion);
			System.out.println(Thread.currentThread().getName() + " Intentando disparar transicion " + transicion + " k = " + k);
			
			if (k == 1) {
			
				// Si no se cumple los invariantes
				 try {
				 red.revisarInvariantes();
				 } catch (Exception e) {
				 e.printStackTrace();
				 System.exit(1);
				 }
				
				// Dispare una transicion correctamente
				System.out.println("         " + t + " Dispare transicion " + transicion + ", k = " + k);
								
				sensibilizadas = red.get_sensibilizadas();
				
				// Actualizo quienes estan en la cola
//				actualizarQuienesEnCola();
				
				List<Integer> listasParaDisparar = andVectores(sensibilizadas, quienesEnCola);
				// Aca hay que hacer el and de sensibilizidas y listas para disparar
				
				if (listasParaDisparar.contains(1)) {
					
					int indiceDespertar = politicas.cual(listasParaDisparar);
					
					// Despierto a un hilo que esta esperando por esa transicion
					colas[indiceDespertar].release();
					
					System.out.println(t + " desperte al hilo en transicion " + indiceDespertar);
					
					return 0;

				} else {
					
					// Salgo del while
					System.out.println(t + " salgo del monitor sin despertar a nadie");
					entrada_monitor.release();
					
					return 0;
				}
			} else if (k == -3) {
				
//				System.out.println(t + " transicion " + transicion + " no sensibilizada, me voy a la cola");
				
				quienesEnCola.set(transicion, 1);
				// No dispare por no estar sensibilizada
				entrada_monitor.release();
				try {
					colas[transicion].acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				quienesEnCola.set(transicion, 0);

			} else if (k == -1){
				
//				System.out.println(t + " Antes del alfa, durmiendo " + red.getTimeSleep(transicion) + " ms");
				entrada_monitor.release();
				
				try {
					Thread.sleep(red.getTimeSleep(transicion));
				} catch (InterruptedException e) {
					System.out.print("Error hilo esperando alfa");
					e.printStackTrace();
				}
				
				// No dispare por no estar en ventana de tiempo (antes del alfa)
				// Igual que el caso anterior pero no espero en la cola sino que me voy
//				k = 1;
//				System.out.println(t + " saliendo del monitor despues de dormir con k = " + k);
				return 1;

			} else if(k== -2){
				//estoy despues del beta, me voy a la cola a esperar
//				System.out.println(t + " transicion " + transicion + " despues del beta, me voy a la cola");
//				System.out.println(t + " salgo del monitor y me pongo a esperar en una cola [" + entrada_monitor.availablePermits() + "]");
//				k = 1;
				entrada_monitor.release();
//				System.out.println(t + " {" + entrada_monitor.availablePermits() + "}");
				try {
					colas[transicion].acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
//				System.out.println("K TIENE UN VALOR NO VALIDO---------------------- = " + k + " " + t);
				System.exit(-1);
			}
		}
		
//		System.out.println(t + " salgo del monitor [" + entrada_monitor.availablePermits() + "]");
		entrada_monitor.release();
//		System.out.println(t + " {" + entrada_monitor.availablePermits() + "}");
//		System.out.println("USANDO ULTIMO RETURN " + t);
		return 1;
	}

	private void actualizarQuienesEnCola() {
		for (int i = 0; i < colas.length; i++) {
			
			// Lleno quienesEnCola con "1" donde hay hilos esperando y "0" donde no los hay
			
//			int cantidad_hilos_cola = colas[i].getQueueLength();
//			boolean hay_hilos_cola = colas[i].hasQueuedThreads();
//			boolean hay_hilos_cola = colas[i].isTaken();
			
//			if (colas[i].isTaken()) {
			if(colas[i].hasQueuedThreads()){
				quienesEnCola.set(i, 1);
			} else {
				quienesEnCola.set(i, 0);
			}
		}
		
		return;
	}

	private List<Integer> andVectores(List<Integer> sensibilizadas, List<Integer> enCola) {
		List<Integer> listas = new ArrayList<Integer>(sensibilizadas.size());

		for (int i = 0; i < sensibilizadas.size(); i++) {
			listas.add(sensibilizadas.get(i) & enCola.get(i));
		}

		return listas;
	}

	public void verMarcado() {
		System.out.print(Arrays.toString(red.getMarcado()));
		return;
	}

	private int countTransitions(int marcado[][]) {
		return marcado[0].length;
	}
	
	public int writeToLog(String message){
		log.add(message);
//		System.out.println(message);
		if(checkLog() == true)
			return 0;
		else
			return -1;
	}
	
	private boolean checkLog(){
		return true;
	}
	
	public void setPoliticas(Politicas newPoliticas) { politicas = newPoliticas; }
	public Politicas getPoliticas() { return politicas; }
	
	public MyEntradaMonitor getSemaforoEntrada(){ return entrada_monitor;}
	
}