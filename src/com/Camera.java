package com;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import com.entities.Entity;
import com.entities.Unit;
import com.tiles.Tile;
import com.utilities.MathUtilities;

public class Camera {
	public float x, y;
	public float dx, dy;
	private int lx, ly, ux, uy;
	private int screenWidth, screenHeight;
	private Entity target;
	
	public Camera(int lowerBoundX, int lowerBoundY, int upperBoundX, int upperBoundY) {
		lx = lowerBoundX;
		ly = lowerBoundY;
		ux = upperBoundX;
		uy = upperBoundY;
		x=75*50;
		y=75*50;
	}
	
	public void update(GameContainer gc, int delta) throws SlickException{
		this.screenWidth = gc.getWidth();
		this.screenHeight = gc.getHeight();
		if (target != null && !((Unit)target).idle) {
			float middleX = x/Tile.TILE_WIDTH + screenWidth/Tile.TILE_WIDTH/2;
			float middleY = y/Tile.TILE_WIDTH + screenHeight/Tile.TILE_WIDTH/2;
			float dist = MathUtilities.distance(middleX, middleY, target.x, target.y);
			float dir = (float)Math.atan2(target.y - middleY, target.x- middleX);
			if (dist > 2){
				dx += Math.cos(dir) * .1;
				dy += Math.sin(dir) * .1;
				if (dx > 3) dx = 3;
				if (dy > 3) dy = 3;
				if (dx < -3) dx = -3;
				if (dy < -3) dy = -3;
			} else {
				this.dx = (float)Math.cos(dir) * dist;
				this.dy = (float)Math.sin(dir) * dist;
			}
		}
		
		this.x += dx * delta;
		this.y += dy * delta;
		dx *= .88f;
		dy *= .88f;
		if (dx < .001f && dx > -.001f) dx = 0;
		if (dy < .001f && dy > -.001f) dy = 0;
		if (x < lx*Tile.TILE_WIDTH) x = lx*Tile.TILE_WIDTH;
		if (x+screenWidth > ux*Tile.TILE_WIDTH) x = ux*Tile.TILE_WIDTH-screenWidth;
		if (y < ly*Tile.TILE_WIDTH) y = ly*Tile.TILE_WIDTH;
		if (y+screenHeight > uy*Tile.TILE_WIDTH) y = uy*Tile.TILE_WIDTH-screenHeight;
	}
	
//	public boolean increaseZoom(double amount) {
//		final float maxZoom = 2.0f;
//		final float minZoom = 0.5f;
//		if (zoom <= maxZoom && zoom >= minZoom) {
//			int in = (amount>0)?1:-1;
//			zoom += amount/100f;
//			Tile.TILE_WIDTH = (int)(zoom * 50);
//			if (zoom > maxZoom) { 
//				zoom = maxZoom;
//				Tile.TILE_WIDTH = (int)(zoom * 50);
//				return false;
//			}
//			else if (zoom < minZoom) { 
//				zoom = minZoom;
//				Tile.TILE_WIDTH = (int)(zoom * 50);
//				return false;
//			}
//			x += 10/zoom*in;
//			y += 10/zoom*in;
//			return true;
//		}
//		return false;
//	}
//	
	public int getX() { return (int)x; }
	public int getY() { return (int)y; }

	public void setPosition(float newX, float newY) {
		x = newX;
		y = newY;
		if (x < lx*Tile.TILE_WIDTH) x = lx*Tile.TILE_WIDTH;
		if (x+screenWidth > ux*Tile.TILE_WIDTH) x = ux*Tile.TILE_WIDTH-screenWidth;
		if (y < ly*Tile.TILE_WIDTH) y = ly*Tile.TILE_WIDTH;
		if (y+screenHeight > uy*Tile.TILE_WIDTH) y = uy*Tile.TILE_WIDTH-screenHeight;
	}
	
	public void setTarget(Entity e) {
		this.target = e;
	}
	public void clearTarget() {
		this.target = null;
	}
	
	public Entity getTarget() {
		return target;
	}
}
