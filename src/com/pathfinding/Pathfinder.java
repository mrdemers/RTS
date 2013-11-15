package com.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.tiles.Tile;

public class Pathfinder {
	private static List<PathNode> openList = new ArrayList<PathNode>();
	private static List<PathNode> closedList = new ArrayList<PathNode>();

	/**
	 * Finds the shortest path to the specified tile
	 * @param tiles - A 2D array of tiles which represent positions the character can walk to
	 * @param sx - The starting x coordinate
	 * @param sy - The starting y coordinate
	 * @param ex - The end x coordinate
	 * @param ey - The end y coordinate
	 * @return The shortest path possible
	 */
	public static Path findPath(Tile[][] tiles, int sx, int sy, int ex, int ey) {
		if (!tiles[ey][ex].passable()) return new Path(null);
		
		//Attempt to find a straight line path right at the start
		PathNode los = inLOS(tiles, sx, sy, ex, ey, null);
		if (los != null) {
			LinkedList<PathNode> nodes = new LinkedList<PathNode>();
			nodes.add(los);
			return new Path(nodes);
		}
		
		//The first node, which is where you start
		PathNode currentNode = new PathNode(sx, sy, tiles[sy][sx].passable(), null);
		//Add the first node to the open list.
		openList.add(currentNode);
		
		//Do this until the open list is empty
		while (!openList.isEmpty()) {
			//Check for the node in the open list with the lowest "cost". Cost = distance from the start + distance to the end
			currentNode = lowestCost();
			
			//Check for every tile if there is a straight line path available, speeds up algorithm
			los = inLOS(tiles, currentNode.x, currentNode.y, ex, ey, currentNode);
			if (los != null) {
				openList.add(los);
				currentNode = los;
			}
			
			if (currentNode.x == ex && currentNode.y == ey) {
				/*
				 * We found a path to the end, create a Path object to return
				 */
				LinkedList<PathNode> temp = new LinkedList<PathNode>();
				while (currentNode != null) {
					temp.add(currentNode);
					currentNode = currentNode.parent;
				}
				openList.clear();
				closedList.clear();
				return new Path(temp);
			} else {
				//Add the current node to the closed list. Closed list contains nodes which 
				//have been checked to be the end and adjacent nodes
				closedList.add(currentNode);
				//Then remove it from the open list, because we checked it
				openList.remove(currentNode);
				
				//This big block creates all the adjacent nodes. It checks if they are passable,
				//and if it is a diagonal move it checks if the blocks to either side of it are empty
				PathNode[] adjacentNodes = new PathNode[8];
				{
					int x = currentNode.x;
					int y = currentNode.y;
					if (x > 0 && y > 0) {
						boolean passable = tiles[y-1][x-1].passable() && tiles[y-1][x].passable() && tiles[y][x-1].passable();
						adjacentNodes[0] = new PathNode(x - 1, y - 1, passable, currentNode);
					}
					if (y > 0)
						adjacentNodes[1] = new PathNode(x, y - 1, tiles[y - 1][x].passable(), currentNode);
					if (x < tiles[y].length - 1 && y > 0) {
						boolean passable = tiles[y-1][x+1].passable() && tiles[y-1][x].passable() && tiles[y][x+1].passable();
						adjacentNodes[2] = new PathNode(x + 1, y - 1, passable, currentNode);
					}
					if (x < tiles[y].length - 1)
						adjacentNodes[3] = new PathNode(x + 1, y, tiles[y][x + 1].passable(), currentNode);
					if (x < tiles[y].length - 1 && y < tiles.length - 1) {
						boolean passable = tiles[y+1][x+1].passable() && tiles[y+1][x].passable() && tiles[y][x+1].passable();
						adjacentNodes[4] = new PathNode(x + 1, y + 1, passable, currentNode);
					}
					if (y < tiles.length - 1)
						adjacentNodes[5] = new PathNode(x, y + 1, tiles[y + 1][x].passable(), currentNode);
					if (x > 0 && y < tiles.length - 1) {
						boolean passable = tiles[y+1][x-1].passable() && tiles[y+1][x].passable() && tiles[y][x-1].passable();
						adjacentNodes[6] = new PathNode(x - 1, y + 1, passable, currentNode);
					}
					if (x > 0)
						adjacentNodes[7] = new PathNode(x - 1, y, tiles[y][x - 1].passable(), currentNode);
				}
				
				//For all the adjacent nodes we just made, if they exist and are passable, add them to the open list
				for (PathNode p : adjacentNodes) {
					if (p == null)
						continue;
					if (!openList.contains(p) && !closedList.contains(p) && p.passable) {
						p.calculateScore(ex, ey);
						openList.add(p);
					}
				}
			}
		}
		
		//All of the open list is empty, and we didn't find a path, so there is no possible path
		openList.clear();
		closedList.clear();
		//Return a path with null nodes, because it won't cause errors
		return new Path(null);
	}

	/**
	 * Finds the path node in the open list with the lowest "cost" to move to
	 * @return
	 */
	public static PathNode lowestCost() {
		double lowest = Integer.MAX_VALUE;
		PathNode curr = null;
		for (PathNode p : openList) {
			if (p.score < lowest) {
				lowest = p.score;
				curr = p;
			}
		}
		return curr;
	}
	
	/**
	 * Checks if the current node can find a straight line path the the end x and y coordinates
	 * @return A path node that represents the end of the path, or null which means there is no straight line path
	 */
	public static PathNode inLOS(Tile[][] tiles, int startX, int startY, int endX, int endY, PathNode currentNode) {
		if (startX < 0 || startX > tiles[0].length) return null;
		if (startY < 0 || startY > tiles.length) return null;
		if (endX < 0 || endX > tiles[0].length) return null;
		if (endY < 0 || endY > tiles.length) return null;
		
		double actX = startX, actY = startY;
		int curX = startX, curY = startY;
		int oldX, oldY;
		PathNode end = new PathNode(endX, endY, true, currentNode);
		int distanceTraveled = 0;
		if (currentNode != null) distanceTraveled = currentNode.distanceFromStart;
		while (true) {
			double dir = Math.atan2(endY-curY, endX-curX);
			oldX = curX;
			oldY = curY;
			actX += Math.cos(dir);
			actY += Math.sin(dir);
			curX = (int)actX;
			curY = (int)actY;
			if (oldX - curX < 0 && oldY - curY > 0) { //Moving up right
				if (!(tiles[oldY-1][oldX].passable() && tiles[oldY][oldX+1].passable())) {
					return null;
				}
			}
			if (oldX - curX < 0 && oldY - curY < 0) { //Moving up left
				if (!(tiles[oldY+1][oldX].passable() && tiles[oldY][oldX+1].passable()))
					return null;
			}
			if (oldX - curX > 0 && oldY - curY < 0) { //Moving down left
				if (!(tiles[oldY+1][oldX].passable() && tiles[oldY][oldX-1].passable())) 
					return null;
			}
			if (oldX - curX > 0 && oldY - curY > 0) { //Moving down right
				if (!(tiles[oldY-1][oldX].passable() && tiles[oldY][oldX-1].passable())) 
					return null;
			}
			distanceTraveled++;
			if (curX == endX && curY == endY) {
				end.distanceFromStart = distanceTraveled;
				return end;
			}
			if (!tiles[curY][curX].passable()) return null;
		}
	}
}
