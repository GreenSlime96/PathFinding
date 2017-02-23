package algorithms;

import java.awt.Point;
import java.util.List;
import java.util.Stack;

import core.Grid;
import core.Node;
import core.Util;

public class DepthFirst extends Search {

	// ==== Search Method ====

	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		final Stack<Node> stack = new Stack<Node>();
		
		final Node startNode = grid.getNodeAt(startX, startY);
		final Node endNode = grid.getNodeAt(endX, endY);
		
		stack.push(startNode);
		startNode.open();

		while (!stack.isEmpty()) {
			Node node = stack.pop();
			
			if (node == endNode) {								
				return Util.backtrace(node);
			}
			
			if (node.closed())
				continue;
			
			node.close();

			for (Node n : grid.expand(node, diagonalMovement)) {
				if (n.closed())
					continue;
				
				n.open();
				stack.add(n);
			}
		}
		
		System.out.println("no solution found");
		return null;
	}
}
