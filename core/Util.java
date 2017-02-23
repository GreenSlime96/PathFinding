package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
	
	public static List<Point> biBacktrace(Node nodeA, Node nodeB) {
		List<Point> listA = backtrace(nodeA);
		List<Point> listB = backtrace(nodeB);
		
		Collections.reverse(listB);
		
		listA.addAll(listB);
		
		return listA;
	}
	
	public static List<Point> backtrace(Node node) {
		List<Point> list = new ArrayList<Point>();
		
		while (node != null) {
			list.add(new Point(node.x, node.y));
			node = node.parent;
		}
		
		Collections.reverse(list);
		
		return list;
	}

}
