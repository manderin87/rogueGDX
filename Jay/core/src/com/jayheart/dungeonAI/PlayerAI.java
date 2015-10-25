package com.jayheart.dungeonAI;

import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.LogScreen;
import com.jayheart.dungeonScreens.LoseScreen;
public class PlayerAI extends ActorAI {
	//PlayerAI
	//The onTurn should be revamped; the current system isn't very friendly towaards alterate options
	final Jayheart game;
	Background background;
	public PlayerAI(final Jayheart g){
		super();
		game = g;
	}

	public void setBackground(Background b){
		background = b;
	}
	
	public void eat(Item i){
		if (!i.isType("Edible")) { 
			LogScreen.log(this.actor.name()+" stupidly tried to eat a "+i.name()+", but in vain.");
			return;
		}
		else {
			nutrition += i.food();
			actor.inventory().remove(i);
			LogScreen.log(this.actor.name()+" ate a "+i.name()+"! Yum!");
		}
	}
	
	public void onTurn(){
		updateVision();
		nutrition--;
		System.out.println(":D");
		if (this.nutrition<1) die();
		if (!queue.isEmpty()){
			queue.remove(0).execute();
			return;
		}
	}
	
	public void die(){
		game.setScreen(new LoseScreen(game));
	}
	
	public boolean isAggressiveTo(ActorJ target){
		return true;
	}
	
	public void onLevel() {
		actor.removeEXP(levelReq());
		background.onLevel();
		actor.level(actor.level() + 1);
		LogScreen.log(actor.name()+" leveled up!");
		
	}
}
