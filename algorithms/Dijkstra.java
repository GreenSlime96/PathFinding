package algorithms;

import java.awt.Point;
import java.util.List;

import core.Grid;

public class Dijkstra extends Search {

	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		heuristic = (t, u) -> 0d;
		return new AStar().search(startX, startY, endX, endY, grid);
	}
}
