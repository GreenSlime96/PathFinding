package algorithms;

import java.awt.Point;
import java.util.List;
import java.util.PriorityQueue;
import core.Grid;
import core.Node;
import core.Util;

public class BiAStar extends Search {

	// ==== Constants ====
	
	private static final double SQRT_2 = Math.sqrt(2);
	
	private static final int BY_START = 1;
	private static final int BY_END = 2;

	// ==== Public Static Methods ====

	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		final PriorityQueue<Node> startQueue = new PriorityQueue<Node>();
		final PriorityQueue<Node> goalQueue = new PriorityQueue<Node>();
		
		final Node startNode = grid.getNodeAt(startX, startY);
		final Node goalNode = grid.getNodeAt(endX, endY);
		
		startQueue.add(startNode);
		startNode.opened = BY_START;
		
		goalQueue.add(goalNode);
		goalNode.opened = BY_END;

		while (!startQueue.isEmpty() && !goalQueue.isEmpty()) {
			Node node = startQueue.poll();
			node.close();
			
			for (Node n : grid.expand(node, diagonalMovement)) {
				if (n.closed())
					continue;
				
				if (n.opened == BY_END)
					return Util.biBacktrace(node, n);
				
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
						startQueue.remove(n);
					else {
						n.opened = BY_START;
						n.open();
					}
					
					n.parent = node;
					startQueue.add(n);
				}
			}
			
			// ==== Starting from End ====			
						
			node = goalQueue.poll();
			node.close();
			
			for (Node n : grid.expand(node, diagonalMovement)) {
				if (n.closed())
					continue;
				
				if (n.opened == BY_START)
					return Util.biBacktrace(node, n);
				
				double ng = node.g + ((n.x - node.x == 0 || n.y - node.y == 0) ? 1 : SQRT_2);
				double f, g, h;
				
				if (!n.opened() || ng < n.g) {					
					final int dx = Math.abs(n.x - startX);
					final int dy = Math.abs(n.y - startY);

					g = ng;
					h = n.h != 0 ? n.h : heuristic.apply(dx, dy);
					f = g + h;

					n.f = f;
					n.g = g;
					n.h = h;

					if (n.opened())
						goalQueue.remove(n);
					else {
						n.opened = BY_END;
						n.open();
					}

					n.parent = node;
					goalQueue.add(n);
				}
			}
			
			try {
				Thread.sleep(Search.sleepTime);
			} catch (InterruptedException e) {
				break;
			}
		}

		System.out.println("no solution found!");
		return null;
	}
}
