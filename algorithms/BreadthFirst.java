package algorithms;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import core.Grid;
import core.Node;
import core.Util;

public class BreadthFirst extends Search {
	
	// ==== Search Method ====
	
	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		final Queue<Node> queue = new LinkedList<Node>();
		
		final Node startNode = grid.getNodeAt(startX, startY);
		final Node endNode = grid.getNodeAt(endX, endY);
		
		queue.add(startNode);
		startNode.open();

		while (!queue.isEmpty()) {			
			Node node = queue.poll();
			node.close();

			if (node == endNode) {								
				return Util.backtrace(node);
			}

			for (Node n : grid.expand(node, diagonalMovement)) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if either closed or opened sets contain
				if (n.closed() || n.opened())
					continue;
				
				queue.add(n);
				
				n.parent = node;
				n.open();
			}
		}
		
		System.out.println("no solution found");
		return null;
	}

}
