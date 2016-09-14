//package raymond;

import java.util.LinkedList;
import java.util.Queue;

public class RequestQ {

	public static Queue<String> queue = new LinkedList<String>();	
	private int size;		// = Ray_protocol.neighcount +1;
	public String first = queue.peek();
	
	public RequestQ(int limit){
		this.size=limit;
	}
	
	public synchronized void enqueue(String Node){	
		queue.add(Node);
	}
	
	public String dequeue(){
		return queue.remove();
	}
	
	public synchronized boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public synchronized boolean isFull(){
		return (queue.size()==size);
	}
}
