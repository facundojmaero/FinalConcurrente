import java.util.LinkedList;

public class MyLinkedList <E> extends LinkedList<E>{
	int indiceActual;
	
	public MyLinkedList(){
		super();
		indiceActual = 0;
	}
	
	public E getActual(){
		return (E) get(indiceActual);
	}
	
	public void avanzar(){
		indiceActual = (indiceActual+1) % size();
	}
}
