package ticketingsystem.CbCouter;

public class TestCombingTree implements Runnable {
	
	private CombiningTree tree;
	private int id;
	
	public TestCombingTree(int tid, CombiningTree tree) {
		// TODO Auto-generated constructor stub
		this.id = tid;
		this.tree = tree;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int run_time = 10000;
		while(run_time-- > 0) {
			try {
				int result = tree.getAndIncrement();
				System.out.println("Thread "+id + " get: " + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
