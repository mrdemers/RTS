package com;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.entities.Entity;
import com.entities.Unit;
import com.entities.UnitCluster;
import com.gui.GUIBar;
import com.tiles.Tile;
import com.utilities.ShadowMask;

public class Player {
	public UnitCluster myUnits;
	public UnitCluster selectedUnits;
	private Vector2f startDrag, stopDrag;
	private boolean dragging = false;
	protected int color;
	public int[] visMap; // Does the FOW, 0 for not seen, 1 for has seen, 3 for
						 // seen now
	private boolean useFOW = true;

	public Player() {
		myUnits = new UnitCluster();
		selectedUnits = new UnitCluster();
		startDrag = new Vector2f(0, 0);
		stopDrag = new Vector2f(0, 0);
		this.color = 0x0000ff;
		visMap = new int[World.getWidth() * World.getHeight()];
	}

	private void castRay(int x0, int y0, int x1, int y1, int range) {
		double xDistance = x0 - x1;
		double yDistance = y0 - y1;
		int xLast = x0, yLast = y0;
		Tile[][] tiles = World.getTiles();
		for (int i = 0; i < range; i++) {
			double xa = xDistance * i / range;
			double ya = yDistance * i / range;
			if (xa * xa + ya * ya > range * range) return;
			
			int x = (int) (x0 + xa);
			int y = (int) (y0 + ya);
			int xDir = x - xLast;
			int yDir = y - yLast;
			xLast = x;
			yLast = y;
			int dir = 0;
			if (xDir < 0 && yDir < 0) dir += 1;
			else if (xDir > 0 && yDir < 0) dir += 2;
			else if (xDir < 0 && yDir > 0) dir += 4;
			else if (xDir > 0 && yDir > 0) dir += 8;
			if (x >= 0 && y >= 0 && x < World.getWidth() && y < World.getHeight()) {
				if (dir > 0) {
					if (!tiles[y][x].passable()) visMap[x + y * World.getWidth()] = 3;
					if (dir == 1 && !tiles[y+1][x].passable() && !tiles[y][x+1].passable())
						return;
					if (dir == 2 && !tiles[y][x-1].passable() && !tiles[y+1][x].passable())
						return;
					if (dir == 4 && !tiles[y-1][x].passable() && !tiles[y][x+1].passable())
						return;
					if (dir == 8 && !tiles[y-1][x].passable() && !tiles[y][x-1].passable())
						return;
				}
				visMap[x + y * World.getWidth()] = 3;
				if (!tiles[y][x].passable()) return;
			}
		}
	}

	boolean wasPressed, isPressed;
	int clickTime = 0;

	public void update(GameContainer gc, int delta, World world) throws SlickException {
		Input input = gc.getInput();
		int mx = input.getMouseX();
		int my = input.getMouseY();
		wasPressed = isPressed;
		isPressed = Mouse.isButtonDown(0);
		if (isPressed && wasPressed) clickTime++;
		else clickTime = 0;

		boolean clicked = input.isMousePressed(0);
		if (clickTime > 80 && my < gc.getHeight() - GUIBar.HEIGHT) {
			dragging = true;
		}

		if (clicked) {
			startDrag.x = mx + world.c.x;
			startDrag.y = my + world.c.y;
		}

		for (int i = 0; i < visMap.length; i++) {
			visMap[i] &= 1;
		}
		// Clean up myunits array
		for (int i = 0; i < myUnits.size();) {
			if (!myUnits.get(i).isAlive) myUnits.remove(i);
			else {
				// Also update the visibility map
				Unit u = myUnits.get(i);
				if (clicked && u.intersectedByPoint((mx + world.c.x) / Tile.TILE_WIDTH, (my + world.c.y) / Tile.TILE_WIDTH)) {
					if (!(input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL))) {
						for (Unit a : selectedUnits) {
							a.setSelected(false);
						}
						selectedUnits.clear();
					}
					selectedUnits.add(u);
					u.setSelected(true);
				}
				int range = u.visibilityRange;
				int xx = (int) u.x;
				int yy = (int) u.y;
				for (int j = 0; j < range * 2; j++) {
					castRay(xx, yy, xx - range, yy - range + j, range);
					castRay(xx, yy, xx + range, yy - range + j, range);
					castRay(xx, yy, xx - range + j, yy - range, range);
					castRay(xx, yy, xx - range + j, yy + range, range);
				}
				i++;
			}
		}

