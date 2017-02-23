package algorithms;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import core.Grid;
import core.Node;
import core.Util;

public class BiBreadthFirst extends Search {
	
	// ==== Constants ====
	
	private static final int BY_START = 1;
	private static final int BY_END = 2;
	
	
	// ==== Search Implementation ====
	
	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		final Queue<Node> startQueue = new LinkedList<Node>();
		final Queue<Node> goalQueue = new LinkedList<Node>();
		
		final Node startNode = grid.getNodeAt(startX, startY);
		final Node goalNode = grid.getNodeAt(endX, endY);
		
		startQueue.add(startNode);
		startNode.opened = BY_START;
		
		goalQueue.add(goalNode);
		goalNode.opened = BY_END;

		while (!startQueue.isEmpty() && !goalQueue.isEmpty()) {
			final long startTime = System.nanoTime();
			
			Node node = startQueue.poll();
			node.close();
			
			for (Node n : grid.expand(node, diagonalMovement)) {
				if (n.closed())
					continue;
				
				if (n.opened()) {
					if (n.opened == BY_END)
						return Util.biBacktrace(node, n);
					
					continue;
				}

				startQueue.add(n);
				n.parent = node;
				n.opened = BY_START;
				n.open();
			}
			
			// ==== Starting from End ====			
						
			node = goalQueue.poll();
			node.close();
			
			for (Node n : grid.expand(node, diagonalMovement)) {
				if (n.closed())
					continue;
				
				if (n.opened()) {
					if (n.opened == BY_START)
						return Util.biBacktrace(n, node);
					
					continue;
				}

				goalQueue.add(n);
				n.parent = node;
				n.opened = BY_END;				
				n.open();
			}
			
			timeElapsed += System.nanoTime() - startTime;
			
			if (Search.sleepTime > 0)
				try {
					Thread.sleep(Search.sleepTime);
				} catch (InterruptedException e) {
					break;
				}
		}
		
		return null;
	}
}
