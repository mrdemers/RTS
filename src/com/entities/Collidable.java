package com.entities;

public interface Collidable{
	public boolean collidesWith(Collidable other);
	public void onCollide(Collidable other);
}