		/*
		 * Selects the units you have dragged the mouse over
		 */
		if (dragging) {
			stopDrag.x = mx + world.c.x;
			stopDrag.y = my + world.c.y;
			if (!input.isMouseButtonDown(0)) {
				world.c.clearTarget();
				if (!input.isKeyDown(Input.KEY_LCONTROL)) {
					for (Unit u : selectedUnits) {
						u.setSelected(false);
					}
					selectedUnits.clear();
				}
				dragging = false;
				float x0 = Math.min(startDrag.x, stopDrag.x) / Tile.TILE_WIDTH;
				float y0 = Math.min(startDrag.y, stopDrag.y) / Tile.TILE_WIDTH;
				float width = Math.abs(stopDrag.x - startDrag.x) / Tile.TILE_WIDTH;
				float height = Math.abs(stopDrag.y - startDrag.y) / Tile.TILE_WIDTH;
				for (Unit u : myUnits) {
					if (u.x > x0 && u.x < x0 + width && u.y > y0 && u.y < y0 + height) {
						selectedUnits.add(u);
						u.setSelected(true);
					}
				}
			}
		}

		Iterator<Unit> i = selectedUnits.iterator();
		boolean pressed = input.isMousePressed(1) && input.getAbsoluteMouseY()<gc.getHeight()-200;
		boolean checked = false, canGo = false;;
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive) i.remove();
			/*
			 * Sends selected units to target when right mouse button pressed
			 */
			else if (pressed) {
				if (checked && !canGo) {
					u.setTargetLocation((mx + world.c.x) / Tile.TILE_WIDTH, (my + world.c.y) / Tile.TILE_WIDTH, false);
				}
				List<Entity> worldEntities = World.getEntites();
				Unit enemy = null;
				for (Entity e : worldEntities) {
					if (e instanceof Unit) {
						if (((Unit) e).getOwner() != this) {
							if (e.intersectedByPoint((float) (mx + world.c.x) / Tile.TILE_WIDTH, (float) (my + world.c.y) / Tile.TILE_WIDTH)) {
								enemy = (Unit) e;
							}
						}
					}
				}
				if (enemy != null) {
					u.setTargetUnit(enemy);
				} else if (!checked || canGo) {
					canGo = u.setTargetLocation((mx + world.c.x) / Tile.TILE_WIDTH, (my + world.c.y) / Tile.TILE_WIDTH);
					u.setTargetUnit(null);
				}
			}
		}
	}
	Color grayCol = new Color(50, 50, 50, 190);
	public void render(GameContainer gc, Graphics g, Camera c) throws SlickException {
		if (dragging) {
			float x0 = Math.min(startDrag.x, stopDrag.x);
			float y0 = Math.min(startDrag.y, stopDrag.y);
			float width = Math.abs(stopDrag.x - startDrag.x);
			float height = Math.abs(stopDrag.y - startDrag.y);
			g.setColor(new Color(0, 255, 216, 255));
			g.drawRect(x0 - c.x, y0 - c.y, width, height);
			g.setColor(new Color(0, 200, 200, 150));
			g.fillRect(x0 - c.x, y0 - c.y, width, height);
		}
		
		// Draw Fog of War
		if (!useFOW) return;
		
		for (int y = c.getY() / Tile.TILE_WIDTH - 1; y < (c.getY() + gc.getHeight()) / Tile.TILE_WIDTH + 1; y++) {
			if (y < 0 || y >= World.getHeight()) continue;
			for (int x = c.getX() / Tile.TILE_WIDTH - 1; x < (c.getX() + gc.getWidth()) / Tile.TILE_WIDTH + 1; x++) {
				if (x < 0 || x >= World.getWidth()) continue;

				int fogSlot = 0;
				int blackSlot = 0;
				int w = World.getWidth();
				
				if (x >= 1 && y >= 1 && x < w - 2 && y < World.getHeight() - 2) {
					if (visMap[(x-1) + (y-1) * w] > 1) {
						if (visMap[(x-1) + y*w] > 1 && visMap[(x)+(y-1)*w] > 1)
							fogSlot += 1;
					}
					if (visMap[(x + 1) + (y-1) * w] > 1) {
						if (visMap[(x) + (y-1)*w] > 1 && visMap[(x+1)+(y)*w] > 1)
							fogSlot += 2;
					}
					if (visMap[(x-1) + (y + 1) * w] > 1) {
						if (visMap[(x-1) + (y)*w] > 1 && visMap[(x)+(y+1)*w] > 1)
							fogSlot += 4;
					}
					if (visMap[(x + 1) + (y + 1) * w] > 1) {
						if (visMap[(x+1) + (y)*w] > 1 && visMap[(x)+(y+1)*w] > 1)
							fogSlot += 8;
					}
					
					if (visMap[(x-1) + (y-1) * w] > 0) {
						if (visMap[(x-1) + y*w] > 0 && visMap[(x)+(y-1)*w] > 0)
							blackSlot += 1;
					}
					if (visMap[(x + 1) + (y-1) * w] > 0) {
						if (visMap[(x) + (y-1)*w] > 0 && visMap[(x+1)+(y)*w] > 0)
							blackSlot += 2;
					}
					if (visMap[(x-1) + (y + 1) * w] > 0) {
						if (visMap[(x-1) + (y)*w] > 0 && visMap[(x)+(y+1)*w] > 0)
							blackSlot += 4;
					}
					if (visMap[(x + 1) + (y + 1) * w] > 0) {
						if (visMap[(x+1) + (y)*w] > 0 && visMap[(x)+(y+1)*w] > 0)
							blackSlot += 8;
					}
					if (visMap[x+y*w]==0) blackSlot = 0;
					else if (visMap[x+y*w]==1) fogSlot=0;
					if (fogSlot == 15) continue;
				} else {
					if (x == 0) {
						if (y == 0) {
							if (visMap[(x+1)+(y+1)*w] > 1) fogSlot+=8;
							if (visMap[(x+1)+(y+1)*w] > 0) blackSlot+=8;
						} else if (y==World.getHeight()) {
							if (visMap[(x+1)+(y-1)*w] > 1) fogSlot+=2;
							if (visMap[(x+1)+(y-1)*w] > 0) blackSlot+=2;							
						} else {
							if (visMap[x+1+(y-1)*w] > 1 && visMap[x+1+y*w]>1) fogSlot+= 2;
							if (visMap[x+1+(y+1)*w] > 1 && visMap[x+1+y*w]>1) fogSlot+=8;
							if (visMap[x+1+(y-1)*w] > 0 && visMap[x+1+y*w]>0) blackSlot+= 2;
							if (visMap[x+1+(y+1)*w] > 0 && visMap[x+1+y*w]>0) blackSlot+=8;
						}
					} else if (y==0) {
						if (x == World.getWidth()) {
							if (visMap[(x-1)+(y+1)*w] > 1) fogSlot+=4;
							if (visMap[(x-1)+(y+1)*w] > 0) blackSlot+=4;
						} else {
							if (visMap[x-1+(y+1)*w] > 1 && visMap[x+(y+1)*w]>1) fogSlot+= 4;
							if (visMap[x+1+(y+1)*w] > 1 && visMap[x+(y+1)*w]>1) fogSlot+=8;
							if (visMap[x-1+(y+1)*w] > 0 && visMap[x+(y+1)*w]>0) blackSlot+= 4;
							if (visMap[x+1+(y+1)*w] > 0 && visMap[x+(y+1)*w]>0) blackSlot+=8;
						}
					}
				}
				float x0 = x * Tile.TILE_WIDTH - c.x;
				float y0 = y * Tile.TILE_WIDTH - c.y;
				ShadowMask sm = ShadowMask.getInstance();
				if (blackSlot > 0)
					g.drawImage(sm.getMask(fogSlot), x0, y0, x0 + Tile.TILE_WIDTH, y0 + Tile.TILE_WIDTH, 0, 0, 50, 50, grayCol);
				
				if (blackSlot == 15) continue;
				g.drawImage(sm.getMask(blackSlot), x0, y0, x0 + Tile.TILE_WIDTH, y0 + Tile.TILE_WIDTH, 0, 0, 50, 50, Color.black);
			}
		}
	}

	public void addUnit(Unit u) {
		myUnits.add(u);
	}

	public int getColor() {
		return color;
	}
}
