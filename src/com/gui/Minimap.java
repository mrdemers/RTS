package com.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;

import com.Player;
import com.World;
import com.entities.Entity;
import com.entities.Unit;
import com.tiles.Tile;

public class Minimap {
	Image mapImage;
	int xPosition, yPosition, width, height;
	private float posX, posY;
	boolean hoverOver;
	List<Unit> units = new ArrayList<Unit>();
	ImageBuffer mapBuffer;
	ImageBuffer shadowBuffer;

	public Minimap(GameContainer gc) {
		width = 150;
		height = 150;
		xPosition = 10;
		yPosition = gc.getHeight() - height - 10;
		Tile[][] tiles = World.getTiles();
		mapBuffer = new ImageBuffer(tiles[0].length, tiles.length);
		shadowBuffer = new ImageBuffer(tiles[0].length, tiles.length);
		for (int y = 0; y < tiles.length; y++) {
			for (int x = 0; x < tiles[0].length; x++) {
				int col = tiles[y][x].getColor();
				mapBuffer.setRGBA(x, y, (col >> 16) & 0xff, (col >> 8) & 0xff, col & 0xff, 255);
				shadowBuffer.setRGBA(x, y, 0, 0, 0, 255);
			}
		}
		mapImage = mapBuffer.getImage();
	}

	public void update(GameContainer gc, int delta, World world) {
		int mx = Mouse.getX();
		int my = gc.getHeight() - Mouse.getY();
		Player p = world.getMainPlayer();
		for (int y = 0; y < shadowBuffer.getHeight(); y++) {
			for (int x = 0; x < shadowBuffer.getWidth(); x++) {
				int vis = p.visMap[x+y*shadowBuffer.getWidth()]; 
				if (vis > 0) {
					if (vis == 1) shadowBuffer.setRGBA(x, y, 50, 50, 50, 190);
					else shadowBuffer.setRGBA(x, y, 0, 0, 0, 0);
				} else {
					shadowBuffer.setRGBA(x, y, 0, 0, 0, 255);
				}
			}
		}
		if (mx > xPosition && mx < xPosition + width && my > yPosition && my < yPosition + height) {
			if (!hoverOver && Mouse.isButtonDown(0)) {
				// Do nothing
			} else {
				posX = (float)(mx-xPosition)/width;
				posY = (float)(my-yPosition)/height;
				hoverOver = true;
			}
		} else if (!Mouse.isButtonDown(0)){
			hoverOver = false;
		}

		if (Mouse.isButtonDown(0) && hoverOver) {
			world.c.setPosition(posX*World.getWidth()*Tile.TILE_WIDTH-gc.getWidth()/2, posY*World.getHeight()*Tile.TILE_WIDTH-gc.getHeight()/2);
			world.c.clearTarget();
		}

		for (int i = 0; i < units.size();) {
			if (!units.get(i).isAlive) {
				units.remove(i);
			} else {
				i++;
			}
		}

		List<Entity> entities = World.getEntites();
		for (Entity e : entities) {
			if (e instanceof Unit) {
				if (!units.contains((Unit) e)) {
					units.add((Unit) e);
				}
			}
		}
	}

	public void render(GameContainer gc, Graphics g, World world) {
		g.drawImage(mapImage, xPosition, yPosition, xPosition + width, yPosition + height, 0, 0, mapImage.getWidth(), mapImage.getHeight());
		Image shadow = shadowBuffer.getImage();
		g.drawImage(shadow, xPosition, yPosition, xPosition + width, yPosition + height, 0, 0, mapImage.getWidth(), mapImage.getHeight());
		try {
			shadow.destroy();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		for (Unit u : units) {
			g.setColor(new Color(u.getOwner().getColor()));
			int x0 = xPosition + (int)((u.x-u.width*2)/World.getWidth()*width);
			int y0 = yPosition + (int)((u.y-u.height*2)/World.getHeight()*height);
			g.fillRect(x0, y0, u.width * 4, u.height * 4);
		}
		g.setClip(xPosition, yPosition, width, height);
		g.setColor(Color.white);
		int x1 = xPosition + (int)(world.c.x/Tile.TILE_WIDTH/World.getWidth()*width);
		int y1 = yPosition + (int)(world.c.y/Tile.TILE_WIDTH/World.getHeight()*height);
		int x2 = gc.getWidth() / Tile.TILE_WIDTH;
		int y2 = gc.getHeight() / Tile.TILE_WIDTH;
		g.drawRect(x1, y1, x2, y2);
		g.clearClip();
	}
}
