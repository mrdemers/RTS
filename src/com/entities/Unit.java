package com.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.Camera;
import com.Player;
import com.World;
import com.pathfinding.Path;
import com.pathfinding.PathNode;
import com.pathfinding.Pathfinder;
import com.tiles.Tile;
import com.utilities.MathUtilities;

public class Unit extends Entity implements Collidable {
	Player owner;
	private boolean selected;
	protected Vector2f target;
	protected Unit targetEnemy;
	private Path pathToFollow;
	private Animation[] walkingAnimation;
	private Animation[] attackAnimation;
	private int currentAnimation;
	private int[] directions = { 270, 315, 0, 45, 90, 135, 180, 225, 360 };
	public boolean idle;
	public int type;
	
	// Attack variables
	protected int attackPower;
	protected int defense;
	protected int health;
	protected int maxHealth;
	protected float attackCooldown;
	protected float maxCooldown = 1;
	protected float hurtTime;
	private float speed = 20; // 20 by default, can be changed for other units
	public int visibilityRange = 10;

	public Unit(float x, float y, float width, float height, Player owner) {
		super(x, y, width, height);
		this.owner = owner;
		owner.addUnit(this);
		target = new Vector2f(-1, -1);
		radius = .25f;

		// Initializes the animations
		walkingAnimation = new Animation[8];// Goes in order: up, upright,
											// right,...
		attackAnimation = new Animation[8];
		initializeAnimation();
		currentAnimation = 0;
		
		pathToFollow = new Path(null);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		super.update(gc, delta);
		if (x-radius < 0) x = radius;
		if (x+radius > World.getWidth()) x = World.getWidth()-radius;
		if (y-radius < 0) y = radius;
		if (y+radius > World.getHeight()) y = World.getHeight()-radius;
		
		// Decrement all timer variables
		attackCooldown -= delta * .001;
		hurtTime -= delta * .001f;
		if (attackCooldown > 0) {
			attackAnimation[currentAnimation].update(delta);
		}
		
		// Check for collisions with world entities
		for (Entity e : World.getEntites()) {
			if (e instanceof Collidable) {
				if (((Collidable) e).collidesWith(this)) {
					onCollide((Collidable) e);
				}
			}
		}

		// Check if the animation needs to update/change
		if (acceleration.length() > .1f) {
			float dir = (float) acceleration.getTheta();
			setDirection(dir);
			walkingAnimation[currentAnimation].update(delta);
		} else {
			walkingAnimation[currentAnimation].setCurrentFrame(5);
		}

		// Friction forces
		int friction = 80;
		acceleration.x *= (friction - delta) / 100f;
		acceleration.y *= (friction - delta) / 100f;
		
		if (targetEnemy == null && target.x == -1 && target.y == -1) {
			idle = true;
		} else {
			idle = false;
		}
		
		// Check if following a target enemy
		if (targetEnemy != null) {
			if (!targetEnemy.isAlive()) {
				targetEnemy = null;
			} else {
				float dist = dist(targetEnemy);
				if (dist > radius * 3f + targetEnemy.radius) {
					target.set(targetEnemy.x, targetEnemy.y);
					pathToFollow.clear();
				} else {
					resetTarget();
					setDirection((float)Math.toDegrees(Math.atan2(targetEnemy.y-y, targetEnemy.x-x)));
					attack(targetEnemy);
				}
			}
		}
		//Check if following a path
		if (target.x != -1 && target.y != -1) {
			if (pathToFollow.isEmpty()) {
				float dist = MathUtilities.distance(x, y, target.x, target.y);
				if (dist < 1) {
					resetTarget();
				}
				float dir = (float)Math.atan2(y-target.y, x-target.x);
				acceleration.x = -(float) Math.cos(dir) * delta * .01f * speed;
				acceleration.y = -(float) Math.sin(dir) * delta * .01f * speed;
			} else {
				PathNode node = pathToFollow.getCurrentNode();
				float distX = node.getX() + .5f - x;
				float distY = node.getY() + .5f - y;
				float dist = (float) Math.sqrt(distX * distX + distY * distY);
				if (dist < .2) {
					if (pathToFollow.hasNext()) {
						pathToFollow.next();
					} else {
						resetTarget();
					}
				}
				float dir = (float) Math.atan2(distY, distX);
				acceleration.x = (float) Math.cos(dir) * delta * .01f * speed;
				acceleration.y = (float) Math.sin(dir) * delta * .01f * speed;
			}
		}
	}

	

