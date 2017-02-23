package core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

import javax.swing.Timer;

import algorithms.AStar;
import algorithms.BestFirst;
import algorithms.BiAStar;
import algorithms.BiBreadthFirst;
import algorithms.BreadthFirst;
import algorithms.DepthFirst;
import algorithms.Dijkstra;
import algorithms.Search;

public class Model extends Observable implements ActionListener {
	
	// ==== Properties ====
	
	private Dimension dimension;
	
	private final Timer timer = new Timer(50, this);
	
	private final List<Point> solution = new ArrayList<Point>();

	private boolean active = true;
	private boolean biSearch = false;
	private int diagonalMovement = DiagonalMovement.ALWAYS;
	private int searchAlgorithm = Algorithm.A_STAR;
	private int searchHeuristic = Heuristic.MANHATTAN_DISTANCE;
	
	private Thread thread;
	
	private final Set<Point> walls = Collections.synchronizedSet(new HashSet<Point>());
	private final Point start = new Point();
	private final Point goal = new Point();
	
	public Grid grid;
	
	// ==== Constructor ====
	
	public Model() {
		super();
		
		setActive(false);
		setSize(new Dimension(1, 1));
		setActive(true);
	}
	
	
	// ==== Accessors ====
	
	public synchronized final Dimension getSize() {
		return dimension;
	}
	
	public synchronized final void setSize(Dimension dimension) {
		grid = new Grid(dimension, walls);
		
		if (this.dimension == null) {
			this.dimension = dimension;
			fit();
			return;
		}
		
		if (!dimension.equals(this.dimension)) {
			this.dimension = dimension;
			
			// this is fucking useful!
			walls.removeIf(p -> !isInside(p));
			
			// TODO: implement point repositioning
			if (!isInside(start) || !isInside(goal)) {
				fit();
			}
			
			setChanged();
			notifyObservers();
		}
	}
	
	public synchronized final void fit() {
		clearWalls();
		
		final int midX = (dimension.width - 1) / 2;
		final int midY = (dimension.height - 1) / 2;
		
		start.setLocation(midX - 5, midY);
		goal.setLocation(midX + 5, midY);
				
		setChanged();
		notifyObservers();
	}
	
	public synchronized final int getDiagonalMovement() {
		return diagonalMovement;
	}
	
	// TODO: implement error checking
	public synchronized final void setDiagonalMovement(int diagonalMovement) {
		if (diagonalMovement != DiagonalMovement.ALWAYS && 
			diagonalMovement != DiagonalMovement.NEVER &&
			diagonalMovement != DiagonalMovement.IF_AT_MOST_ONE_OBSTACLE &&
			diagonalMovement != DiagonalMovement.ONLY_WHEN_NO_OBSTACLES) {
			throw new IllegalArgumentException("diagonalMovement not recognised");
		}
		
		this.diagonalMovement = diagonalMovement;
	}
	
	public synchronized final int getSearchAlgorithm() {
		return searchAlgorithm;
	}
	
	// TODO: ref above
	public synchronized final void setSearchAlgorithm(int searchAlgorithm) {
		if (searchAlgorithm != Algorithm.A_STAR &&
			searchAlgorithm != Algorithm.BEST_FIRST	&& 
			searchAlgorithm != Algorithm.BREADTH_FIRST &&
			searchAlgorithm != Algorithm.DEPTH_FIRST && 
			searchAlgorithm != Algorithm.DIJKSTRA) {			
			throw new IllegalArgumentException("searchAlgorithm not recognised");
		}
		
		this.searchAlgorithm = searchAlgorithm;
	}
	
	public synchronized final int getSearchHeuristic() {
		return searchHeuristic;
	}
	
