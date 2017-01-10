package ticketingsystem.CbCouter;

public class ThreadId {
	
	private static volatile int nextId = 0;
	
	/**
	 * 内部类，继承自ThreadLocal
	 * @author wangjialong
	 *
	 */
	private static class ThreadLocalID extends ThreadLocal<Integer> {
		protected synchronized Integer initialValue() {
			return nextId++;
		}
	}
	
	private static ThreadLocalID threadId = new ThreadLocalID();

	public static int get() {
		// TODO Auto-generated method stub
		return threadId.get();
	}
	
}
