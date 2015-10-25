package com.jayheart.dungeonAI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.jayheart.dungeonGame.Action;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Coordinate;
import com.jayheart.dungeonGame.FieldOfView;
import com.jayheart.dungeonGame.FloorMap;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonScreens.*;

public class ActorAI {
	//ActorAI. Every Actor has an AI used to determine its moves; all AIs are descendents of the core ActorAI, which provides basic features among other things
	//It recalls the actor and the item names it knows, as well as what items it's identified. 
	protected ActorJ actor;
	protected Map<String, Boolean> itemsKnown;
	protected Map<String, String> personalItemNames;
	public ActorJ actor(){ return actor; }
	public void actor(ActorJ a){ this.actor = a; }
	public Map<ActorJ, Integer> memories;
	protected int memoryCD;
	protected List<Action> queue;
	public void queueAction(Action a){ queue.add(a); }
	protected Map<FloorMap, boolean[][]> knownPlaces;
	protected int nutrition;
	public int nutrition() { return this.nutrition; }
	public void nutrition(int i) { this.nutrition = i; }
	
	public boolean isKnown(FloorMap f, Coordinate c) {
		//Checks if a coordinate is known
		return (knownPlaces.containsKey(f) && knownPlaces.get(f)[c.x][c.y]);
	}
	public void makeKnown(FloorMap f, Coordinate c){
		//Makes a coordinate is known
		if (!knownPlaces.containsKey(f)) {
			knownPlaces.put(f, new boolean[f.map().length][f.map()[0].length]);
			knownPlaces.get(f)[c.x][c.y] = true;
		}
		else knownPlaces.get(f)[c.x][c.y] = true;
	}
	
	public ActorAI(){
		//Returns a new ActorAI(). This method doesn't get used anywhere except for super() in its extensions (really, ActorAI could probably be abstract)
		this.personalItemNames = new HashMap<String, String>();
		this.itemsKnown = new HashMap<String, Boolean>();
		this.memories = new HashMap<ActorJ, Integer>();				//memories are positive or negative, based on addition
		queue = new ArrayList<Action>();
		memoryCD = 100;
		knownPlaces = new HashMap<FloorMap, boolean[][]>();
		nutrition = 1000;
	}
	
	public void skip(){
		//Method that does nothing, mostly used for Action.execute()s.
	}

	public void onTurn(){
		//Occurs on turn for any NPC
		memoryCD--;
		if (memoryCD == 0){
			memoryCD = 100;
			for (ActorJ key : memories.keySet()){
				if (memories.get(key) > 0){
					memories.put(key, memories.get(key)-1);
				}
				if (memories.get(key) < 0){
					memories.put(key, memories.get(key)+1);
				}
				if (memories.get(key) == 0) memories.remove(key);
			}
		}
		updateVision();
		//Called whenever a creature gets its turn
	}

	protected void updateVision() {
		//Updates the field of view. :V
		float[][] FOV = new FieldOfView().calculateFOV(actor().getMap().obstructions(), actor().pos(), actor().sight(), FieldOfView.RadiusStrategy.CIRCLE);
		for (int x=0; x<actor().getMap().map().length; x++){
			for (int y=0; y<actor().getMap().map().length; y++){
				if (FOV[x][y] > 0){
					makeKnown(actor.getMap(), new Coordinate(x,y));
				}
			}
		}
	}
	public void wander(){
		//Called for a creature to move randomly
		int rand = MathUtils.random(1,4);
		switch (rand){
		case 1: go("Left"); break;
		case 2: go("Right"); break;
		case 3: go("Up"); break;
		case 4: go("Down"); break;
		}
	}
	
	public void clairhunt(ActorJ target) {
		//Clairvoyantly hunt the target, regardless of distance
		List<Coordinate> path = actor().getMap().findPathAStar(actor.pos(), target.pos());
		if (path == null || path.size() == 0) return;
		moveTo(path.get(0));
		return;
	}

	public void huntPlayer(){
		//Called for a non-player creature to Hunt the player
		for (int x=Math.max(0, actor.pos().x-actor.sight()/2-1); x<Math.min(actor.pos().x+actor.sight()/2+1, actor.getMap().map().length); x++){
			for (int y=Math.max(0, actor.pos().y-actor.sight()/2-1); y<Math.min(actor.pos().y+actor.sight()/2+1, actor.getMap().map()[0].length); y++){
				if (actor.getMap().actor(x,y)!=null && actor.getMap().actor(x,y).isPlayer && FieldOfView.LOS(actor.getMap().obstructions(), actor.pos(), actor.sight(), FieldOfView.RadiusStrategy.CIRCLE, new Coordinate(x, y))){
					List<Coordinate> path = actor().getMap().findPathAStar(actor.pos(), actor.getMap().actor(x,y).pos());
					if (path == null || path.size() == 0) continue; 
					moveTo(path.get(0));
					return;
				}
			}
		}
		onHuntFailure();
	}

