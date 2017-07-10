
public class MySemaphore {
	
	boolean taken = false;
	
	public MySemaphore(){
		taken = false;
	}
	
	public synchronized void acquire(){
		taken = true;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void release(){
		taken = false;
		notify();
	}
	
	public boolean isTaken(){
		return taken;
	}
}
