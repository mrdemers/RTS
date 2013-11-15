package com.tiles;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.Art;
import com.Camera;
import com.World;
import com.buildings.Stockpile;
import com.entities.Entity;
import com.resources.Resource;

public class Tile{
	public static int TILE_WIDTH = 50;
	private int x, y;
	private int type;

	//private static Image temp = Art.loadImage("tempTile.png"); leaving these around in case we have to go back
	//private static Image tempUnpassable = Art.loadImage("tempUnpassable.png");
	private static ArrayList<Image> images = new ArrayList<Image>();
	
	//this will load our image files as the game boots
	static{
		images.add(Art.loadImage("tempTile.png"));
		images.add(Art.loadImage("tempUnpassable.png"));
		images.add(Art.loadImage("tempTree.png"));
		images.add(Art.loadImage("tempStockpile.png"));
	}
	
	public Tile(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
		
		//stockpile script
		if(type==3){
			World.addBuilding(new Stockpile(x, y));
		}
		
	}
		
	public void update(GameContainer gc, int delta) throws SlickException {

	}
	
	public boolean passable() {
		return TileTypes.passable(type);
	}
	
	public void render(GameContainer gc, Graphics g, Camera c) throws SlickException {
		float x1 = x * TILE_WIDTH - c.getX();
		float y1 = y * TILE_WIDTH - c.getY();
		float x2 = x1 + TILE_WIDTH;
		float y2 = y1 + TILE_WIDTH;
		//Image data = (type==0)?temp:tempUnpassable;
		Image data = images.get(type);
		g.drawImage(data, x1, y1, x2, y2, 0, 0, data.getWidth(), data.getHeight());
	}
	
	public boolean intersects(Entity e) {
// Outofbounds from:    The right             The left                The bottom               The top
		return !(e.x - e.radius > x + 1 || e.x + e.radius < x || e.y - e.radius/3 > y + 1 || e.y + e.radius/3 < y);
	}
	
	public Resource getResource(){
		return TileTypes.resource(type);
	}
	
	public String toString(){
		return TileTypes.name(type)+" at: "+x+","+y;
	}
		
	public void onCollected(){
		if(TileTypes.collectable(type)){
			type=0;
		}
	}
	
	public int getColor() {
		return TileTypes.color(type);
	}
}
