import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorMonitor {

	final Semaphore entrada_monitor = new Semaphore(1, true);
	private int k;
	private RedPetri red;
	private MySemaphore colas[];
	private List<Integer> sensibilizadas = new ArrayList<Integer>();
	private List<Integer> quienesEnCola = new ArrayList<Integer>();
	
	private List<String> log = new ArrayList<String>();

	public GestorMonitor(int I[][], int[] M, int[][] invariantes, int[] resultadoInvariantes, int[] tiempos) {

		red = new RedPetri(countTransitions(I), I, M, entrada_monitor, invariantes, resultadoInvariantes, tiempos);
		colas = new MySemaphore[countTransitions(I)];
		for (int i = 0; i < countTransitions(I); i++) {
			colas[i] = new MySemaphore();
		}
		for (int i = 0; i < countTransitions(I); i++) {
			quienesEnCola.add(0);
			sensibilizadas.add(0);
		}
	}

	public int dispararTransicion(int transicion){
		
		String t = Thread.currentThread().getName();
		
		try {
//			System.out.println(t + " {{" + entrada_monitor.availablePermits() + "}} b4");
			entrada_monitor. acquire();
//			System.out.println(t + " obtuve la entrada al monitor");
//			System.out.println(t + " {{" + entrada_monitor.availablePermits() + "}}");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		k = 1;
		while (k == 1) {
			k = red.disparar(transicion);
//			System.out.println(Thread.currentThread().getName() + " Intentando disparar transicion " + transicion + " k = " + k);
			
			if (k == 1) {
			
				// Si no se cumple los invariantes
				 try {
				 red.revisarInvariantes();
				 } catch (Exception e) {
				 e.printStackTrace();
				 System.exit(1);
				 }
				
				// Dispare una transicion correctamente
//				System.out.println("         " + t + " Dispare transicion " + transicion + ", k = " + k);
				
				sensibilizadas = red.get_sensibilizadas();

				// Actualizo quienes estan en la cola
				actualizarQuienesEnCola();

				List<Integer> listasParaDisparar = andVectores(sensibilizadas, quienesEnCola);
//				System.out.println(t + " sensi: " + sensibilizadas);
//				System.out.println(t + " colas: " + quienesEnCola);
				// Aca hay que hacer el and de sensibilizidas y listas para disparar

				if (listasParaDisparar.contains(1)) {

					// A falta de politica despierto a los hilos de la primera
					// transicion disponible
					int indiceDespertar = listasParaDisparar.lastIndexOf(1);
	
					// Despierto a un hilo que esta esperando por esa transicion
					colas[indiceDespertar].release();
					
//					System.out.println(t + " desperte al hilo en transicion " + indiceDespertar);
					
					return 0;

				} else {
//					k = 0;
					// Salgo del while
//					System.out.println(t + " salgo del monitor sin despertar a nadie [" + entrada_monitor.availablePermits() + "]");
					entrada_monitor.release();
//					System.out.println(t + " {" + entrada_monitor.availablePermits() + "}");
					
					return 0;
				}
			} else if (k == -3) {
//				System.out.println(t + " transicion " + transicion + " no sensibilizada, me voy a la cola");
				
				// No dispare por no estar sensibilizada
//				System.out.println(t + " salgo del monitor y me pongo a esperar en una cola [" + entrada_monitor.availablePermits() + "]");
				entrada_monitor.release();
//				System.out.println(t + " {" + entrada_monitor.availablePermits() + "}");
//				k = 1;
				colas[transicion].acquire();

			} else if (k == -1){
				
//				System.out.println(t + " Antes del alfa, durmiendo " + red.tiempo.getTimeSleep(transicion) + " ms [" + entrada_monitor.availablePermits() + "]");
				entrada_monitor.release();
//				System.out.println(t + " {" + entrada_monitor.availablePermits() + "}");
				
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
				colas[transicion].acquire();
			}
			else{
				System.out.println("K TIENE UN VALOR NO VALIDO---------------------- = " + k + " " + t);
				System.exit(-1);
			}
		}
		
		System.out.println(t + " salgo del monitor [" + entrada_monitor.availablePermits() + "]");
		entrada_monitor.release();
		System.out.println(t + " {" + entrada_monitor.availablePermits() + "}");
		System.out.println("USANDO ULTIMO RETURN " + t);
		return 0;
	}

	private void actualizarQuienesEnCola() {
		for (int i = 0; i < colas.length; i++) {
			
			// Lleno quienesEnCola con "1" donde hay hilos esperando y "0" donde no los hay
			
//			int cantidad_hilos_cola = colas[i].getQueueLength();
//			boolean hay_hilos_cola = colas[i].hasQueuedThreads();
			boolean hay_hilos_cola = colas[i].isTaken();
			
			if (hay_hilos_cola) {
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
	
}