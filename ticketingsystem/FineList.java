package ticketingsystem;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 细粒度锁用于存放每个座位卖出去的票
 * @author wangjialong
 *
 * @param <T>
 */
public class FineList<T extends Ticket> {
	
	TicketNode<T> head = new TicketNode<T>();
	
	TicketNode<T> tail = new TicketNode<T>();
	
	volatile AtomicInteger size = new AtomicInteger(0);
	
	public FineList() {
		head.next = tail;
	}
	
	public boolean add(T item) {
//		int key = item.hashCode();
		head.lock();
		TicketNode<T> pred = head;
		try {
			TicketNode<T> curr = pred.next;
			curr.lock();
			try{
				while(curr != tail) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
					if( !(pred.item.departure >= item.arrival 
							|| pred.item.arrival <= item.departure) ) {
						return false;
					}
				}
//				if(curr.key == key) {
//					return false;
//				}
				if(item.route == 1 && item.coach == 1 && item.seat == 1)
					System.out.println();
				TicketNode<T> newNode = new TicketNode<T>(item);
				newNode.next = curr;
				pred.next = newNode;
				size.getAndIncrement();
				return true;
			} finally {
				curr.unlock();
			}
		} finally {
			pred.unlock();
		}
	}
	
	public boolean remove(T item) {
		TicketNode<T> pred = null, curr = null;
		int key = item.hashCode();
		head.lock();
		try {
			pred = head;
			curr = pred.next;
//			curr.lock();
			try {
				while(curr!= tail && curr.key < key) {
//					pred.unlock();
					pred = curr;
					curr = curr.next;
//					curr.lock();
				}
				if(curr.key == key) {
					pred.next = curr.next;
					size.getAndDecrement();
					return true;
				}
				return false;
			} finally {
//				curr.unlock();
			}
		} finally {
			head.unlock();
		}
	}

	public void print(PrintWriter pw) {
		head.lock();
		TicketNode<T> pred = head;
		pred.lock();
		try {
			TicketNode<T> curr = pred.next;
			curr.lock();
			try{
				while(curr != tail ) {
					pred.unlock();
					pred = curr;
					pw.println(curr.item.tid+"	"+curr.item.passenger+"	"+curr.item.route+"	"
							+curr.item.coach+"	"+curr.item.seat+"	"+curr.item.departure+"	"
							+curr.item.arrival);
					pw.flush();
					curr = curr.next;
					curr.lock();
				}
			} finally {
				curr.unlock();
			}
		} finally {
			pred.unlock();
		}
	}
	
}
