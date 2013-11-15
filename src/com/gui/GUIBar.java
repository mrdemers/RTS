package com.gui;

import java.util.Collections;
import java.util.Comparator;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.Art;
import com.Player;
import com.World;
import com.entities.Unit;

public class GUIBar {
	Image bar = Art.loadImage("guiBar.png");
	Minimap minimap;
	Player player;
	public static final int HEIGHT = 200;
	private Image[] unitPictures;
	
	public GUIBar(GameContainer gc, Player p) {
		minimap = new Minimap(gc);
		player = p;
		unitPictures = new Image[10];
		try {
			unitPictures[0] = new Image("harvesterPicture.png");
			unitPictures[1] = new Image("knightPicture.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	int width = 40;
	boolean wasRightMouseDown = false;
	public void update(GameContainer gc, int delta, World world) throws SlickException {
		minimap.update(gc, delta, world);
		Input input = gc.getInput();
		if (input.isMouseButtonDown(0)) {
			for (int i = 0; i < player.selectedUnits.size(); i++) {
				int mx = input.getAbsoluteMouseX();
				int my = input.getAbsoluteMouseY();
				
				if (mx > (i%10) * (width+5)+215 && mx < i*(width+5)+215+width && my > (i/10)*(width+20)+65+gc.getHeight()-HEIGHT && my < (i/10)*(width+20)+65+gc.getHeight()-HEIGHT+width) {
					Unit unit = player.selectedUnits.get(i);
					for (Unit u : player.selectedUnits) {
						u.setSelected(false);
					}
					player.selectedUnits.clear();
					unit.setSelected(true);
					player.selectedUnits.add(unit);
				}
			}
		}
		if (input.isMouseButtonDown(1) && !wasRightMouseDown) {
			for (int i = 0; i < player.selectedUnits.size(); i++) {
				int mx = input.getAbsoluteMouseX();
				int my = input.getAbsoluteMouseY();
				
				if (mx > (i%10) * (width+5)+215 && mx < i*(width+5)+215+width && my > (i/10)*(width+20)+65+gc.getHeight()-HEIGHT && my < (i/10)*(width+20)+65+gc.getHeight()-HEIGHT+width) {
					player.selectedUnits.remove(i).setSelected(false);
				}
			}
		}
		wasRightMouseDown = input.isMouseButtonDown(1);
		Collections.sort(player.selectedUnits, new Comparator<Unit>() {
			@Override
			public int compare(Unit u1, Unit u2) {
				return u2.type - u1.type;
			}
		});
	}
	
	public void render(GameContainer gc, Graphics g, World world) throws SlickException {
		g.drawImage(bar, 0, gc.getHeight()-HEIGHT, gc.getWidth(), gc.getHeight(), 0, 0, bar.getWidth(), bar.getHeight());
		for (int i = 0; i < player.selectedUnits.size(); i++) {
			Unit u = player.selectedUnits.get(i);
			int x = i % 10;
			int y = i / 10;
			int yDraw = gc.getHeight()-HEIGHT + y * (width+20) + 65;
			int xDraw = x * (width+5) + 215;
			g.drawImage(unitPictures[u.type-1], xDraw, yDraw, xDraw + width, yDraw + width, 0 , 0, unitPictures[u.type-1].getWidth(), unitPictures[u.type-1].getHeight());
			Color col = Color.green;
			if (u.getHealth() < u.getMaxHealth() / 2) {
				col = Color.yellow;
				if (u.getHealth() < u.getMaxHealth() / 5) {
					col = Color.red;
				}
			}
			g.setColor(col);
			float percentage = u.getHealth()/(float)u.getMaxHealth();
			g.fillRect(xDraw, yDraw + (width+5), width*percentage, 10);
			g.setColor(Color.white);
			g.drawRect(xDraw, yDraw + (width+5), width, 10);
		}
		minimap.render(gc, g, world);
	}
}
