package com.jayheart.dungeonAI;

import com.jayheart.dungeonGame.ActorJ;

public class BeastAI extends ActorAI {
	//AI class for aggressive beasts. Hunts on its turn, instead of simply wandering. 
	public BeastAI(){
		super();
	}

	public void onTurn(){
		super.onTurn();
		hunt();
	}

	public boolean isAggressiveTo(ActorJ target){
		if (target.summoner() != null && target.summoner().equals(this)){
			return false;
		}
		if (target.summoner() != null){
			return isAggressiveTo(target.summoner());
		}
		if (target.name().equals(actor.name())){
			//creatures don't attack their own kind
			return false;
		}
		if (!target.AI().isAggressiveTo(actor)) return false;
		return true;
	}
}
