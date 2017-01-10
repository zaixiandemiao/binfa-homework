package ticketingsystem;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SeatNode {
	
	int route;
	int coach;
	int seat;
	
	Lock lock = new ReentrantLock();
	
	volatile FineList<Ticket> sold_tickets = new FineList<Ticket>();
	
	volatile List<Section> sold_sections = Collections.synchronizedList(new ArrayList<Section>());

	public SeatNode(int r, int c, int s) {
		this.route = r;
		this.coach = c;
		this.seat = s;
	}
	
	private boolean isArrangeble(int departure, int arrival) {
		for(Section section: sold_sections) {
			if(!(section.departure >= arrival || section.arrival <= departure)) {
				return false;
			}
		}
		if(sold_sections.size() > 0)
			System.out.println();
		return true;
	}
	
	public boolean sellTicket(Ticket t) {
		lock.lock();
		try{
//			if(!isArrangeble(t.departure, t.arrival))
//				return false;
//			t.seat = this.seat;   //该座位可卖出
//			return  sold_tickets.add(t) && sold_sections.add(new Section(t.departure, t.arrival));
			t.seat = this.seat;
			return sold_tickets.add(t);
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 删除该座位卖出的票
	 * @param t
	 * @return
	 */
	public boolean refundTicket(Ticket t) {
		return sold_tickets.remove(t);
//		if(sold_tickets.remove(t))
//			return removeSection(t);
//		return false;
	}
	
	private synchronized boolean removeSection(Ticket t) {
		for(int i=0; i< sold_sections.size(); i++) {
			Section section  = sold_sections.get(i);
			if(section.departure == t.departure ||
					section.arrival == t.arrival) {
				return sold_sections.remove(i) != null;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.seat;
	}
	
	public void printTickets(PrintWriter pw) {
		sold_tickets.print(pw);
	}
	

}
