package com.jayheart.dungeonScreens.buttons;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.jayheart.dungeonGame.Action;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class ItemScreenButtonListener extends ChangeListener {
	private final Jayheart game;
	private ExploreScreen ret;
	private ActorJ player;
	private Item i;
	private String type;
	public ItemScreenButtonListener(final Jayheart g, ExploreScreen es, ActorJ player, Item i2, String type){
		super();
		this.game = g;
		this.ret = es;
		this.player = player;
		this.i = i2;
		this.type = type;
	}
	@Override
	public void changed(ChangeEvent event, Actor actor) {
		System.out.println("ISC");
		if (type.equals("Wield")){
			Action a = new Action(){
				public void execute(){
					player.wield(i);
				}
			};
			player.AI().queueAction(a);
			System.out.println("WIELD");
			ret.playTurn();
			game.setScreen(ret);
		}
		if (type.equals("Unwield")){
			Action a = new Action(){
				public void execute(){
					player.unwield(i);
				}
			};
			player.AI().queueAction(a);
			System.out.println("UNWIELD");
			ret.playTurn();
			game.setScreen(ret);
		}
		if (type.equals("Equip")){
			/*
			Action a = new Action(){
				public void execute(){
					player.equip(i);
				}
			};
			player.AI().queueAction(a);
			ret.playTurn();
			*/
		}
		if (type.equals("Eat")){
			Action a = new Action(){
				public void execute(){
					player.AI().eat(i);
				}
			};
			player.AI().queueAction(a);
			ret.playTurn();
			System.out.println("EAT");
			game.setScreen(ret);
		}
		if (type.equals("Quaff")){
			Action a = new Action(){
				public void execute(){
					player.AI().quaff(i);
				}
			};
			player.AI().queueAction(a);
			ret.playTurn();
			System.out.println("QUAFF");
			game.setScreen(ret);
		}
	}

}
