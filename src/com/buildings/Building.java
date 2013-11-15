package com.buildings;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.Camera;
import com.entities.Unit;
import com.tiles.Tile;

public abstract class Building {
	protected int x, y, width, height, health;
	protected Image image;
	
	public Building(int x, int y, int width, int height, int health) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.health = health;
	}
	
	public void render(GameContainer gc, Graphics g, Camera c){
		float xDraw = (x - width / 2) * Tile.TILE_WIDTH - c.getX();
		float yDraw = (y - height) * Tile.TILE_WIDTH -c.getY() ;
		Color imageColor = Color.white;
		g.drawImage(image, xDraw, yDraw, xDraw+width*Tile.TILE_WIDTH, yDraw+height*Tile.TILE_WIDTH, 0, 0, image.getWidth(), image.getHeight(), imageColor);
		
	}
	
	//defines whatever action a worker preforms here
	public abstract void doWork(Unit user);
	
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth(){ return width; }
	public int getHeight(){ return height; }
}