	public synchronized final void setSearchHeuristic(int searchHeuristic) {
		if (searchHeuristic != Heuristic.MANHATTAN_DISTANCE &&
			searchHeuristic != Heuristic.EUCLIDEAN_DISTANCE &&
			searchHeuristic != Heuristic.CHEBYSHEV_DISTANCE &&
			searchHeuristic != Heuristic.OCTILE_DISTANCE) {
			throw new IllegalArgumentException("searchHeuristic not recognised");
		}
		
		this.searchHeuristic = searchHeuristic;
	}
	
	public synchronized final boolean getActive() {
		return active;
	}
	
	public synchronized final void setActive(boolean active) {
		this.active = active;
	}
	
	public synchronized final Point getStart() {
		return start;
	}
	
	public synchronized final void setStart(Point start) {
		if (isUnoccupied(start))
			this.start.setLocation(start);
	}
	
	public synchronized final Point getGoal() {
		return goal;
	}
	
	public synchronized final void setGoal(Point goal) {
		if (isUnoccupied(goal))
			this.goal.setLocation(goal);
	}
	
	public synchronized final Set<Point> getWalls() {
		return walls;
	}
	
	public synchronized final void addWall(Point point) {
		if (start.equals(point) || goal.equals(point))
			return;
		
		grid.setWalkableAt(point.x, point.y, false);
		walls.add(point);
	}
	
	public synchronized final void clearWall(Point point) {
		grid.setWalkableAt(point.x, point.y, true);
		walls.remove(point);
	}
	
	public synchronized final void clearWalls() {
		stopSearching();		
		clearSearches();
		
		walls.clear();	
		grid = new Grid(dimension, walls);
		
		setChanged();
		notifyObservers();
	}
	
	public synchronized final void startSearch() {
		stopSearching();
		startSearching();
	}
	
	public synchronized final void pauseSearch() {
		pauseSearching();
	}
	
	public synchronized final List<Point> getSolution() {
		return solution;
	}
	
	public synchronized final void generateMaze() {
		clearWalls();

		start.setLocation(1, 1);
		goal.setLocation(dimension.width - (dimension.width % 2 == 0 ? 3 : 2),
				dimension.height - (dimension.height % 2 == 0 ? 3 : 2));
				
		for (int x = 0; x < dimension.width; x++) {
			for (int y = 0; y < dimension.height; y++) {
				if (dimension.height % 2 == 0 && y == dimension.height - 1)
					continue;
				
				if (dimension.width % 2 == 0 && x == dimension.width - 1)
					continue;
					
				Point point = new Point(x, y);
				
				if (isUnoccupied(point)) {
					grid.setWalkableAt(point.x, point.y, false);
					walls.add(point);
				}
			}
		}
		
		Stack<Point> stack = new Stack<Point>();
		Set<Point> visited = new HashSet<Point>();
		
		stack.push(start);
					
		while (!stack.isEmpty()) {
			Point exit = stack.pop();

			walls.remove(exit);
			grid.setWalkableAt(exit.x, exit.y, true);
			visited.add(exit);

			List<Point> points = new ArrayList<Point>(4);

			for (int i = 0; i < 4; i++) {
				Point newPoint = new Point(exit);

				switch (i) {
				case 0:
					newPoint.translate(0, -2);
					break;
				case 1:
					newPoint.translate(2, 0);
					break;
				case 2:
					newPoint.translate(0, 2);
					break;
				case 3:
					newPoint.translate(-2, 0);
					break;
				}

				if (!isInside(newPoint))
					continue;

				points.add(newPoint);
			}

			// randomise the branching order
			Collections.shuffle(points);
			
			for (Point p : points) {
				if (visited.add(p)) {
					// if the current cell is at the outskirts do not remove wall
					// this ensures that the maze is always enclosed
					if (p.x == dimension.width - 1 || p.y == dimension.height - 1)
						continue;
					
					Point np = new Point((p.x + exit.x) / 2, (p.y + exit.y) / 2);
					walls.remove(np);
					grid.setWalkableAt(np.x, np.y, true);
					stack.push(p);
				}
			}
		}
	}
	
