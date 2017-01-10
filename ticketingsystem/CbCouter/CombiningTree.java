package ticketingsystem.CbCouter;

import java.util.Stack;


public class CombiningTree {
	
	CbTreeNode[] nodes;
	CbTreeNode[] leaf;
	
	
	public CombiningTree(int width) {
		nodes = new CbTreeNode[width - 1];
		nodes[0] = new CbTreeNode();
		for(int i = 1; i<nodes.length; i++) {
			nodes[i] = new CbTreeNode(nodes[(i-1)/2]);
		}
		leaf = new CbTreeNode[(width+1)/2];
		for(int i =0; i<leaf.length; i++) {
			leaf[i] = nodes[nodes.length - i -1];
		}
	}
	
	public int getAndIncrement() throws Exception {
		Stack<CbTreeNode> stack = new Stack<CbTreeNode>();
//		long thread_id = Thread.currentThread().getId();
		CbTreeNode myLeaf = leaf[(ThreadId.get()/2) % leaf.length];
//		System.out.println(Integer.parseInt(Thread.currentThread().getName()));
		CbTreeNode node = myLeaf;
		// precombine
		while(node.precombine()) {
			node = node.parent;
		}
		// combining
		CbTreeNode stop = node;
		node = myLeaf;
		int combined = 1;
		while(node != stop) {
			combined = node.combine(combined);
			stack.push(node);
			node = node.parent;
		}
		// operation
		int prior = stop.op(combined);
		// distribution
		while(!stack.empty()) {
			node = stack.pop();
			node.distribute(prior);
		}
		return prior;
	}

}
