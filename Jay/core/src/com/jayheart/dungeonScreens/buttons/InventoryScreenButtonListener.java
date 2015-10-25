package com.jayheart.dungeonScreens.buttons;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.android.AndroidItemScreen;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class InventoryScreenButtonListener extends ChangeListener {
	private final Jayheart game;
	private ExploreScreen ret;
	private ActorJ player;
	private Item i;
	boolean isWield;
	boolean isMine;
	
	public InventoryScreenButtonListener(final Jayheart g, ExploreScreen es, ActorJ player, Item i2){
		super();
		this.game = g;
		this.ret = es;
		this.player = player;
		this.i = i2;
		isMine = true;
		isWield = false;
		
	}
	public InventoryScreenButtonListener(final Jayheart g, ExploreScreen es, ActorJ player, Item i2, boolean isMine, boolean isWield){
		super();
		this.game = g;
		this.ret = es;
		this.player = player;
		this.i = i2;
		this.isWield = isWield;
		this.isMine = isMine;
	}
	@Override
	public void changed(ChangeEvent event, Actor actor) {
		game.setScreen(new AndroidItemScreen(game, ret, player, i, isMine, isWield));
	}

}
