package ticketingsystem;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * 无锁链表用于存座位分段信息，以及卖出去的票，每个无锁表对应为一个车厢的所有座位
 * @author wangjialong
 *
 * @param <T>
 */
public class LockFreeList<T extends Ticket> {
	
	private Node<T> head = new Node<T>();
	
	private Node<T> tail = new Node<T>();
	
	private volatile boolean find_before_added;
	
	private volatile Node<T> find_node;
	
	private volatile boolean seat_before_added;
	
	private volatile Node<T> seat_node;
	
	private volatile AtomicInteger seat_index = new AtomicInteger(1);
	
	private int seat_size;
	public LockFreeList(int seat_size) {
		head.next = new AtomicMarkableReference<Node<T>>(tail, false);
		this.seat_size = seat_size;
	}
	
	
	volatile AtomicInteger size = new AtomicInteger(0);
	
	class Window {
		public Node<T> pred, curr;
		public Window(Node<T> myPred, Node<T> myCurr) {
			pred = myPred; curr = myCurr;
		}
	}
	
	public Window find(Node<T> head, int key) {
		Node<T> pred = null, curr = null, succ = null;
		boolean[] marked = {false};
		boolean snip;
		retry: while(true) {
			pred = head;
			curr = pred.next.get(marked);
			while(true) {
				if (curr == tail) {  //末尾节点
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
				if(curr.key >= key) {   //此if必须放在while(marked)的后面，以保证pred,curr是可达的
					return new Window(pred, curr);
				}
				pred = curr;
				curr = succ;
			}
		}
	}
	
	public Node<T> get(int index) {
		if(index < 0 || index > size.get() -1)
			return null;
		Node<T> pred = null, curr = null, succ = null;
		boolean[] marked = {false};
		pred = head;
		curr = pred.next.get(marked);
		while(true) {
			if(!marked[0]) { 
				index--;
				if(index < 0)
					break;
			}
			pred = curr;
			if(curr == tail)
				return null;
			succ = curr.next.get(marked);
			curr = succ;
		}
		return curr;
	}
	
	/**
	 * 找到乘车区间兼容的座位
	 * @param head
	 * @param seatnum
	 * @return
	 */
	public synchronized Node<T> findByDepartureArri(int departure, int arrival) {
		Node<T> pred = null, curr = null, succ = null;
		this.find_before_added = false;
		boolean[] marked = {false};
		retry:while(true){
			pred = head;
			curr = pred.next.get(marked);
			while(true) {
				if(curr == tail) {
					return null;
				}
				succ = curr.next.get(marked);
				if(!marked[0]) {
					boolean flag = false;
					for(int i=0; i< curr.sold_sections.size(); i++)
						if(!(curr.sold_sections.get(i).arrival <= departure 
								|| curr.sold_sections.get(i).departure >= arrival))
							flag = true;
					if(!flag) {
//						if(this.find_before_added)
//							continue retry;
						if(curr.locked.compareAndSet(false, true))
							return curr;
					}
				}
				pred = curr;
				curr = succ;
				this.find_node = curr;
			}
		}
	}
	
	public int avalibleSeat() {
		int seat = this.seat_index.getAndIncrement();
		if(seat >= this.seat_size)
			this.seat_index.set(1);
		while(findBySeat(seat) != null) {
			seat = this.seat_index.getAndIncrement();
			if(seat >= this.seat_size)
				this.seat_index.set(1);
		}
		return seat;
	}
	
	private synchronized Node<T> findBySeat(int seat) {
		Node<T> pred = null, curr = null, succ = null;
		this.seat_before_added = false;
		boolean[] marked = {false};
		retry:while(true){
			pred = head;
			curr =  pred.next.get(marked);
			while(true) {
				if(curr == tail) {
					curr = null;
					return curr;
				}
				succ = curr.next.get(marked);
				if(!marked[0] && curr.item.seat == seat) {
					if(this.seat_before_added)
						continue retry;
					return curr;
				}
				pred = curr;
				curr = succ;
				this.seat_node = curr;
			}
		}
	}
	
	public boolean add(T item) {
		int key = item.hashCode();
		while(true) {
			Window window = find(head, key);
			Node<T> pred = window.pred, curr = window.curr;
			if(curr.key == key) {
				return false;
			} else {
				Node<T> node = new Node<T>(item);
				node.next = new AtomicMarkableReference<Node<T>>(curr, false);
				if(pred.next.compareAndSet(curr, node, false, false)) {
					this.size.getAndIncrement();
//					if(item.coach == 2) 
//						System.out.println(item);
					if(this.find_node != null && node.key < this.find_node.key)
						this.find_before_added = true;
					if(this.seat_node != null && node.key < this.seat_node.key)
						this.seat_before_added = true;
					return true;
				}
			}
		}
	}
	
	public boolean remove(T item) {
		int key = item.hashCode();
		boolean snip;
		while(true) {
			Window window = find(head, key);
			Node<T> pred = window.pred, curr = window.curr;
			if(curr == tail || curr.key != key) {
				return false;
			} else {
				Node<T> succ = curr.next.getReference();
				snip = curr.next.attemptMark(succ, true);
				if(!snip) 
					continue;
				if(pred.next.compareAndSet(curr, succ, false, false))
					this.size.getAndDecrement();
				return true;
			}
		}
	}
	
	public boolean contains(T item) {
		boolean[] marked = {false};
		int key = item.hashCode();
		Node<T> curr = head;
		while(curr.key < key) {
			if(curr == tail)
				return false;
			Node<T> succ = curr.next.get(marked);
			curr = succ;
		}
		return (curr.key == key && !marked[0]);
	}
	
	public void print() {
		Node<T> pred, curr, succ;
		boolean[] marked = {false};
		pred = head;
		curr = pred.next.get(marked);
		while(true) {
			if(!marked[0] && curr != tail) { 
				System.out.println(curr.item);
			}
			pred = curr;
			if(curr == tail)
				return ;
			succ = curr.next.get(marked);
			curr = succ;
		}
	}
	
	
	
}
