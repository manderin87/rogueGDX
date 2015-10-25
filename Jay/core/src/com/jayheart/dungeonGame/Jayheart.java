package com.jayheart.dungeonGame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jayheart.dungeonScreens.MainMenuScreen;

public class Jayheart extends Game implements ApplicationListener {
	//Base game class. All screens have access to information in this, and it contains basic, useful things they all want. That's it. 
	public SpriteBatch batch;
	public BitmapFont font;
	


	@Override
	public void create () {
		batch = new SpriteBatch();
		batch.enableBlending();
		font = new BitmapFont();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override 
	public void dispose(){
		batch.dispose();
		font.dispose();
	}
}
