package com.pathfinding;

import java.util.Iterator;
import java.util.LinkedList;

public class Path implements Iterator<PathNode>{
	private LinkedList<PathNode> nodes;
	
	public Path(LinkedList<PathNode> nodes) {
		if (nodes != null)
			this.nodes = nodes;
		else
			this.nodes = new LinkedList<PathNode>();
	}
	
	public PathNode getCurrentNode() {
		return nodes.getLast();
	}
	
	public int size() {
		return nodes.size();
	}
	
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	@Override
	public boolean hasNext() {
		return nodes.size()>1;
	}

	@Override
	public PathNode next() {
		nodes.removeLast();
		return nodes.getLast();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public int getDistance() {
		if (isEmpty()) return 0;
		return nodes.getFirst().distanceFromStart;
	}
	
	public void clear() {
		if (nodes != null)
			nodes.clear();
	}
}
