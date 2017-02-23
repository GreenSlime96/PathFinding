package algorithms;

import java.awt.Point;
import java.util.List;
import java.util.PriorityQueue;

import core.Grid;
import core.Node;
import core.Util;

public class AStar extends Search {

	// ==== Constants ====

	public static final double SQRT_2 = Math.sqrt(2);

	// ==== Public Static Methods ====

	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		final PriorityQueue<Node> queue = new PriorityQueue<Node>();
		
		final Node startNode = grid.getNodeAt(startX, startY);
		final Node goalNode = grid.getNodeAt(endX, endY);

		queue.add(grid.getNodeAt(startX, startY));
		startNode.open();

		while (!queue.isEmpty()) {
			Node node = queue.poll();
			node.close();

			if (node == goalNode) {
				return Util.backtrace(node);
			}
			
			for (Node n : grid.expand(node, diagonalMovement)) {
				if (n.closed())
					continue;
				
				double ng = node.g + ((n.x - node.x == 0 || n.y - node.y == 0) ? 1 : SQRT_2);
				double f, g, h;

				if (!n.opened() || ng < n.g) {					
					final int dx = Math.abs(n.x - endX);
					final int dy = Math.abs(n.y - endY);

					g = ng;
					h = n.h != 0 ? n.h : heuristic.apply(dx, dy);
					f = g + h;

					n.f = f;
					n.g = g;
					n.h = h;

					if (n.opened())
						queue.remove(n);
					else
						n.open();

					n.parent = node;
					queue.add(n);
				}
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("no solution found!");
		return null;
	}
}
