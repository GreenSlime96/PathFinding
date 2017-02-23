package core;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Grid {

	// ==== Properties ====

	public final int width, height;
	
	public final Node[][] nodes;

	// ==== Constructor ====
	
	public Grid(Dimension dimension, Set<Point> walls) {
		this(dimension.width, dimension.height, walls);
	}

	public Grid(int width, int height, Set<Point> walls) {
		this.width = width;
		this.height = height;
		
		this.nodes = buildNodes(walls);
	}

	// ==== Public Helper Methods ====
	
	public Node getNodeAt(int x, int y) {
		return nodes[y][x];
	}

	public boolean isWalkableAt(int x, int y) {
		return isInside(x, y) && nodes[y][x].isWalkable();
	}

	public boolean isInside(int x, int y) {
		return (x >= 0 && x < width && y >= 0 && y < height);
	}
	
	public void setWalkableAt(int x, int y, boolean walkable) {
		nodes[y][x].setWalkable(walkable);
	}
	
	public List<Node> expand(Node node, int diagonalMovement) {
		final List<Node> list = new ArrayList<Node>(8);

		boolean s0, s1, s2, s3;
		boolean d0, d1, d2, d3;

		s0 = s1 = s2 = s3 = false;
		d0 = d1 = d2 = d3 = false;

		final int x = node.x;
		final int y = node.y;

		// ↑
		if (isWalkableAt(x, y - 1)) {
			list.add(nodes[y - 1][x]);
			s0 = true;
		}

		// →
		if (isWalkableAt(x + 1, y)) {
			list.add(nodes[y][x + 1]);
			s1 = true;
		}

		// ↓
		if (isWalkableAt(x, y + 1)) {
			list.add(nodes[y + 1][x]);
			s2 = true;
		}

		// ←
		if (isWalkableAt(x - 1, y)) {
			list.add(nodes[y][x - 1]);
			s3 = true;
		}

		if (diagonalMovement == DiagonalMovement.NEVER)
			return list;

		if (diagonalMovement == DiagonalMovement.ONLY_WHEN_NO_OBSTACLES) {
			d0 = s3 && s0;
			d1 = s0 && s1;
			d2 = s1 && s2;
			d3 = s2 && s3;
		} else if (diagonalMovement == DiagonalMovement.IF_AT_MOST_ONE_OBSTACLE) {
			d0 = s3 || s0;
			d1 = s0 || s1;
			d2 = s1 || s2;
			d3 = s2 || s3;
		} else if (diagonalMovement == DiagonalMovement.ALWAYS) {
			d0 = true;
			d1 = true;
			d2 = true;
			d3 = true;
		} else {
			throw new IllegalArgumentException("incorrect diagonal parameter");
		}

		// ↖
		if (d0 && isWalkableAt(x - 1, y - 1))
			list.add(nodes[y - 1][x - 1]);

		// ↗
		if (d1 && isWalkableAt(x + 1, y - 1))
			list.add(nodes[y - 1][x + 1]);

		// ↘
		if (d2 && isWalkableAt(x + 1, y + 1))
			list.add(nodes[y + 1][x + 1]);

		// ↙
		if (d3 && isWalkableAt(x - 1, y + 1))
			list.add(nodes[y + 1][x - 1]);

		return list;
	}
	
	public Grid clone() {
		return null;
	}
	
	// ==== Private Helper Methods ====
	
	private Node[][] buildNodes(Set<Point> matrix) {
		final Node[][] nodes = new Node[height][width];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Point point = new Point(x, y);
				
				nodes[y][x] = new Node(x, y);				
				nodes[y][x].setWalkable(!matrix.contains(point));
			}			
		}
		
		return nodes;
	}
}