	public void hunt(ActorJ target){
		//Called for a non-player creature to Hunt the target
		for (int x=Math.max(0, actor.pos().x-actor.sight()/2-1); x<Math.min(actor.pos().x+actor.sight()/2+1, actor.getMap().map().length); x++){
			for (int y=Math.max(0, actor.pos().y-actor.sight()/2-1); y<Math.min(actor.pos().y+actor.sight()/2+1, actor.getMap().map()[0].length); y++){
				if (actor.getMap().actor(x,y)==target && FieldOfView.LOS(actor.getMap().obstructions(), actor.pos(), actor.sight(), FieldOfView.RadiusStrategy.CIRCLE, new Coordinate(x, y))){
					List<Coordinate> path = actor().getMap().findPathAStar(actor.pos(), actor.getMap().actor(x,y).pos());
					if (path == null || path.size() == 0) continue; 
					moveTo(path.get(0));
					return;
				}
			}
		}
		onHuntFailure();
	}

	public void hunt(){
		//Called for a non-player creature to Hunt the first aggressive actor it can see
		for (int x=Math.max(0, actor.pos().x-actor.sight()/2-1); x<Math.min(actor.pos().x+actor.sight()/2+1, actor.getMap().map().length); x++){
			for (int y=Math.max(0, actor.pos().y-actor.sight()/2-1); y<Math.min(actor.pos().y+actor.sight()/2+1, actor.getMap().map()[0].length); y++){
				if (actor.getMap().actor(x,y) != null && this.isAggressiveTo(actor.getMap().actor(x,y)) && FieldOfView.LOS(actor.getMap().obstructions(), actor.pos(), actor.sight(), FieldOfView.RadiusStrategy.CIRCLE, new Coordinate(x, y))){
					System.out.println(actor.name()+" is hunting "+actor.getMap().actor(x,y).name());
					List<Coordinate> path = actor().getMap().findPathAStar(actor.pos(), actor.getMap().actor(x,y).pos());
					if (path == null || path.size() == 0) continue; 
					moveTo(path.get(0));
					return;
				}
			}
		}
		onHuntFailure();
	}

	public void onHuntFailure(){
		//Called when a creature attempts and fails its hunt. 
		wander();
	}
	
	public boolean isAggressiveTo(ActorJ target){
		//Returns whether this feels aggressive towards a target. 
		return false;
	}

	public void onHit(ActorJ attacker){
		//Called when a creature is attacked by another creature
		if (actor == null || attacker == null) return;
		if (memories.containsKey(attacker)){
			memories.put(attacker, Math.min(memories.get(attacker)-1, -10));
		}
		else memories.put(attacker, -10);
		
	}
	
	public void moveTowards(int x, int y){
		//Called for a creature to move Towards x, y; 
		if (this.actor.pos().x < x){
			go("Right");
		}
		else if (this.actor.pos().x > x){
			go("Left");
		}
		else if (this.actor.pos().y < y){
			go("Up");
		}
		else if (this.actor.pos().y > y){
			go("Down");
		}
	}

	public void moveTo(Coordinate po){
		//Moves the creature directly to the position
		int gx=po.x;
		int gy=po.y;
		if (actor.getMap().getTile(gx, gy).walktype()==1 && actor.getMap().actor(gx, gy)==null){
			actor.getMap().actor(this.actor.pos().x, this.actor.pos().y, null);
			actor.getMap().actor(gx, gy, this.actor);
			this.actor.x(gx);
			this.actor.y(gy);
		}
		else if (actor.getMap().getTile(gx, gy).walktype()==1 && actor.getMap().actor(gx, gy)!=null){
			if (actor.getMap().actor(gx, gy).summoner() != null && actor.getMap().actor(gx, gy).summoner().equals(actor) && actor.isPlayer){
				//If it's the player and the summon, swap places
				Coordinate pos = actor.pos();
				ActorJ summon = actor.getMap().actor(gx, gy);
				actor.pos(summon.pos());
				summon.pos(pos);
				actor.getMap().actor(actor.pos().x, actor.pos().y, actor);
				summon.getMap().actor(summon.pos().x, summon.pos().y, summon);
				return;
			}
			if (isAggressiveTo(actor.getMap().actor(gx, gy))){
				System.out.println(actor.name() + " attacked!");
				actor.basicAttack(actor.getMap().actor(gx, gy));
				return;
			}
			System.out.println(actor.name() + " chose not to attack "+actor.getMap().actor(gx, gy).name());
		}
		
	}
	
