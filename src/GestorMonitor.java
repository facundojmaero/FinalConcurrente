import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorMonitor {
	
	public MyEntradaMonitor entrada_monitor;
	
	private int k;
	private RedPetri red;
	private Semaphore colas[];
	private List<Integer> sensibilizadas = new ArrayList<Integer>();
	private List<Integer> quienesEnCola = new ArrayList<Integer>();
	private Politicas politicas = null;
	
	private boolean debug = false;

	public GestorMonitor(int I[][], int[] M, int[][] invariantes, int[] resultadoInvariantes, int[] tiempos, int nroPiezas) {

		entrada_monitor = new MyEntradaMonitor(nroPiezas, countTransitions(I));
		
		red = new RedPetri(countTransitions(I), I, M, invariantes, resultadoInvariantes, tiempos);
		colas = new Semaphore[countTransitions(I)];
		for (int i = 0; i < countTransitions(I); i++) {
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
		
		if(debug)
			System.out.println(t + " obtuve la entrada al monitor");
		
		k = 1;
		while (k == 1) {
			k = red.disparar(transicion);
			
			if(debug)
				System.out.println(Thread.currentThread().getName() + " Intentando disparar transicion " + transicion + " k = " + k);
			
			if (k == 1) {
			
				try { red.revisarInvariantes(); } 
				catch (Exception e) { System.exit(1); }
				
				// Dispare una transicion correctamente
				 if(debug)
				 	System.out.println("         " + t + " Dispare transicion " + transicion + ", k = " + k);
								
				sensibilizadas = red.get_sensibilizadas();
				
				List<Integer> listasParaDisparar = andVectores(sensibilizadas, quienesEnCola);
				// Aca hay que hacer el and de sensibilizidas y listas para disparar
				
				if (listasParaDisparar.contains(1)) {
					
					int indiceDespertar = politicas.cual(listasParaDisparar);
					
					// Despierto a un hilo que esta esperando por esa transicion
					colas[indiceDespertar].release();
				
					if(debug)
						System.out.println(t + " desperte al hilo en transicion " + indiceDespertar);

				} else {
					
					// Salgo del while
					if(debug)
						System.out.println(t + " salgo del monitor sin despertar a nadie");

					entrada_monitor.tryRelease(transicion);
				}
				
				return 0;
				
			} else if (k == -3) {
				
				if(debug)
					System.out.println(t + " transicion " + transicion + " no sensibilizada, me voy a la cola");
				
				quienesEnCola.set(transicion, 1);
				// No dispare por no estar sensibilizada
				entrada_monitor.release();
				
				try { colas[transicion].acquire(); } 
				catch (InterruptedException e) {}
				
				quienesEnCola.set(transicion, 0);

			} else if (k == -1){
				
				if(debug)
					System.out.println(t + " Antes del alfa, durmiendo " + red.getTimeSleep(transicion) + " ms");
				
				entrada_monitor.tryRelease(transicion);
				
				try { Thread.sleep(red.getTimeSleep(transicion)); } 
				catch (InterruptedException e) { System.out.print("Error hilo esperando alfa " + e); }
				
				
				// No dispare por no estar en ventana de tiempo (antes del alfa)
				// Igual que el caso anterior pero no espero en la cola sino que me voy
				if(debug)
					System.out.println(t + " saliendo del monitor despues de dormir con k = " + k);
				return 1;

			} else if(k== -2){
				//estoy despues del beta, me voy a la cola a esperar
				entrada_monitor.release();

				try { colas[transicion].acquire(); } 
				catch (InterruptedException e) {}
			}
			else{
				System.exit(-1);
			}
		}
		
		entrada_monitor.release();
		return 1;
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
	
	public void setPoliticas(Politicas newPoliticas) { 
		politicas = newPoliticas; 
		entrada_monitor.setPoliticas(newPoliticas);
	}
	public Politicas getPoliticas() { return politicas; }
	
	public MyEntradaMonitor getSemaforoEntrada(){ return entrada_monitor;}
	
}