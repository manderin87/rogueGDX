package com.jayheart.dungeonAI;
import com.jayheart.dungeonGame.*;

public class PassiveAI extends ActorAI {
	//A passive AI that does nothing but wander. Behavior "in combat" should be managed later. 
	public PassiveAI(){
		super();
	}

	public void onTurn(){
		super.onTurn();
		if (this.memories.keySet().isEmpty()) wander();
		else hunt();
	}
	
	public boolean isAggressiveTo(ActorJ target){
		if (this.memories.keySet().contains(target) && this.memories.get(target) < 0) {
			return true;
		}
		if (actor == null) System.out.println("Null actor");
		if (target == null) System.out.println("Null target");
		return false;
	}
}