	public synchronized final boolean getBiSearch() {
		return biSearch;
	}
	
	public synchronized final void setBiSearch(boolean biSearch) {
		this.biSearch = biSearch;
	}
	
	// ==== ActionListener Implementation ====
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		if (e.getSource() == timer) {
			setChanged();
			notifyObservers();
			
			if (thread != null && thread.isAlive())
				return;
			
			if (Search.isActive)
				return;
			
			timer.stop();
		}		
	}
	
	// ==== Private Helper Methods ====
	
	public boolean isWalkable(int x, int y) {
		Point point = new Point(x, y);
		return isWalkable(point);
	}
	
	public boolean isWalkable(Point point) {
		return isInside(point) && !(walls.contains(point));
	}
	
	public boolean isUnoccupied(Point point) {
		return isWalkable(point) && !start.equals(point) && !goal.equals(point);
	}
	
	public boolean isInside(Point point) {
		return (point.getX() >= 0 && 
				point.getX() < dimension.getWidth() &&
				point.getY() >= 0 &&
				point.getY() < dimension.getHeight());
	}
	
	private void stopSearching() {
		Search.isActive = false;
		
		if (thread != null) {
			thread.interrupt();			
			
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}
		
		timer.stop();
	}
	
	private void clearSearches() {
		solution.clear();
	}
	
	private void pauseSearching() {
		
	}
	
	private void startSearching() {
		if (active) {			
			// clear the board and reset search states
			clearSearches();
			
			// set these?
			Search.solution = solution;
			
			// reset these variables
			Search.grid = new Grid(dimension, walls);
			Search.nodesProcessed = 0;
			Search.timeElapsed = 0;
			
			// retrieve these variables
			Search.diagonalMovement = diagonalMovement;
			
			grid = Search.grid;
			
			// setting the Heuristics
			
			BiFunction<Integer, Integer, Double> heuristic;
			
			switch (searchHeuristic) {
			case Heuristic.MANHATTAN_DISTANCE:
				heuristic = Heuristic::manhattanDistance;
				break;
			case Heuristic.EUCLIDEAN_DISTANCE:
				heuristic = Heuristic::euclideanDistance;
				break;
			case Heuristic.OCTILE_DISTANCE:
				System.out.println("here");
				heuristic = Heuristic::octileDistance;
				break;
			case Heuristic.CHEBYSHEV_DISTANCE:
				heuristic = Heuristic::chebyshevDistance;
				break;
			default:
				heuristic = Heuristic::manhattanDistance;
				break;
			}
			
			Search.heuristic = heuristic;			

			// setting the Search Algorithm to use			
			Search search;
			
			switch (searchAlgorithm) {
			case Algorithm.A_STAR:
				if (biSearch)
					search = new BiAStar();
				else
					search = new AStar();
				
				break;
			case Algorithm.BREADTH_FIRST:
				if (biSearch)
					search = new BiBreadthFirst();
				else
					search = new BreadthFirst();
				
				break;
			case Algorithm.DEPTH_FIRST:
				search = new DepthFirst();
				break;
			case Algorithm.BEST_FIRST:
				search = new BestFirst();
				break;
			case Algorithm.DIJKSTRA:
				search = new Dijkstra();
				break;
			default:
				search = new AStar();
				break;
			}
			
			// TODO: rely less on static types, those were just for practice! 
			thread = new Thread() {
				@Override
				public void run() {
					final long startTime = System.nanoTime();
					
					Node.operations.clear();
					
					final List<Point> solutions = search.search(start.x, start.y, goal.x, goal.y, Search.grid);
					System.out.println("time:\t" + (System.nanoTime() - startTime) / 1000000f + "ms");
					System.out.println("ops:\t" + Node.operations.size());
					
					if (solutions != null)
						solution.addAll(solutions);
				}
			};
			
			thread.start();
			timer.start();
		}
		
	}
}
