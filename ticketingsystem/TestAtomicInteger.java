package ticketingsystem;

import java.util.concurrent.atomic.AtomicLong;

public class TestAtomicInteger implements Runnable {
	
	private AtomicLong value;
	
	public TestAtomicInteger(AtomicLong value) {
		this.value = value;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int time = 10000;
		while(time-- > 0) {
			System.out.println(this.value.getAndIncrement());
		}
	}

}
