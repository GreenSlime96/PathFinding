package algorithms;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import core.Grid;
import core.Heuristic;
import core.Node;

public abstract class Search {
	
	// ==== Static Variables ====
	
	public static BiFunction<Integer, Integer, Double> heuristic;
	
	public static Set<Node> opened, closed;
	public static List<Point> solution;
	
	public static int diagonalMovement;
	public static int nodesProcessed;
	
	public static long timeElapsed;
	public static long sleepTime;
	
	public static Grid grid;
	
	public static int weight;
	
	public static boolean isActive;
	
	
	// ==== Properties ====
	
	public int _diagonalMovement;
	public int _weight;
	
	// ==== Constructor ====
	
//	public Search(Search search) {
//		
//	}
	
	// ==== Abstract Methods ====
	
	public abstract List<Point> search(int startX, int startY, int endX, int endY, Grid grid);
}
