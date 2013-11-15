package com.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

import com.Art;
import com.Camera;
import com.World;
import com.tiles.Tile;
import com.utilities.MathUtilities;

public class Entity implements Comparable<Entity>{
	/**
	 * The x and y value will be actual coordinates,
	 * drawing will take place based on that
	 */
	public float x, y;
	public Vector2f acceleration;
	public float width, height;
	public float radius;
	public boolean isAlive;
	private int id;
	private static int number;
	
	//Images
	private Image spritesImage = Art.loadImage("sprites.png");
	public SpriteSheet sheet = new SpriteSheet(spritesImage, 200, 300);
	private Image attackSprites = Art.loadImage("attackSprites.png");
	public SpriteSheet attackSheet = new SpriteSheet(attackSprites, 200, 300);
	
	public Entity(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		isAlive = true;
		acceleration = new Vector2f(0,0);
		this.id = ++number;
	}
	
	public void update(GameContainer gc, int delta) throws SlickException{
		move(acceleration.x, 0, delta);
		move(0, acceleration.y, delta);
	}
	
	private void move(float xMove, float yMove, int delta) {
		float origX = x;
		float origY = y;
		this.x += xMove * delta * .01f;
		this.y += yMove * delta * .01f;
		Tile[][] tiles = World.getTiles();
		for (int yy = (int)y-1; yy <= (int)y+1; yy++) {
			if (yy < 0 || yy >= tiles.length) continue;
			for (int xx = (int)x-1; xx <= (int)x+1; xx++) {
				if (xx < 0 || xx >= tiles[yy].length) continue;
				if (tiles[yy][xx].intersects(this) && !tiles[yy][xx].passable()) {
					this.x = origX;
					this.y = origY;
					return;
				}
			}
		}
	}
	
	public void render(GameContainer gc, Graphics g, Camera c) throws SlickException{
		
	}
	
	public boolean isAlive() {
		return isAlive;
	}

	public boolean intersectedByPoint(float x, float y) {
		return x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;
	}
	
	@Override
	public int compareTo(Entity e) {
		if (y < e.y) {
			return -1;
		} else if (y == e.y){
			return 0;
		} else
			return 1;
	}
	
	/**
	 * 
	 * @param other
	 * @return The distance between the current entity and another
	 */
	public float dist(Entity other) {
		if (other == null) return Float.MAX_VALUE;
		return MathUtilities.distance(x, y, other.x, other.y);
	}
	
	/**
	 * 
	 * @param other
	 * @return The angle in radians towards an entity
	 */
	public float dir(Entity other) {
		return (float)Math.atan2(y-other.y, x-other.x);
	}
	
	public int getID() {
		return id;
	}
}
