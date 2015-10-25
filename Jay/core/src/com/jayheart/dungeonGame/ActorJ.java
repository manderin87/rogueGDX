package com.jayheart.dungeonGame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.jayheart.dungeonAI.ActorAI;
import com.jayheart.dungeonScreens.LogScreen;

public class ActorJ {
	//Actor class. Actors are individual instances of creatures that exist within the game - including the player. All life is Actors. 
	//Every actor has a number of basic stats, a position, a floor, a name, a texture, AI (see ActorAI and descendents), an inventory, and some generic Traits
	//
	public ActorAI AI() { return this.AI; }
	public int HP() { return HP; }              //getter: current HP
	public int mHP() { return mHP; }            //"..." max HP
	public int bAttack() { return bAttack; }    //"..." base Attack
	public int bDef() { return bDef; }          //"..." base Defense
	public int bInt() { return bInt; }          //"..." base Int
	public void HP(int i) { HP = i; }              //getter: current HP
	public void mHP(int i) { mHP = i; }            //"..." max HP
	public void bAttack(int i) { bAttack = i; }    //"..." base Attack
	public void bDef(int i) { bDef = i; }          //"..." base Defense
	public void bInt(int i) { bInt = i; }          //"..." base Int
	public Coordinate pos() { return this.pos; }
	public void pos(Coordinate c) { this.pos = c; }
	public int floor() { return floor; }        //"..." floor
	public String name() { return name; }       //"..." name
	public Texture texture() { return texture; }//"..." texture
	public void x(int x) { this.pos.x = x; }
	public void y(int y) { this.pos.y = y; }
	public boolean isSummoned() { return this.isSummoned; }
	public void isSummoned(boolean b, ActorJ summoner) { 
		this.isSummoned = b; 
		this.summoner = summoner;
	}
	public ActorJ summoner() { return this.summoner; }
	private ActorJ summoner;
	private int HP;
	private int mHP;
	private int bAttack;
	private int bDef;
	private int bInt;
	private Coordinate pos;
	private int floor;
	private String name;    
	private Texture texture;
	private ActorAI AI;
	private List<Item> inventory;
	public boolean isPlayer;
	boolean isSummoned;
	public boolean alive;
	private int sight;
	public int sight() { return sight; }
	private FloorMap map;
	public void map(FloorMap m){ this.setMap(m); }
	
	private int level;
	public int level() { return this.level; }
	public void level(int l) { this.level = l; }
	
	private int EXPlevel;
	public int EXPlevel(){ return this.EXPlevel; }
	
	private int EXPgive;
	public int EXPgive(){ return this.EXPgive; }
	
	public Map<String, Item> equipment;
	public Map<String, Item> weapons;

	public ActorJ(FloorMap flr, int s, int hp, int atk, int def, int in, int x, int y, int f, String name, Texture t, ActorAI a, List<String> bodyParts, int hands, int EXPgive){
		//if (Jayheart.dungeon.get(floor).actor(x,y) == null) Jayheart.dungeon.get(floor).actor(x,y,this);
		//else this.exists = false;
		this.setMap(flr);
		this.HP = hp;
		this.mHP = hp;
		this.bAttack = atk;
		this.bDef = def;
		this.bInt = in;
		this.sight = s;
		this.pos = new Coordinate(x, y);
		this.floor = f;
		this.name = name;
		this.texture = t;
		this.AI = a;
		this.isPlayer = false;
		this.isSummoned = false;
		this.summoner = null;
		this.alive = true;
		this.inventory = new ArrayList<Item>();
		equipment = new HashMap<String, Item>();
		weapons = new HashMap<String, Item>();
		for (String bodyPart : bodyParts){
			equipment.put(bodyPart, null);
		}
		for (int it=0; it<hands; it++){
			weapons.put("hand "+it, null);
		}
		this.EXPlevel = 0;
		this.level = 1;
		this.EXPgive = EXPgive;
	}

	public void update() {
		AI.onTurn();
	}

	public void wield(Item weapon){
		//Equip a weapon
		if (!weapon.isType("Weapon")) LogScreen.log(name + " tried to equip "+weapon.name()+" - that's not a weapon!");
		else if (!weapon.usable()) LogScreen.log(weapon.name()+ " burns "+name+"'s hands!");
		else {
			List<String> hands = new ArrayList<String>();
			for (String hand : weapons.keySet()){
				if (weapons.get(hand) == null){
					hands.add(hand);
					if (hands.size() == weapon.hands()){
						for (String ehand : hands){
							weapons.put(ehand, weapon);
						}
						inventory.remove(weapon);
						LogScreen.log(name+" wielded "+weapon.name()+".");
						weapon.onWield(this);
						return;
					}
				}
			}
			LogScreen.log(name+" tried to equip "+weapon.name()+" but didn't have enough hands...");
		}
	}


