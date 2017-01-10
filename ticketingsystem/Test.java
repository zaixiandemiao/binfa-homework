package ticketingsystem;

import java.util.Scanner;

import ticketingsystem.CbCouter.CombiningTree;
import ticketingsystem.CbCouter.TestCombingTree;

public class Test {
	
	public static Integer THREAD_NUM = 64;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		// 按照6,3,1比例进行TicketingDS的测试
		if(args.length == 0 || args[0].equals("tds")) {
		
			double start_time = System.currentTimeMillis();
			TicketingDS tds;
			int thread_num = THREAD_NUM;
			if(args.length == 2) {
				thread_num = Integer.parseInt(args[1]);
				tds = new TicketingDS(thread_num);
			} else {
				tds = new TicketingDS(THREAD_NUM);
			}
			ExportDataThread thread = new ExportDataThread(start_time, tds);
			
			for (int i = 0; i < thread_num; i++) {
				Thread t = new Thread(new TestJob(tds, thread, 10000, start_time));
				t.start();
			}
			
			thread.start();
		}
		
		// 测试CombiningTree主键唯一
		if(args.length > 0 && args[0].equals("tid")) {
			// this is for testing the combiningtree
			CombiningTree tree = new CombiningTree(8);  // THREAD_NUM * 2;
			for(int i=0; i < THREAD_NUM; i++) {
				Thread t = new Thread(new TestCombingTree(i, tree));
				t.start();
			}
		}
		
		// this is for testing the AtomicInteger
		
//		AtomicLong atomic = new AtomicLong();
//		
//		for(int i=0; i< 16; i++) {
//			new Thread(new TestAtomicInteger(atomic)).start();
//		}
		
		// this is for testing the LockFreeList
		if(args.length > 0 && args[0].equals("list")) {
			SectionList<SeatNode> list = new SectionList<>(); 
			
			SeatNode node =  new SeatNode(1, 10, 10);
			
			SectionNode<SeatNode> t = new SectionNode<SeatNode>(node);
			
			
			for(int i = 0; i < THREAD_NUM; i++) {
				new Thread(new TestAddList(list, t)).start();
			}
			for(int i = 0; i < THREAD_NUM; i++) {
				new Thread(new TestRemoveList(list, t)).start();
			}
			for(int i = 0; i < THREAD_NUM; i++) {
				new Thread(new TestContainsList(list, t)).start();
			}
		
		}
		// end of testing of the LockFreeList
		
		// this is non thread test 
		
//		Ticket t = tds.buyTicket("wang", 1, 2, 3);
//		if(t != null) {
//			System.out.println("Ticket is sold successfully!" + t );
//			if(tds.refundTicket(t))
//				System.out.println("ticket is refunded");;
//		}
//		t = tds.buyTicket("wang", 1, 2, 3);
//		if(t != null) {
//			System.out.println("Ticket is sold successfully!" + t);
//		}
		
		// end of non thread test
	}

}
