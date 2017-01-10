package ticketingsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node<T> extends AtomicMarkableReference<T>{
	
	public Node() {
		super(null, false);
		this.key = -1;
	}
	
	public Node(T item) {
		super(item, false);
		this.item = item;
		this.key = item.hashCode(); 
	}
	public Node(Node<T> item) {
		super(item.item, false);
		this.item = item.item;
		this.key = item.item.hashCode();
	}
	
	T item;
	int key;
	AtomicMarkableReference<Node<T>> next = null;
	Lock lock = new ReentrantLock();
	
	AtomicBoolean locked = new AtomicBoolean(false);
	
	List<Section> sold_sections = Collections.synchronizedList(new ArrayList<Section>());
	
	
}
