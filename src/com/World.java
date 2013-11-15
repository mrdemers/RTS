package com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.buildings.Building;
import com.buildings.Stockpile;
import com.entities.Entity;
import com.entities.Harvester;
import com.entities.Knight;
import com.tiles.Tile;

public class World {
	private static Tile[][] tiles;
	private static List<Building> buildings;
	private static List<Building> newBuildings;
	public int worldWidth;
	public int worldHeight;
	public static Image map = Art.loadImage("map.png");
	public Camera c;
	private static List<Entity> entities;
	private static List<Entity> newEntities;

	Player player;
	ComputerPlayer player2;

	public World() {
		tiles = new Tile[map.getHeight()][map.getWidth()];
		buildings = new ArrayList<Building>();
		newBuildings = new ArrayList<Building>();
		worldWidth = map.getWidth();
		worldHeight = map.getHeight();
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[y].length; x++) {
				int color = Art.getRGB(map.getColor(x, y));
				if (color == 0xffffff) {
					tiles[y][x] = new Tile(x, y, 0);
				} else if (color == 0x00ff00) {
					tiles[y][x] = new Tile(x, y, 2);
				} else {
					tiles[y][x] = new Tile(x, y, 1);
				}

			}
		}
		player = new Player();
		player2 = new ComputerPlayer();

		entities = new ArrayList<Entity>();
		newEntities = new ArrayList<Entity>();
		for (int i = 0; i < 10; i++) {
			addEntity(new Knight(80 + (float) (Math.random() - .5) * 3, 80 + (float) (Math.random() - .5) * 3, player));
		}
		
		addEntity(new Harvester(80,80,player));
//		for (int i = 0; i < 10; i++) {
//			addEntity(new Knight(5 + (float)(Math.random()-.5)*3, 5 + (float)(Math.random()-.5)*3, player2));
//		}
		addBuilding(new Stockpile(70, 80));
		addBuilding(new Stockpile(90, 80));
		
	}

	public void update(GameContainer gc, int delta) throws SlickException {
		processMouse(gc, delta);
		Collections.sort(entities);
		Iterator<Entity> i = entities.iterator();
		while (i.hasNext()) {
			Entity e = i.next();
			e.update(gc, delta);

			if (!e.isAlive()) {
				i.remove();
			}
		}
		entities.addAll(newEntities);
		buildings.addAll(newBuildings);
		newEntities.clear();
		newBuildings.clear();
		player.update(gc, delta, this);
		player2.update(gc, delta, this);
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		//Renders all the tiles
		for (int y = c.getY() / Tile.TILE_WIDTH - 1; y < (c.getY() + gc.getHeight()) / Tile.TILE_WIDTH + 1; y++) {
			if (y < 0 || y >= tiles.length) continue;
			for (int x = c.getX() / Tile.TILE_WIDTH - 1; x < (c.getX() + gc.getWidth()) / Tile.TILE_WIDTH + 1; x++) {
				if (x < 0 || x >= tiles[y].length) continue;
				tiles[y][x].render(gc, g, c);
			}
		}

		//Renders all the buildings
		for (Building cur : buildings) {
			cur.render(gc, g, c);
		}
		//Renders all the entities
		for (Entity e : entities) {
			e.render(gc, g, c);
		}
		//Renders the Gui
		player.render(gc, g, c);

		/*
		 * Draws lines through center of screen - can be used for debugging
		 * purposes
		 */
		/*
		g.drawLine(gc.getWidth() / 2, 0, gc.getWidth() / 2, gc.getHeight());
		g.drawLine(0, gc.getHeight() / 2, gc.getWidth(), gc.getHeight() / 2);
		*/
	}

	private void processMouse(GameContainer gc, int delta) {
		int sw = gc.getWidth(); // Screen width
		int sh = gc.getHeight(); // Screen height

		float speed = 60 * delta * .01f;
		
		// Manage x screen scrolling
		int mx = Mouse.getX();
		float edgeSpace = 200;
		if (mx > sw - sw / edgeSpace) {
			c.dx = speed;
			Game.currentCursor = Game.CAMERA_MOVE_CURSOR;
		}
		if (Mouse.getX() < sw / edgeSpace) {
			c.dx = -speed;
			Game.currentCursor = Game.CAMERA_MOVE_CURSOR;
		}

		// Manage y screen scrolling
		int my = sh - Mouse.getY();
		if (my > sh - sh / edgeSpace) {
			c.dy = speed;
			Game.currentCursor = Game.CAMERA_MOVE_CURSOR;
		}
		if (my < sh / edgeSpace) {
			c.dy = -speed;
			Game.currentCursor = Game.CAMERA_MOVE_CURSOR;
		}
		
		// Scroll wheel moved, zoom in and adjust camera
		int amt = Mouse.getDWheel();
		amt /= 100;
		amt *= 2;
		final int maxScroll = 200;
		final int minScroll = 40;
		int oldWidth = Tile.TILE_WIDTH;
		if (amt != 0) {
			if (Tile.TILE_WIDTH <= maxScroll && Tile.TILE_WIDTH >= minScroll) {
				Tile.TILE_WIDTH += amt;
				if (Tile.TILE_WIDTH > maxScroll) 
					Tile.TILE_WIDTH = maxScroll;
				else if (Tile.TILE_WIDTH < minScroll) 
					Tile.TILE_WIDTH = minScroll;
				else {
					float xPercent = (c.x+sw/2f)/(getWidth()*oldWidth);
					float yPercent = (c.y+sh/2f)/(getHeight()*oldWidth);
					c.x = xPercent*getWidth()*Tile.TILE_WIDTH-sw/2;
					c.y = yPercent*getHeight()*Tile.TILE_WIDTH-sh/2;
				}
			}
		}

	}

	public static List<Entity> getEntites() {
		return entities;
	}

	public static void addEntity(Entity e) {
		newEntities.add(e);
	}

	public static Tile[][] getTiles() {
		return tiles;
	}

	public static int getWidth() {
		return tiles[0].length;
	}

	public static int getHeight() {
		return tiles.length;
	}

	public static List<Building> getBuildings() {
		return buildings;
	}

	public static void addBuilding(Building b) {
		newBuildings.add(b);
	}
	
	public Player getMainPlayer() {
		return player;
	}
}
