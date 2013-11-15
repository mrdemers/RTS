package com.entities;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.Player;
import com.World;
import com.buildings.Building;
import com.buildings.Stockpile;
import com.pathfinding.Pathfinder;
import com.resources.Resource;
import com.tiles.Tile;
import com.utilities.MathUtilities;

public class Harvester extends Unit {

	final double HARVEST_DISTANCE = 2;

	static Tile DUMMY_OPEN_TILE = new Tile(-10, -10, 0);

	// standard for the RTS, a harvester can only have one resource at a time
	public Resource currentResource = Resource.Nothing;
	public int resourcesCollected;
	Tile collectingFrom;
	Vector2f depositLocation;
	Vector2f targetLocation;
	boolean getting = false; // denotes if harvester is adding to or taking from
								// the stockpile

	public Harvester(float x, float y, Player owner) {
		super(x, y, .5f, 1.0f, owner);
		resourcesCollected = 0;
		depositLocation = new Vector2f(x, y);
		targetLocation = new Vector2f(x, y);
		collectingFrom = DUMMY_OPEN_TILE;
		maxHealth = health = 10;
		attackPower = 1;
		type = 1;
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		super.update(gc, delta);

		if (MathUtilities.distance(x, y, target.x, target.y) < HARVEST_DISTANCE) {

			harvestOrDropoff();

		}

	}

	@Override
	public boolean setTargetLocation(float x, float y) {
		boolean yes = super.setTargetLocation(x, y);

		// I'm sorry that this looks awfull
		// Instead of making the resource type storage, were going to make diff
		collectingFrom = World.getTiles()[(int) y][(int) x];

		List<Building> buildings = World.getBuildings();

		for (Building cur : buildings) {
			if (cur instanceof Stockpile && cur.getX() == x && cur.getY() == y) {
				getting = true;
			}
		}
		return yes;
	}

	public void harvestOrDropoff() {
		if (collectingFrom.getResource() != Resource.Nothing) {

			System.out.println("Harvesting...");

			if (collectingFrom.getResource() == currentResource) {
				resourcesCollected++;
				System.out.println("I am collecting " + currentResource);
				collectingFrom.onCollected();
			} else if (collectingFrom.getResource() != Resource.Nothing) {
				currentResource = collectingFrom.getResource();
				System.out.println("I am now collecting " + currentResource);
				resourcesCollected = 1;
				collectingFrom.onCollected();
			}

			if (resourcesCollected > 0) {
				Vector2f dropoff = findNearestStockpileWith(currentResource);
				if (dropoff == null) {
					dropoff = findNearestStockpile();
				}
				if (dropoff != null) {
					setTargetLocation(dropoff.x, dropoff.y);
					depositLocation = dropoff;
				}
			}
		}

		if (MathUtilities.distance(x, y, depositLocation.x, depositLocation.y) < HARVEST_DISTANCE && resourcesCollected > 0) {
			List<Building> buildings = World.getBuildings();
			if (getting) {
				System.out.println("in get mode...");
				for (Building b : buildings) {
					if (b.getX() == targetLocation.x && b.getY() == targetLocation.y) {
						System.out.println("I am getting " + currentResource);
						if (((Stockpile) (b)).removeResource(currentResource)) {
							System.out.println("I have " + resourcesCollected + " of " + currentResource);
							resourcesCollected++;
						}
					}
				}
			} else {
				for (Building b : buildings) {
					if (b.getX() == depositLocation.x && b.getY() == depositLocation.y) {
						System.out.println("I am depostiting " + currentResource);
						((Stockpile) b).store(currentResource);
					}
				}
			}
			resourcesCollected = 0;
		}

	}

	// under construction

	public Vector2f findNearestStockpileWith(Resource r) {

		List<Building> buildings = World.getBuildings();

		int bestPath=-1;
		Vector2f retVal=null;
		for (Building cur : buildings) {

			if (cur instanceof Stockpile && ((Stockpile) cur).hasAny(r)) {

				Vector2f temp = new Vector2f(cur.getX(), cur.getY());

				if(bestPath==-1||Pathfinder.findPath(World.getTiles(), (int)this.x, (int)this.y, (int)temp.x, (int)temp.y).getDistance()<bestPath){
					retVal=temp;
				}

			}

		}

		return retVal;

	}

	public Vector2f findNearestStockpile() {

		List<Building> buildings = World.getBuildings();

		int bestPath=-1;
		Vector2f retVal=null;
		for (Building cur : buildings) {

			if (cur instanceof Stockpile) {

				Vector2f temp = new Vector2f(cur.getX(), cur.getY());

					if(bestPath==-1||Pathfinder.findPath(World.getTiles(), (int)this.x, (int)this.y, (int)temp.x, (int)temp.y).getDistance()<bestPath){
						retVal=temp;
					}
															

			}

		}

		return retVal;

	}

}
