package com.jayheart.dungeonAI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.DCFactory;
import com.jayheart.dungeonGame.Item;

public class Background {
//Backgrounds aren't, strictly, AI; they handle how players level up, develop skills, etc. 
//The current Backgrounds are as follows:
//PRIEST: Though extremely frail, priests will have an easier time on their Int checks, allowing them to randomly bypass Fable fights.
	//Start with extremely low HP, low Strength, and extra Int. 
	//Start items are standard +1 HP potion, -1 Ration
	//No start armor, but start with Algernon
	//Gains Loyalty from Ego weapons more quickly, and automatically knows some Fable knowledge
//FIGHTER: Bulky and powerful, but stupid - and as a result, likely to struggle on Fable fights. 
	//Start with high HP, high Strength, and low Int. 
	//Start items are standard.
	//Start with standard armor and a snappertooth
	
	
	//GEAR FOR BACKGROUNDS
	private static List<Item> priestGear = Arrays.asList(DCFactory.makeHealthPotion(), DCFactory.makeHealthPotion(), DCFactory.makeRations());
	private static List<Item> fighterGear = Arrays.asList(DCFactory.makeHealthPotion(), DCFactory.makeRations(), DCFactory.makeRations());
	
	//WEAPONS FOR BACKGROUNDS
	private static List<Item> priestWeapons = Arrays.asList(DCFactory.makeAlgernon());
	private static List<Item> fighterWeapons = Arrays.asList(DCFactory.makeSnapperTooth());
	
	//ARMOR FOR BACKGROUND
	// TODO armor
	
	//STATS FOR BACKGROUND
	private static List<Float> priestStats = Arrays.asList(0.75f, 1.5f, 0.75f, 0.65f);
	private static List<Float> fighterStats = Arrays.asList(1.5f, 0.5f, 1.5f, 1.5f);
	
	//BACKGROUNDS THEMSELVES
	public static Background priestBackground(ActorJ p){ return new Background(priestGear, priestWeapons, priestStats, p); }
	public static Background fighterBackground(ActorJ p){ return new Background(fighterGear, fighterWeapons, fighterStats, p); }
	
	
	private List<Item> startGear;	
	private List<Item> startWeapon;
	private List<Item> startEquip;
	private Map<String, Float> statRatio;
	private Map<String, Float> roundedLeftoverStats;
	private ActorJ player;
	
	public Background(List<Item> startGear, List<Item> startWeapon, List<Float> statRatios, ActorJ player){
		this.startGear = startGear;
		this.startWeapon = startWeapon;
		statRatio = new HashMap<String, Float>();
		statRatio.put("mHP", statRatios.get(0));
		statRatio.put("bInt", statRatios.get(1));
		statRatio.put("bDef", statRatios.get(2));
		statRatio.put("bAttack", statRatios.get(3));
		roundedLeftoverStats = new HashMap<String, Float>();
		roundedLeftoverStats.put("mHP", 0f);
		roundedLeftoverStats.put("bInt", 0f);
		roundedLeftoverStats.put("bDef", 0f);
		roundedLeftoverStats.put("bAttack", 0f);
		this.player = player;
	}
	
	public void onStartUp(){
		
	}
	
	public void onLevel(){
		if (player.level()%3 == 0){
			
		}
		float bAt = 1*(statRatio.get("bAttack")) + roundedLeftoverStats.get("bAttack");
		float bDf = 1*(statRatio.get("bDef")) + roundedLeftoverStats.get("bDef");
		float bIt = 1*(statRatio.get("bInt")) + roundedLeftoverStats.get("bInt");
		float mH =  5*(statRatio.get("mHP")) + roundedLeftoverStats.get("mHP");
		roundedLeftoverStats.put("bAttack", bAt%1);
		roundedLeftoverStats.put("bDef", bDf%1);
		roundedLeftoverStats.put("bInt", bIt%1);
		roundedLeftoverStats.put("mHP", mH%1);
		player.bAttack(player.bAttack()+(int)bAt);
		player.bAttack(player.bDef()+(int)bDf);
		player.bInt(player.bInt()+(int)bIt);
		player.mHP(player.mHP()+(int)mH);
		player.changeHP((int)-mH);
	}
}
