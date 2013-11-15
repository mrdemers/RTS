package com.tiles;

import java.util.ArrayList;

import com.resources.*;

/*
 * The main role of this class is to store hard-coded info about different tile 
 * types, in order to keep the tile class clean as we add more tiles
 */
public class TileTypes {

	//this could be an array, but we wouldn't be saveing much memory or time
	private static ArrayList<TileType> tiles = new ArrayList<TileType>();
	
	static{
		tiles.add(new TileType("open", 0, true, 1.0, Resource.Nothing, false, 0x55dd55));
		tiles.add(new TileType("Impassable", 1, false, 0.0, Resource.Nothing, false, 0));
		tiles.add(new TileType("Tree", 2, false, 0.0, Resource.Wood, true, 0x226838));
	}
	
	public static boolean passable(int index){
		return tiles.get(index).passable;		
	}
	
	public static double speedMod(int index){
		return tiles.get(index).speedMod;		
	}
	
	public static String name(int index){
		return tiles.get(index).name;
	}
	public static Resource resource(int index){
		return tiles.get(index).resource;
	}

	public static boolean collectable(int index) {
		return tiles.get(index).collectable;
	}
	
	public static int color(int index) {
		return tiles.get(index).color;
	}
}
