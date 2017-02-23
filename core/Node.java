package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import algorithms.Search;

// ==== Node Implementation ====

public class Node implements Comparable<Node> {	
	
	class Operations {
		int x;
		int y;
		int o;
		
		public Operations(int x, int y, int o) {
			this.x = x;
			this.y = y;
			this.o = o;
		}
	}
	
	// ==== Public Static Members ====
	
	public static List<Operations> operations = new ArrayList<Operations>();
	
	// ==== Constants ====
	
	static final int BIT_WALKABLE = 0;
	static final int BIT_OPENED = 1;
	static final int BIT_CLOSED = 2;
	static final int BIT_OPENED_BY = 3;
	
	// ==== Properties ====
	
	// the real data of the node
	public final int x, y;
	
	// whether or not the node can be "walked"
//	public boolean walkable;
	
	// heuristics of the node
	public double f, g, h;

	// whether or not the node has been opened or not
	public int opened, closed;
	
	// the current node's parent
	public Node parent;
	
	// bit: property
	// 0: walkable
	// 1: opened
	// 2: closed
	// 3: opened_by
	private byte state = 0;
	
	// ==== Constructor ====
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Node(Point data, Node parent) {
		this(data.x, data.y);
		this.parent = parent;		
	}
	
	// ==== Helper Methods ====
	
	public void setWalkable(boolean walkable) {
		setBit(BIT_WALKABLE, walkable);
	}
	
	public boolean isWalkable() {
		return isBitSet(0);
	}
	
	public void close() {
		operations.add(new Operations(x, y, BIT_CLOSED));
		setBit(BIT_CLOSED, true);
	}
	
	public void open() {
		operations.add(new Operations(x, y, BIT_OPENED));
		setBit(BIT_OPENED, true);
	}
	
	public boolean closed() {
		return isBitSet(BIT_CLOSED);
	}
	
	public boolean opened() {
		return isBitSet(BIT_OPENED);
	}
	
	// ==== Private Helper Methods ====
	
	private boolean isBitSet(int position) {
		return ((state >> position) & 1) == 1;
	}
	
	private void setBit(int position, boolean bool) {
		state = (byte) (bool ? state | (1 << position) : state & ~(1 << position));
	}

	// ==== Object Overrides ====

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof Node))
			return false;

		Node other = (Node) obj;

		if (x != other.x)
			return false;

		if (y != other.y)
			return false;

		return true;
	}

	// ==== Comparable Implementation ====

	@Override
	public int compareTo(Node o) {
		if (this.f > o.f)
			return 1;
		else if (this.f < o.f)
			return -1;
		
		return 0;
	}		
}