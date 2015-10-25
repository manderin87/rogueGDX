package com.jayheart.dungeonGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.jayheart.dungeonAI.*;

public class DCFactory {
	//Factory that generates creatures
	//ACTORS
	private static List<String> humanBodyParts = Arrays.asList("torso","legs","feet","hands","back");
	
	public static ActorJ makePlayer(int floor, FloorMap f, final Jayheart game, String bg){
		List<String> bodyParts = new ArrayList<String>();
		bodyParts.addAll(humanBodyParts);
		ActorJ p = new ActorJ(f, 5, 15, 5, 5, 5, 0, 0, floor, "Player", new Texture(Gdx.files.internal("player.png")), new PlayerAI(game), bodyParts, 2, 0);
		p.AI().actor(p);
		p.isPlayer = true;
		if (bg.equals("priest")){
			p.AI().setBackground(com.jayheart.dungeonAI.Background.priestBackground(p));
		}
		else {
			p.AI().setBackground(com.jayheart.dungeonAI.Background.fighterBackground(p));
			
		}
		System.out.println("DEBUG: character creatued.");
		return p;
	}

	public static ActorJ makeScurrier(int floor, FloorMap f){
		ActorJ p = new ActorJ(f, 2, 15, 5, 5, 5, 0, 0, floor, "Scurrier", new Texture(Gdx.files.internal("scurrier.png")), new PassiveAI(), new ArrayList<String>(), 0, 5);
		p.AI().actor(p);
		return p;
	}
	
	public static ActorJ makeSnapper(int floor, FloorMap f){
		ActorJ p = new ActorJ(f, 5, 10, 5, 2, 5, 0, 0, floor, "Snapper", new Texture(Gdx.files.internal("snapper.png")), new BeastAI(), new ArrayList<String>(), 0, 3);
		p.AI().actor(p);
		return p;
	}
	
	public static ActorJ makeSnapperSpirit(int floor, FloorMap f, ActorJ player, Item weapon){
		ActorJ p = new ActorJ(f, 5, 15, 5, 5, 5, 0, 0, floor, "Snapper Spirit", new Texture(Gdx.files.internal("snapperspirit.png")), new SummonedAI(weapon), new ArrayList<String>(), 0, 0);
		p.isSummoned(true, player);
		p.AI().actor(p);
		return p;
	}
	
	//FOOD
	public static Item makeRations(){
		Item i = new Item("Rations", new Texture(Gdx.files.internal("ration.png")), "A common ration. Eating it will restore some nutrition, and there's no risk of food poisoning!");
		i.addType("Food");
		i.addType("Edible");
		i.food(100);
		return i;
	}
	
	//POTIONS
	public static Item makeHealthPotion(){
		Item i = new Item("Health Potion", new Texture(Gdx.files.internal("healthpotion.png")), "A delicious health potion! It tastes like candy!"){
			public void onConsume(ActorJ consumer){
				super.onConsume(consumer);
				consumer.changeHP(-10);
			}
		};
		i.addType("Potion");
		i.addType("Quaffable");
		i.food(100);
		return i;
	}
	
	//WEAPONS
	public static Item makeSnapperTooth(){
		Item i = new Item("Snapper Tooth", new Texture(Gdx.files.internal("snappertooth.png")), "A snapper's tooth. It glows with a spirit inside!");
		i.addType("Weapon");
		i.attackVal(2);
		i.hands(1);
		i.food(0);
		i.summon(makeSnapperSpirit(-1, null, null, i));
		return i;
	}

	public static Item makeAlgernon(){
		List<String> bodyParts = new ArrayList<String>();
		bodyParts.addAll(humanBodyParts);
		Item i = new Item("Algernon", new Texture(Gdx.files.internal("algernon.png")), "A legendary silver butcher's knife once wielded by the ancestor of all evil rats."){ 
			public void onUpdate(){
			onEgoUpdate(2);
		}};
		i.addType("Weapon");
		i.attackVal(4);
		i.hands(2);
		i.food(0);
		i.ego(true);
		ActorJ p = new ActorJ(null, 8, 20, 7, 7, 7, 0, 0, -1, "Matriarch of the Tegana", new Texture(Gdx.files.internal("algernonsummon.png")), new SummonedAI(i), bodyParts, 2, 0);
		p.isSummoned(true, null);
		((SummonedAI) p.AI()).ego(true);
		p.AI().actor(p);
		i.summon(p);
		return i;
		
	}
}
