package com.jayheart.dungeonAI;

import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonScreens.LogScreen;

public class SummonedAI extends ActorAI {
	//AI for basic Summons. Complex Summons may have their own AI :?
	//A summon is inherently aggressive towards anything aggressive to its master, will focus on the creature most dangerous to its master, and may be given a target to follow
	//It also will NOT move more than 25 squares away from its master
	private Item weapon;
	private int depth;
	private boolean ego;
	public void ego(boolean b) { ego=b; }
	
	public SummonedAI(Item i){
		super();
		weapon = i;
		depth = 0;
		ego = false;
	}

	public void onTurn(){
		if (!actor().isSummoned()) return;
		super.onTurn();
		depth = 0;
		if (actor.pos().distanceFrom(actor.summoner().pos()) > 25) {
			System.out.println("Seeking master");
			hunt(actor.summoner());
		}
		else hunt();
	}
	
	public void onHuntFailure(){
		System.out.println("Hunt failure");
		depth++;
		if (depth < 3) clairhunt(actor.summoner());
		else wander();
	}

	public boolean isAggressiveTo(ActorJ target){
		System.out.println("Aggression check");
		if (target.equals(actor.summoner())){
			return false;
		}
		if (!actor.summoner().isPlayer){ 
			System.out.println("AHUGEEROR");
			return actor.summoner().AI().isAggressiveTo(target);
		}
		if (target.AI().isAggressiveTo(actor.summoner())) System.out.println("GOOD");
		else System.out.println("Summon is okay with "+target.name());
		
		return target.AI().isAggressiveTo(actor.summoner());
	}

	public void die(){
		//Kills the creature
		actor.alive = false;
		if (!ego && weapon != null){
			actor.summoner().destroyEquip(weapon);
			LogScreen.log("As the "+actor.name()+" dies, its weapon shatters!");
			actor.getMap().actor(actor.pos().x, actor.pos().y, null);
			this.actor = null;
		}
		else if (ego && weapon != null){
			actor.summoner().unwield(weapon);
			weapon.usable(false);
			weapon.egoCount(1000);
			LogScreen.log("As "+actor.name()+" disappears, its weapon becomes unwieldably hot!");
		}
	}
}
