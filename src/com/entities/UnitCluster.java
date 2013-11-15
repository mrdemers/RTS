package com.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnitCluster extends ArrayList<Unit>{
	private static final long serialVersionUID = 1L;
	
	public UnitCluster() {
		super();
	}
	
	public UnitCluster(List<Unit> units) {
		super(units);
	}
	
	/**
	 * @return The width between the highest and lowest x value units
	 */
	public float getWidth() {
		float lowest = 10000, highest = -10000;
		Iterator<Unit> i = iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive()) i.remove();
			else {
				if (u.x < lowest) lowest = u.x;
				if (u.x > highest) highest = u.x;
			}
		}
		return highest - lowest;
	}
	
	/**
	 * @return The height between the highest and lowest y value units
	 */
	public float getHeight() {
		float lowest = 10000, highest = -10000;
		Iterator<Unit> i = iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive()) i.remove();
			else {
				if (u.y < lowest) lowest = u.y;
				if (u.y > highest) highest = u.y;
			}
		}
		return highest - lowest;
	}
	
	/**
	 * @return The exact center X of the group, between the highest and lowest values of x
	 */
	public float getCenterX() {
		float lowest = 1000, highest = -1000;
		Iterator<Unit> i = iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive()) i.remove();
			else {
				if (u.x < lowest) lowest = u.x;
				if (u.x > highest) highest = u.x;
			}
		}
		return (lowest+highest)/2;
	}
	
	/**
	 * @return The exact center Y of the group, between the highest and lowest values of y
	 */
	public float getCenterY() {
		float lowest = 1000, highest = -1000;
		Iterator<Unit> i = iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive()) i.remove();
			else {
				if (u.y < lowest) lowest = u.y;
				if (u.y > highest) highest = u.y;
			}
		}
		return (lowest+highest)/2;
	}
	
	/**
	 * @return The average x value of the group
	 */
	public float getMedianX() {
		float amount = 0;
		if (isEmpty()) return 0;
		Iterator<Unit> i = iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive()) i.remove();
			else {
				amount += u.x;
			}
		}
		return amount/size();
	}
	
	/**
	 * @return The average y value of the group
	 */
	public float getMedianY() {
		float amount = 0;
		if (isEmpty()) return 0;
		Iterator<Unit> i = iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (!u.isAlive()) i.remove();
			else {
				amount += u.y;
			}
		}
		return amount/size();
	}
}
