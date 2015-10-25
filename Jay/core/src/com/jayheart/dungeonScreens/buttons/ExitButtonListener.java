package com.jayheart.dungeonScreens.buttons;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class ExitButtonListener extends ChangeListener {
	private final Jayheart game;
	private final ExploreScreen ret;

	public ExitButtonListener(final Jayheart g, ExploreScreen e){
		super();
		this.game = g;
		this.ret = e;
	}
	
	public void changed(ChangeEvent event, Actor actor) {
		game.setScreen(ret);
		
	}

}
