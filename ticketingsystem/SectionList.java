package ticketingsystem;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;


public class SectionList<T extends SeatNode> {
	
	private SectionNode<T> head = new SectionNode<T>();
	
	private SectionNode<T> tail = new SectionNode<T>();
	
	volatile AtomicInteger size = new AtomicInteger(0);
	
	public SectionList() {
		this.head.next = new AtomicMarkableReference<SectionNode<T>>(tail, false);
	}
	
	class Window {
		public SectionNode<T> pred, curr;
		public Window(SectionNode<T> myPred, SectionNode<T> myCurr) {
			pred = myPred; curr = myCurr;
		}
	}
	
	public Window find(SectionNode<T> head, int key) {
		SectionNode<T> pred = null, curr = null, succ = null;
		boolean[] marked = {false};
		boolean snip;
		retry: while(true) {
			pred = head;
			curr = pred.next.get(marked);
			while(true) {
				if (curr == tail) {  //ĩβ�ڵ�
					return new Window(pred, curr);
				}
				succ = curr.next.get(marked);
				while(marked[0]) {
					snip = pred.next.compareAndSet(curr, succ, false, false);
					if(!snip) continue retry;
					this.size.getAndDecrement(); 
					curr = succ;
					if(curr == tail) 
						return new Window(pred, curr);
					succ = curr.next.get(marked);
				}
				if(curr.key >= key) {   //��if�������while(marked)�ĺ��棬�Ա�֤pred,curr�ǿɴ��
					return new Window(pred, curr);
				}
				pred = curr;
				curr = succ;
			}
		}
	}
	
	
	public boolean add(SectionNode<T> section_node) {
		int key = section_node.item.hashCode();
		while(true) {
			Window window = find(head, key);
			SectionNode<T> pred = window.pred, curr = window.curr;
			if(curr.key == key) {
				return false;
			} else {
				SectionNode<T> node = new SectionNode<T>(section_node);
				node.next = new AtomicMarkableReference<SectionNode<T>>(curr, false);
				if(pred.next.compareAndSet(curr, node, false, false)) {
					this.size.getAndIncrement();
					return true;
				}
			}
		}
	}
	
	public boolean remove(SectionNode<T> section_node) {
		int key = section_node.item.hashCode();
		boolean snip;
		while(true) {
			Window window = find(head, key);
			SectionNode<T> pred = window.pred, curr = window.curr;
			if(curr == tail || curr.key != key) {
				return false;
			} else {
				SectionNode<T> succ = curr.next.getReference();
				snip = curr.next.attemptMark(succ, true);
				if(!snip) 
					continue;
				if(pred.next.compareAndSet(curr, succ, false, false))
					this.size.getAndDecrement();
				return true;
			}
		}
	}
	
	public boolean contains(SectionNode<T> section_node) {
		boolean[] marked = {false};
		int key = section_node.item.hashCode();
		SectionNode<T> curr = head;
		while(curr.key < key) {
			if(curr == tail)
				return false;
			SectionNode<T> succ = curr.next.get(marked);
			curr = succ;
		}
		return (curr.key == key && !marked[0]);
	}
	/**
	 * ����֮ǰӦ����Ticket����λ��ֵ, ���һ���µ�Seat�ڵ�,�ڳ�����λû����ʱ����
	 * @param t
	 * @return
	 */
	private boolean addTicket(Ticket t) {
		SeatNode seat_node = new SeatNode(t.route, t.coach, t.seat);
		SectionNode section_node = new SectionNode(seat_node);
		if(add(section_node)) {
			return section_node.item.sellTicket(t);
		}
		return false;
	}
	
	public boolean sellSeat(Ticket t) {
		if(t.seat > 0) 	// ��һ������λ����
			return addTicket(t);
		else {
			boolean[] marked = {false};
			SectionNode<T> curr = head;
			while(curr != tail) {
				SectionNode<T> succ = curr.next.get(marked);
				curr = succ;
				if(!marked[0] && succ.item!= null)
					if(succ.item.sellTicket(t)) {
						return true;
					}
			}
			return false;
		}
	}
	
	public boolean refundSeat(Ticket t) {
		boolean[] marked = {false};
		int key = t.seat;
		SectionNode<T> curr = head;
		while(curr.key < key) {
			if(curr == tail)
				return false;
			SectionNode<T> succ = curr.next.get(marked);
			curr = succ;
		}
		if(curr.key == key && !marked[0]) { //�ҵ�����λ�ڵ�
			if(curr.item != null) {
				return curr.item.refundTicket(t);
			}
		}
		return false;
	}


	public void exportTicket(PrintWriter pw) {
		// TODO Auto-generated method stub
		if(size.get() <= 0)
			return ;
		SectionNode<T> pred = null, curr = null, succ = null;
		boolean[] marked = {false};
		pred = head;
		curr = pred.next.get(marked);
		while(true) {
			if(!marked[0] && curr.item != null) { 
				curr.item.printTickets(pw);
			}
			pred = curr;
			if(curr == tail)
				return;
			succ = curr.next.get(marked);
			curr = succ;
		}
	}
}