	@Override
	public void render(GameContainer gc, Graphics g, Camera c) {
		float xDraw = (x - width / 2) * Tile.TILE_WIDTH - c.getX();
		float yDraw = (y - height) * Tile.TILE_WIDTH - c.getY();
		
		//Draw the circle that shows the unit is selected
		if (selected) {
			g.setColor(new Color(0, 200, 255, 180));
			g.fillOval(xDraw, yDraw + (height - radius * 2 / 3) * Tile.TILE_WIDTH, radius * 2 * Tile.TILE_WIDTH, radius * Tile.TILE_WIDTH);
		}
		
		//Draws the current animation
		Image currentImage = null;
		if (attackCooldown > 0 && !attackAnimation[currentAnimation].isStopped()) {
			currentImage = attackAnimation[currentAnimation].getCurrentFrame();
		} else {
			currentImage = walkingAnimation[currentAnimation].getCurrentFrame();
		}
		Color imageColor = Color.white;
		if (hurtTime > 0) imageColor = Color.red;
		float drawWidth = xDraw + width * Tile.TILE_WIDTH;
		float drawHeight = yDraw + height * Tile.TILE_WIDTH;
		g.drawImage(currentImage, xDraw, yDraw, drawWidth, drawHeight, 0, 0, currentImage.getWidth(), currentImage.getHeight(), imageColor);

		// Draws a health bar
		if (health < maxHealth) {
			Color col = new Color(0, 230, 100);
			if (health < maxHealth / 2) {
				col = Color.yellow;
				if (health < maxHealth / 5) {
					col = Color.red;
				}
			}
			g.setColor(col);
			float percentage = (float) health / maxHealth;
			g.fillRect(xDraw, yDraw - 5, (width * percentage) * Tile.TILE_WIDTH, 5);
			g.setColor(Color.black);
			g.drawRect(xDraw, yDraw - 5, width * Tile.TILE_WIDTH, 5);
		}
	}

	@Override
	public boolean collidesWith(Collidable other) {
		if (other instanceof Entity) {
			Entity e = (Entity) other;
			float dist = dist(e);
			return dist < radius + e.radius;
		}
		return false;
	}

