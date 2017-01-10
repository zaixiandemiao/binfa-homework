package ticketingsystem;

import java.util.Random;

public class TestList implements Runnable {

	private int routenum = 5;
	private int coachnum = 8;
	private int seatnum = 100;
	private int stationnum = 10;

	Random rnd = new Random();
	private int thread_id;
	private int run_time = 10000;

	private LockFreeList<Ticket> list = new LockFreeList<Ticket>(100);

	public TestList(int id) {
		this.thread_id = id;
	}

	@Override
	public void run() {
		while (run_time-- > 0) {
			double dd = rnd.nextDouble();
			if (dd < 0.6) { // contains
				System.out.println("contains..");
				if (list.size.get() > 0) {
					Node<Ticket> t = list.get(rnd.nextInt(list.size.get()));
					if(t != null)
						list.contains(t.item);
				}
			} else if (dd < 0.9) { // add
				System.out.println("add...");
				Ticket t = new Ticket(0, "wang", rnd.nextInt(routenum), rnd.nextInt(coachnum), rnd.nextInt(seatnum),
						rnd.nextInt(stationnum), rnd.nextInt(stationnum));
				list.add(t);
			} else { // remove
				System.out.println("remove..");
				if (list.size.get() > 0) {
					Node<Ticket> t = list.get(rnd.nextInt(list.size.get()));
					if(t != null)
						list.remove(t.item);
				}
			}
		}
	}
}
