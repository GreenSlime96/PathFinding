package algorithms;

import java.awt.Point;
import java.util.List;
import java.util.function.BiFunction;

import core.Grid;

public class BestFirst extends Search {
	
	public List<Point> search(int startX, int startY, int endX, int endY, Grid grid) {
		BiFunction<Integer, Integer, Double> old_h = heuristic;
		heuristic = ((t, u) -> old_h.apply(t, u) * 1000000);
		return new AStar().search(startX, startY, endX, endY, grid);
	}

}
