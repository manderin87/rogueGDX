package com.jayheart.dungeonGame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.jayheart.dungeonScreens.LogScreen;

public class Item{
	//Class for Items. An item has a wide variety of traits; not all are implementd yet. Not much to say about it. 
	private String name;
	public String name(){ return name; }
	
	private String description;
	public String description(){ return description; }
	public void description(String s){ this.description = s; }
	
	private List<String> types;
	public boolean isType(String type){ return types.contains(type); }
	public void addType(String type){ types.add(type); }
	public List<String> getTypes(String typeType){
		//RETURNS "typeTypes": for instance, armor will return all areas it can be euqipped on
		List<String> ret = new ArrayList<String>();
		for (String type : types){
			if (type.contains(typeType)){
				String s = "";
				for (String sub : type.split(typeType)) s += sub;
				ret.add(s);
			}
		}
		return ret;
	}
	
	private int food;
	public int food(){ return food; }
	public void food(int f){ this.food = f; }

	private int hands;
	public int hands(){ return hands; }
	public void hands(int f){ this.hands = f; }
	
	private Texture texture;
	public Texture texture() { return texture; }
	
	private int attackVal; 
	public int attackVal() { return this.attackVal; }
	public void attackVal(int AV) { this.attackVal = AV; }

	private int defVal; 
	public int defVal() { return this.defVal; }
	public void defVal(int DV) { this.defVal = DV; }
	
	private ActorJ summon;
	public ActorJ summon() { return this.summon; }
	public void summon(ActorJ a) { this.summon = a; }
	
	public void ego(boolean b) { this.ego = b; }
	public boolean ego() { return this.ego; }
	private boolean ego;
	
	public void egoCount(int b) { this.egoCount = b; }
	public int egoCount() { return this.egoCount; }
	private int egoCount;
	
	private boolean usable; //Defines usablility for ego items. An ego item is unusuable if the spirit inside is dead.
	public boolean usable() { return usable; }
	public void usable(boolean usable) { this.usable = usable; }
	
	public Item(String n, Texture t, String d){
		this.name = n;
		this.types = new ArrayList<String>();
		this.texture = t;
		this.description = d;
		this.ego = false;
		this.egoCount = -1;
		usable(true);
	}
	
	public void onConsume(ActorJ consumer){
		//Called when the item is eaten or quaffed. Defaults to food.
		consumer.AI().nutrition(consumer.AI().nutrition()+food);
	}
	
	public void onWield(ActorJ equipper){
		//Called when the item is successfully wielded as a weapon. 
		if (this.summon != null){
			summon.setMap(equipper.getMap());
			summon.setFloor(equipper.getFloor());
			summon.spawnNextTo(equipper.pos());
			summon.summoner(equipper);
			summon.isSummoned = true;
			LogScreen.log(summon.name()+ " appeared from the "+name+"!");
		}
	}
	
	public void onUpdate(){
		//Special method. Can be overriden by individual items, as usual. 
	}
	
	public void onEgoUpdate(int edr){
		//Ego update method.
		System.out.println(egoCount);
		if (this.egoCount > 0){
			egoCount-=edr;
			if (this.egoCount <= 0){
				usable(true);
				summon.alive = true;
				summon.HP(summon.mHP());
				LogScreen.log(name+" begins to glow once more.");
			}
		}
	}
}
