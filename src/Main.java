import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		final int NUMTRAN = 19;
//		GestorMonitor monitor = new GestorMonitor(NUMTRAN);
//		RedPetri red = new RedPetri();
		
		int[] a = {0,0,1,1};
		int[] b = {1,0,0,1};
		int[] c = new int[4];
		
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] & b[i];
			System.out.println(c[i]);
		}
		
		
		List<Integer> l1 = new ArrayList<Integer>();
		List<Integer> l2 = new ArrayList<Integer>();
		List<Integer> l3 = new ArrayList<Integer>();
		
		l1.add(0);
		l1.add(0);
		l1.add(1);
		l1.add(1);
		
		
		for (int index = 0; index < b.length; index++)
		{
		    l2.add(b[index]);
		}
		
		for (int i = 0; i < l1.size(); i++) {
			l3.add(l1.get(i) & l2.get(i));
		}
		
		System.out.println(l3.toString());
	}

}
