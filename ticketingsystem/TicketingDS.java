package ticketingsystem;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import ticketingsystem.CbCouter.CombiningTree;
/**
 * 构造函数需要传入线程个数
 * @author wangjialong
 *
 */
public class TicketingDS implements TicketingSystem {
	
	
	volatile AtomicLong tid_counter = new AtomicLong(0);
	
	private int routenum = 5;
	private int coachnum = 8;
	private int seatnum = 100;
	private int stationnum = 10;
	
	
	ArrayList<Route> routes;
	CombiningTree tree;
	
	public TicketingDS() {
		routes = new ArrayList<Route>(routenum);
		for(int i = 0; i < routenum; i++ ) {
			Route route = new Route(coachnum, stationnum, seatnum);
			routes.add(route);
		}
		tree = new CombiningTree(64 * 2);
	}
	
	/**
	 * 需要传入线程数
	 * @param thread_num
	 */
	public TicketingDS(int thread_num) {
		routes = new ArrayList<Route>(routenum);
		for(int i = 0; i < routenum; i++ ) {
			Route route = new Route(coachnum, stationnum, seatnum);
			routes.add(route);
		}
		tree = new CombiningTree(thread_num * 2);
	}
	
	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum) {
		this.routenum = routenum;
		this.coachnum = coachnum;
		this.seatnum = seatnum;
		this.stationnum = stationnum;
		
		routes = new ArrayList<Route>(routenum);
		for(int i = 0; i < routenum; i++ ) {
			Route route = new Route(coachnum, stationnum, seatnum);
			routes.add(route);
		}
		tree = new CombiningTree(64 * 2);
	}
	
	
	public TicketingDS(int thread_num, int routenum, int coachnum, int seatnum, int stationnum) {
		this.routenum = routenum;
		this.coachnum = coachnum;
		this.seatnum = seatnum;
		this.stationnum = stationnum;
		
		routes = new ArrayList<Route>(routenum);
		for(int i = 0; i < routenum; i++ ) {
			Route route = new Route(coachnum, stationnum, seatnum);
			routes.add(route);
		}
		tree = new CombiningTree(thread_num * 2);
	}

	@Override
	public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
		// TODO Auto-generated method stub
		int left_count = inquiry(route, departure, arrival);
		if(left_count <= 0 || arrival > stationnum)
			return null;
		Route tmp_route = this.routes.get(route-1);
		long tid = -1;
		try {
			tid = tree.getAndIncrement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmp_route.sellTicket(tid, passenger, route, departure, arrival);
	}

	@Override
	public int inquiry(int route, int departure, int arrival) {
		// TODO Auto-generated method stub
		Route tmp_route = this.routes.get(route-1);
		int sold_num = 0;
		for(int i = departure; i < arrival-1; i++) {
			for(int j = i+1; j < stationnum + 1; j++) {
				sold_num += tmp_route.sold_num[i][j].get();
			}
		}
		for(int j = arrival; j > departure; j--) {
			for(int i = j-1; i > 0; i--) {
				sold_num += tmp_route.sold_num[i][j].get();
			}
		}
		return this.coachnum * this.seatnum - sold_num;

	}

	@Override
	public boolean refundTicket(Ticket ticket) {
		// TODO Auto-generated method stub
		int route = ticket.route -1;
		int coach = ticket.coach -1;
//		int seat = ticket.seat;
		Route tmp_route = this.routes.get(route);
		SectionList<SeatNode> coaches = tmp_route.coaches.get(coach);
		boolean result =  coaches.refundSeat(ticket);
		if(result) {
			tmp_route.sold_num[ticket.departure][ticket.arrival].getAndDecrement();
		}
		return result;
	}
	
	
	public void exportTids() throws Exception {
		File file = new File("tid.txt");
		PrintWriter pw = new PrintWriter(file);
//		System.out.println("routes length" + routes.size());
		pw.println("tid	passenger	route	coach	seat	departure	arrival");
		for(Route route: routes)
//			System.out.println("coaches" + route.coaches.size());
			for(SectionList<SeatNode> list: route.coaches) {
//				System.out.println("list size"+list.size.get());
				list.exportTicket(pw);
			}
		pw.close();
	}

}
