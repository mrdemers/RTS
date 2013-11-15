package com.entities;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import com.Player;
import com.World;

public class Knight extends Unit {
	private int agressiveRange;
	public Knight(float x, float y, Player owner) {
		super(x, y, .5f, .75f, owner);
		maxHealth = health = 30;
		agressiveRange = 5;
		attackPower = 2;
		type = 2;
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		super.update(gc, delta);
		
		if (targetEnemy == null && target.x == -1 && target.y == -1) {
			List<Entity> worldEntities = World.getEntites();
			Unit closestTarget = null;
			for (Entity e : worldEntities) {
				if (e instanceof Unit) {
					if (((Unit)e).getOwner() != this.getOwner()) {
						Unit u = (Unit)e;
						float dist = dist(u);
						if (dist < agressiveRange + u.radius && dist < dist(closestTarget)) {
							closestTarget = u;
						}
					}
				}
				if (closestTarget != null) {
					setTargetUnit(closestTarget);
				}
			}
		}
	}
}