	public void pickup(){
		//Pickup method. Picks up the first item at the player's position, if such an item exists. 
		if (actor.getMap().item(actor.pos().x, actor.pos().y).size()>0){
			Item item = actor.getMap().item(actor.pos().x, actor.pos().y).remove(0);
			LogScreen.log(this.actor.name()+" picked up a "+item.name()+"!");
			System.out.println(this.actor.name()+" picked up a "+item.name()+"!");
			actor.inventory().add(item);
		}
	}
	
	public void die(){
		//Kills the creature
		actor.getMap().actor(actor.pos().x, actor.pos().y, null);
		this.actor.alive = false;
		this.actor = null;
	}
	
	public void clearActions(){
		//Clears the creature's action queue. Generally mosti mportant for the palyer.
		this.queue.clear();
	}

	public void go(String direction){
		//Goes Left, Right, Up, Down
		int gx=this.actor.pos().x;
		int gy=this.actor.pos().y;
		if (direction.equals("Left")) { gx--; }
		else if (direction.equals("Right")) { gx++; }
		else if (direction.equals("Up")) { gy++; }  
		else if (direction.equals("Down")) { gy--; }
		if (actor.getMap().getTile(gx, gy).walktype()==1 && actor.getMap().actor(gx, gy)==null){
			actor.getMap().actor(this.actor.pos().x, this.actor.pos().y, null);
			actor.getMap().actor(gx, gy, this.actor);
			this.actor.x(gx);
			this.actor.y(gy);
		}
		else if (actor.getMap().getTile(gx, gy).walktype()==1 && actor.getMap().actor(gx, gy)!=null){
			if (actor.getMap().actor(gx, gy).summoner() != null && actor.getMap().actor(gx, gy).summoner().equals(actor) && actor.isPlayer){
				//If it's the player and the summon, swap places
				Coordinate pos = actor.pos();
				ActorJ summon = actor.getMap().actor(gx, gy);
				actor.pos(summon.pos());
				summon.pos(pos);
				actor.getMap().actor(actor.pos().x, actor.pos().y, actor);
				summon.getMap().actor(summon.pos().x, summon.pos().y, summon);
				return;
			}
			if (isAggressiveTo(actor.getMap().actor(gx, gy))){
				System.out.println(actor.name() + " attacked!");
				actor.basicAttack(actor.getMap().actor(gx, gy));
				return;
			}
			System.out.println(actor.name() + " chose not to attack "+actor.getMap().actor(gx, gy).name());
		}
	}
	
	public void eat(Item i){
		//Eats the item.
		if (!i.isType("Edible")) { 
			LogScreen.log(this.actor.name()+" stupidly tried to eat a "+i.name()+", but in vain.");
			return;
		}
		else {
			i.onConsume(actor);
			actor.inventory().remove(i);
			LogScreen.log(this.actor.name()+" ate a "+i.name()+"! Yum!");
		}
		
	}
	
	public void setBackground(Background b){
		//Sets the background. Player-only. 
	}
	
	public void quaff(Item i) {
		//Quaffs the item
		if (!i.isType("Quaffable")) { 
			LogScreen.log(this.actor.name()+" stupidly tried to quaff a "+i.name()+", but in vain.");
			return;
		}
		else {
			i.onConsume(actor);
			actor.inventory().remove(i);
			LogScreen.log(this.actor.name()+" quaffed a "+i.name()+"! How refreshing!");
		}
		
	}
	
	public int levelReq() {
		//Returns the amount of EXP needed for it to level up. 
		return actor.level()*10;
	}
	
	public void onLevel() {
		//Called on level up.
		actor.removeEXP(levelReq());
		actor.bAttack(actor.bAttack()+1);
		actor.bAttack(actor.bDef()+1);
		actor.bInt(actor.bInt()+1);
		actor.mHP(actor.mHP()+5);
		actor.changeHP(-5);
		actor.level(actor.level() + 1);
		LogScreen.log(actor.name()+" leveled up!");
	}
	
	public void goUp() {
		if (actor.getMap().stairs()[actor.pos().x][actor.pos().y] != null){
			actor.getMap().actor(actor.pos().x, actor.pos().y, null);
			actor.getMap().stairs()[actor.pos().x][actor.pos().y].map.actor(actor.getMap().stairs()[actor.pos().x][actor.pos().y].x, actor.getMap().stairs()[actor.pos().x][actor.pos().y].y, actor);
			actor.pos().x = actor.getMap().stairs()[actor.pos().x][actor.pos().y].x;
			actor.pos().y = actor.getMap().stairs()[actor.pos().x][actor.pos().y].y;
		}
	}
	
	public void goDown() {
		goUp();
	}
	
}
