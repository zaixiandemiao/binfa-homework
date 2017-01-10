package ticketingsystem;

import java.util.ArrayList;
import java.util.Random;

import ticketingsystem.CbCouter.ThreadId;

public class TestJob implements Runnable {
	
	
	private int run_time = 10000;
	
	private TicketingDS ts;
	
	private int routenum = 5;
//	private int coachnum = 8;
//	private int seatnum = 100;
	private int stationnum = 10;
	
	
	private int inquiry_num = 0;
	private int buy_num = 0;
	private int refund_num = 0;
	
	Random random = new Random();
	
	private ArrayList<Ticket> tickets = new ArrayList<Ticket>();
	
	
	private ExportDataThread mainThread;
	
	private double start_time;
	
	public TestJob(TicketingDS ts, ExportDataThread mainThread) {
		this.ts = ts;
		this.mainThread = mainThread;
	}
	
	public TestJob(TicketingDS ts, ExportDataThread mainThread, int run_time, double start_time) {
		this.ts = ts;
		this.run_time = run_time;
		this.mainThread = mainThread;
		this.start_time = start_time;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		regist(this);
		double thread_start = System.currentTimeMillis();
		while(this.run_time-- > 0) {
			double ran_double = random.nextDouble();
			try{
				if(ran_double < 0.6) {	//60%查询操作
					ts.inquiry(random.nextInt(routenum)+1, 
							random.nextInt(stationnum)+1, random.nextInt(stationnum)+1);
					inquiry_num++;
				}else if(ran_double < 0.9) { //30%的购票操作
					int departure = random.nextInt(stationnum) + 1;
					int arrival = departure + random.nextInt(stationnum - departure + 1);
					Ticket t = ts.buyTicket("wang", random.nextInt(routenum)+1, 
							departure, arrival);
					if(t != null) {
						tickets.add(t);
						buy_num++;
					}
				} else {  // 10%的退票操作
					if(tickets.size() > 0) {
						Ticket t = tickets.get(random.nextInt(tickets.size()));
						if(ts.refundTicket(t)) {
							tickets.remove(t);
							refund_num++;
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
//		System.out.println("Thread:"+thread_id+" inquiry:"+inquiry_num+" buy_ticket:"+buy_num
//				+" refund:"+refund_num + " list_size:"+tickets.size());
		unregist(this);
		double end_time = System.currentTimeMillis();
		System.out.println("thread"+ThreadId.get()+" end at:"+(end_time-start_time)/1000
				+"s thread run_time:"+(end_time-thread_start)/1000
				+"s thread throughput"+(inquiry_num+buy_num+refund_num)*1000/(end_time-thread_start)
				+"req/s inquiry:"+inquiry_num+" buy_ticket:"+buy_num
				+" refund:"+refund_num + " list_size:"+tickets.size());
	}

	private void unregist(TestJob testJob) {
		// TODO Auto-generated method stub
		mainThread.list.remove(testJob);
	}

	private void regist(TestJob testJob) {
		// TODO Auto-generated method stub
		mainThread.list.add(testJob);
	}

}
