package com.buildings;

import java.util.HashMap;

import com.Art;
import com.entities.Harvester;
import com.entities.Unit;
import com.resources.Resource;

public class Stockpile extends Building {
private HashMap<Resource, Integer> resources;
	
	public Stockpile(int x, int y){
		super(x, y, 1, 1, 100);
		image= Art.loadImage("tempStockpile.png");
		resources= new HashMap<Resource, Integer>();
		
	}
	
	public void addResource(Resource type){
		
		if(resources.containsKey(type)){
			int temp=resources.get(type);
			temp++;
			resources.remove(type);
			resources.put(type, temp);
		}else{
			resources.put(type, 1);
		}
		
		System.out.println("I now have "+resources.get(type)+" of "+type);
		
	}
	
	//two-in-one checkFor and return method
	public boolean removeResource(Resource type){
		
		if(resources.containsKey(type)){
			int temp=resources.get(type);
			temp--;
			resources.remove(type);
			resources.put(type, temp);
			System.out.println("I now have "+resources.get(type)+" of "+type);
			return true;
		}else{
			return false;
		}
		
	}
	
	public boolean hasAny(Resource r){
		
		if(resources.containsKey(r)){
			return (resources.get(r)>0);
		}else{
			return false;
		}
		
	}
	
	public void store(Resource type){
		addResource(type);
	}

	@Override
	public void doWork(Unit user) {

		//if you are a harvester, drop your stuff here
		if(user instanceof Harvester){
			Harvester temp= (Harvester)user;
			addResource(temp.currentResource);
			temp.resourcesCollected--;
		}
		
	}
}