	public void equip(Item equip){
		//Equip a equip
		if (!equip.isType("Equipment")) LogScreen.log(name + " tried to equip "+equip.name()+" - that's not a equip!");
		else if (!equip.usable()) LogScreen.log(equip.name()+ " burns!");
		else {
			List<String> equipParts = equip.getTypes("Armor");
			if (equipParts.size() == 0) {
				LogScreen.log(name + " tried to equip "+equip.name()+" - but it's defective! :(");
				return;
			}
			
			for (String part : equipParts){
				if (equipment.keySet().contains(part) && equipment.get(part) == null){
					equipment.put(part, equip);
					inventory.remove(equip);
					LogScreen.log(name+" wielded "+equip.name()+".");
					equip.onWield(this);
					return;
					
				}
			}
			
			LogScreen.log(name+ " doesnt ave a body part to equip "+equip.name()+"!");
			
		}
	}

	
	public void basicAttack(ActorJ target){
		if (target.changeHP(Math.max(1, this.bAttack+weaponScore()-target.bDef()))){
			if (this.isSummoned){
				gainEXP((int)(target.EXPgive*.75));
				if (summoner!=null)summoner.gainEXP((int)(target.EXPgive*.5));
			}
			else gainEXP(target.EXPgive);
			return;
		}
		target.AI().onHit(this);
		LogScreen.log(this.name+" attacked "+target.name()+" for "+Math.max(1, this.bAttack-target.bDef())+" damage!");
	}

	private int weaponScore() {
		int ret = 0;
		if (this.weapons == null || weapons.values() == null) return 0;
		for (Item weapon : weapons.values()){
			if (weapon!= null) ret+=weapon.attackVal();
		}
		return ret;
	}
	
	public void gainEXP(int EXP){
		this.EXPlevel += EXP;
		while (this.EXPlevel > AI.levelReq()){
			AI.onLevel();
			
		}
	}
	
	public void removeEXP(int EXP){
		this.EXPlevel -= EXP;
		if (EXPlevel < 0) EXPlevel = 0;
	}
	
	public boolean changeHP(int damage){
	//Change HP. Returns True if the damage is fatal.
		this.HP -= damage;
		if (this.HP > this.mHP) this.HP = this.mHP;
		if (this.HP < 1) {
			AI.die();
			return true;
		}
		return false;
	}

	public FloorMap getMap() {
		return map;
	}

	public void setMap(FloorMap map) {
		this.map = map;
	}
	
	public List<Item> inventory() {
		return this.inventory;
	}
	
	public boolean spawnNextTo(Coordinate position){
		for (int x=Math.max(0, position.x-1); x<=Math.min(map.map().length-1, position.x+1); x++){
			for (int y=Math.max(0, position.y-1); y<=Math.min(map.map()[0].length-1, position.y+1); y++){
				System.out.println(x+","+y);
				if (map.actor(x, y) == null && map.map()[x][y].walktype()==1) {
					if (this.pos != null) map.actor(pos.x, pos.y, null);
					map.actor(x, y, this);
					pos = new Coordinate(x, y);
					System.out.println("Spawned at "+x+","+y+" (target at "+position.x+","+position.y+")");
					return true;
				}
			}
		}
		return false;
	}
	public void summoner(ActorJ equipper) {
		this.summoner = equipper;
	}
	
	public void destroyEquip(Item weapon) {
		for (String hand : weapons.keySet()){
			if (weapons.get(hand) != null && weapons.get(hand).equals(weapon)){
				weapons.put(hand, null);
			}
		}
	}

	public void unwear(Item equip) {
		//Take off equipment
		boolean removed = false;
		for (String hand : equipment.keySet()){
			if (equipment.get(hand) != null && equipment.get(hand).equals(equip)){
				equipment.put(hand, null);
				removed = true;
			}
		}
		if (!removed) return;
		inventory.add(equip);
		LogScreen.log(name+" takes off their "+equip.name());
		if (equip.summon() != null){
			equip.summon().isSummoned = false;
			equip.summon().AI().clearActions();
			map.actor(equip.summon().pos().x, equip.summon().pos().y, null);
			equip.summon().pos(null);
		}
	}
	
	public void unwield(Item weapon) {
		boolean removed = false;
		for (String hand : weapons.keySet()){
			if (weapons.get(hand) != null && weapons.get(hand).equals(weapon)){
				weapons.put(hand, null);
				removed = true;
			}
		}
		if (!removed) return;
		inventory.add(weapon);
		LogScreen.log(name+" unwields their "+weapon.name());
		if (weapon.summon() != null){
			weapon.summon().isSummoned = false;
			weapon.summon().AI().clearActions();
			map.actor(weapon.summon().pos().x, weapon.summon().pos().y, null);
			weapon.summon().pos(null);
		}
	}
	public int getFloor() {
		return this.floor;
	}
	
	public void setFloor(int floor){
		this.floor = floor;
	}
}
