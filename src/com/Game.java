package com;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.gui.GUIBar;

public class Game extends BasicGame {
	private static final String GAME_NAME = "RTS";
	public static int windowWidth = 1280;
	public static int windowHeight = 720;
	private static AppGameContainer agc;
	private Image[] cursor;
	public static int currentCursor;
	public static final int NORMAL_CURSOR = 0;
	public static final int CAMERA_MOVE_CURSOR = 1;
	

	private World world;
	private Camera camera;
	private GUIBar guiBar;

	public Game(String title) {
		super(title);
	}

	public static void main(String[] args) {			
		try {
			agc = new AppGameContainer(new Game(GAME_NAME));
			agc.setDisplayMode(windowWidth, windowHeight, false);
			agc.setMouseGrabbed(true);
			agc.setAlwaysRender(true);
			agc.setMinimumLogicUpdateInterval(1);
			agc.setMaximumLogicUpdateInterval(1);
			agc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		cursor = new Image[10];
		cursor[NORMAL_CURSOR] = Art.loadImage("cursor.png");
		cursor[CAMERA_MOVE_CURSOR] = Art.loadImage("cursor-cameraMove.png");
		currentCursor = NORMAL_CURSOR;
		
		world = new World();
		camera = new Camera(0, 0, world.worldWidth,
				world.worldHeight);
		world.c = camera;
		Mouse.setCursorPosition(windowWidth / 2, windowHeight / 2);
		guiBar = new GUIBar(gc, world.getMainPlayer());
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			gc.exit();
		}
		currentCursor = NORMAL_CURSOR;
		world.update(gc, delta);
		camera.update(gc, delta);
		guiBar.update(gc, delta, world);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		world.render(gc, g);
		guiBar.render(gc, g, world);
		
		//Draws the cursor
		int xPos = Mouse.getX();
		int yPos = gc.getHeight() - Mouse.getY();
		if (currentCursor == CAMERA_MOVE_CURSOR) {
			boolean up = false, down = false, left = false, right = false;
			if (xPos > gc.getWidth()-10) {
				right = true;
			}
			if (xPos < 10) {
				left = true;
			}
			if (yPos < 10) {
				up = true;
			}
			if (yPos > gc.getHeight()-10) {
				down = true;
			}
			
			if (right && up) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(315);
				xPos -= 25;
			} else if (right && down) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(45);
				xPos -= 25;
				yPos -= 25;
			} else if (left && down) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(135);
				yPos -= 25;
			} else if (left && up) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(225);
			} else if (up) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(270);				
			} else if (down) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(90);
				yPos-=25;
			} else if (right) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(0);
				xPos -= 25;
			} else if (left) {
				cursor[CAMERA_MOVE_CURSOR].setRotation(180);				
			}
		}
		g.drawImage(cursor[currentCursor], xPos, yPos);
	}
}