	@Override
	public void onCollide(Collidable other) {
		if (other instanceof Unit) {
			// Colliding units, push each other away
			Unit u = (Unit) other;
			if (u.getOwner() != this.getOwner()) return;
			float dist = dist(u);
			float dir = dir(u);
			float scale = 1.0f - dist / (width / 2 + u.width / 2);
			scale *= .2;
			acceleration.x += (float) (Math.cos(dir) * scale);
			acceleration.y += (float) (Math.sin(dir) * scale);
			u.acceleration.x += -(float) (Math.cos(dir) * scale);
			u.acceleration.y += -(float) (Math.sin(dir) * scale);
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean setTargetLocation(float x, float y) {
		return setTargetLocation(x, y, true);
	}
	
	public boolean setTargetLocation(float x, float y, boolean path) {
		if (path) {
			target.set(x, y);
			pathToFollow = Pathfinder.findPath(World.getTiles(), (int) this.x, (int) this.y, (int) x, (int) y);
			if (pathToFollow.isEmpty()) return false;
			return true;
		} else {
			target.set(x, y);
			return false;
		}
	}

	public void setTargetUnit(Unit u) {
		targetEnemy = u;
		if (u == null) return;
		target.set(u.x, u.y);
		//pathToFollow = Pathfinder.findPath(World.getTiles(), (int) x, (int) y, (int) u.x, (int) u.y);
	}
	
	/**
	 * Sets the direction the unit is facing to
	 * decide which animation to use
	 * @param dir
	 */
	private void setDirection(float dir) {
		if (dir < 0) dir += 360;
		float closest = 10000; // Bigger than the angle could possibly be
		for (int a = 0; a < directions.length; a++) {
			float b = Math.abs(directions[a] - (int) dir);
			if (b < closest) {
				closest = b;
				currentAnimation = a;
				if (a >= walkingAnimation.length) // Need the extra angle measurment(360) because
				currentAnimation = 2;             // it is closer than 0
			}
		}
	}

	public void attack(Unit enemy) {
		if (attackCooldown <= 0) {
			attackAnimation[currentAnimation].restart();
			enemy.hurt(attackPower);
			if (!enemy.isAlive()) 
				targetEnemy = null;
			attackCooldown = maxCooldown;
		}
	}

	public void hurt(int damage) {
		this.health -= damage;
		if (health <= 0) {
			isAlive = false;
		} else {
			hurtTime = .2f;
		}
	}

	@Override
	public boolean intersectedByPoint(float x, float y) {
		return x > this.x - this.width/2f && x < this.x + this.width/2f && y < this.y && y > this.y - this.height;
	}
	
	public Player getOwner() {
		return owner;
	}

	private void initializeAnimation() {
		int time = 30;
		// Up
		walkingAnimation[0] = new Animation(false);
		attackAnimation[0] = new Animation(false);
		for (int i = 0; i < 10; i++) {
			walkingAnimation[0].addFrame(sheet.getSprite(i, 3), time);
			attackAnimation[0].addFrame(attackSheet.getSprite(i, 3), time);
		}

		// Right/Left Up
		walkingAnimation[1] = new Animation(false);
		walkingAnimation[7] = new Animation(false);
		attackAnimation[1] = new Animation(false);
		attackAnimation[7] = new Animation(false);
		for (int i = 0; i < 9; i++) {
			walkingAnimation[1].addFrame(sheet.getSprite(i, 2), time);
			walkingAnimation[7].addFrame(sheet.getSprite(i, 2).getFlippedCopy(true, false), time);
			attackAnimation[1].addFrame(attackSheet.getSprite(i, 2), time);
			attackAnimation[7].addFrame(attackSheet.getSprite(i, 2).getFlippedCopy(true, false), time);
		}

		// Right/Left
		walkingAnimation[2] = new Animation(false);
		walkingAnimation[6] = new Animation(false);
		attackAnimation[2] = new Animation(false);
		attackAnimation[6] = new Animation(false);
		for (int i = 0; i < 10; i++) {
			walkingAnimation[2].addFrame(sheet.getSprite(i, 0), time);
			walkingAnimation[6].addFrame(sheet.getSprite(i, 0).getFlippedCopy(true, false), time);
			attackAnimation[2].addFrame(attackSheet.getSprite(i, 0), time);
			attackAnimation[6].addFrame(attackSheet.getSprite(i, 0).getFlippedCopy(true, false), time);
		}

		// Right/Left down
		walkingAnimation[3] = new Animation(false);
		walkingAnimation[5] = new Animation(false);
		attackAnimation[3] = new Animation(false);
		attackAnimation[5] = new Animation(false);
		for (int i = 0; i < 10; i++) {
			walkingAnimation[3].addFrame(sheet.getSprite(i, 1), time);
			walkingAnimation[5].addFrame(sheet.getSprite(i, 1).getFlippedCopy(true, false), time);
			attackAnimation[3].addFrame(attackSheet.getSprite(i, 1), time);
			attackAnimation[5].addFrame(attackSheet.getSprite(i, 1).getFlippedCopy(true, false), time);
		}

		// Down
		walkingAnimation[4] = new Animation(false);
		attackAnimation[4] = new Animation(false);
		for (int i = 0; i < 10; i++) {
			walkingAnimation[4].addFrame(sheet.getSprite(i, 4), time);
			attackAnimation[4].addFrame(attackSheet.getSprite(i, 4), time);
		}
		
		for (int i = 0; i < 8; i++) {
			walkingAnimation[i].setPingPong(true);
			attackAnimation[i].setLooping(false);
		}
	}
	
	private void resetTarget() {
		target.x = -1;
		target.y = -1;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public String toString() {
		return "ID: " + getID() + ", Type: " + type + ", Position:{" + x + ", " + y + "}";
	}
}
