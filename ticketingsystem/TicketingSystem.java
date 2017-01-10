package ticketingsystem;

class Ticket {
	
	long tid;
	String passenger;
	int route;
	int coach;
	int seat = -1;
	int departure;
	int arrival;
	
	public Ticket() {}
	
	public Ticket(long tid, String passenger, int route, int coach, int seat, int departure, int arrival) {
		super();
		this.tid = tid;
		this.passenger = passenger;
		this.route = route;
		this.coach = coach;
		this.seat = seat;
		this.departure = departure;
		this.arrival = arrival;
	}

	@Override
	public int hashCode() {
		return (int) this.tid;
	}
	
	public Ticket setTid(long tid) {
		this.tid = tid;
		return this;
	}
	
	@Override
	public String toString() {
		return "[Ticket tid="+tid+",passenger="+passenger+",route="+route+",coach="+coach
				+",seat="+seat+",departure="+departure+",arrival="+arrival+" ]";
	}
	
}

public interface TicketingSystem {
	
	Ticket buyTicket(String passenger, int route, int departure, int arrival);
	
	int inquiry(int route, int departure, int arrival);
	
	boolean refundTicket(Ticket ticket);

}
