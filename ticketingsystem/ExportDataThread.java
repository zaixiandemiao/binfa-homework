package ticketingsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExportDataThread extends Thread {
	
	List<Runnable> list = Collections.synchronizedList(new ArrayList<Runnable>());
	
	private TicketingDS tds;
	private double start_time;
	
	public ExportDataThread(TicketingDS tds) {
		// TODO Auto-generated constructor stub
		this.tds = tds;
	}
	
	public ExportDataThread(double start_time, TicketingDS tds) {
		// TODO Auto-generated constructor stub
		this.start_time = start_time;
		this.tds = tds;
	}

	public void add(Thread thread) {
		list.add(thread);
	}
	
	
	@Override
	public void run() {
		int while_time  = 0;
		try {
			Thread.sleep(5000);
			while(list.size() > 0 && while_time < 5) {
				Thread.sleep(5000);
				while_time++;
				// TODO Auto-generated catch block
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			this.tds.exportTids();
			double end_time = System.currentTimeMillis();
//			System.out.println("main thread run_time"+((end_time-start_time)/1000-5*(while_time+1))+"s");
			System.out.println("main thread export Tickets to tid.txt ended");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
