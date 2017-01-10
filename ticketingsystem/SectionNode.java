package ticketingsystem;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class SectionNode<T extends SeatNode> extends AtomicMarkableReference<T>{
	
	public SectionNode() {
		super(null, false);
		this.key = -1;
		this.item = null;
	}
	
	public SectionNode(T initialRef) {
		super(initialRef, false);
		this.item = initialRef;
		this.key = initialRef.hashCode();
	}
	


	public SectionNode(SectionNode<T> node) {
		super(node.item, false);
		this.item = node.item;
		this.key = node.item.hashCode();
	}
	public SectionNode(T initialRef, boolean initialMark) {
		super(initialRef, initialMark);
		// TODO Auto-generated constructor stub
	}
	
	T item;
	int key;
	AtomicMarkableReference<SectionNode<T>> next = null;
	
	
}
