package ticketingsystem;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketNode<T extends Ticket> {
	
	private Lock lock = new ReentrantLock();
	
	T item;
	int key;
	TicketNode<T> next;
	
	public TicketNode() {
		this.key = -1;
	}

	public TicketNode(T item2) {
		// TODO Auto-generated constructor stub
		this.item = item2;
		this.key = item2.hashCode();
	}

	public void lock() {
		// TODO Auto-generated method stub
		lock.lock();
	}

	public void unlock() {
		// TODO Auto-generated method stub
		lock.unlock();
	}

	
	
}
