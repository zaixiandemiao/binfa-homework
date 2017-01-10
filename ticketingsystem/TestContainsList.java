package ticketingsystem;

import ticketingsystem.CbCouter.ThreadId;

public class TestContainsList implements Runnable {

	private SectionList<SeatNode> list;
	private SectionNode<SeatNode> item;
	
	int run_time = 10000;
	
	int right_time = 0;
	
	public TestContainsList(SectionList<SeatNode> list, SectionNode<SeatNode> item){
		this.list = list;
		this.item = item;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(run_time-- > 0) {
			boolean flag = this.list.contains(item);
			if(flag) {
				right_time++;
			}
		}
		System.out.println("threadid: "+ThreadId.get()+"	list size: "+this.list.size.get()+"	contains true time "+right_time);
	}

}
