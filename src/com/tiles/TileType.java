package com.tiles;

import com.resources.Resource;


/* I should have made this an Enum, and still may*/
public class TileType {
	 int type;
	 boolean passable;
	 boolean collectable;
	 double speedMod;
	 String name;
	 Resource resource;
	 int color;
	
	//this constructor is used by TileTypes.java
	public TileType(String name, int type , boolean passable, double speedMod, Resource resource, boolean collectable, int color) {
		this.name = name;
		this.type = type;
		this.passable = passable;
		this.speedMod = speedMod;
		this.resource = resource;
		this.collectable = collectable;
		this.color = color;
	}	
}
