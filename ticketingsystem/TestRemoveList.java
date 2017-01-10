package ticketingsystem;

import ticketingsystem.CbCouter.ThreadId;

public class TestRemoveList implements Runnable {

	private SectionList<SeatNode> list;
	private SectionNode<SeatNode> item;
	
	int run_time = 10000;
	
	public TestRemoveList(SectionList<SeatNode> list, SectionNode<SeatNode> item){
		this.list = list;
		this.item = item;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int right_time = 0;
		while(run_time-- > 0) {
			if(this.list.remove(item))
				right_time++;
		}
		System.out.println("threadid: "+ThreadId.get()+"	list size: "+this.list.size.get()+"	remove true time "+right_time);
	}

}
