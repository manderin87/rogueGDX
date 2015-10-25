package com.jayheart.dungeonScreens.pc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Jayheart;

public class InventoryScreen implements Screen {
	//Displays player inventory
	final Jayheart game;
	private OrthographicCamera camera;
	private ExploreScreen ret;
	private ActorJ player;
	private int pointer; 
	
	public InventoryScreen(final Jayheart g, ExploreScreen e, ActorJ p){
		game = g;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		ret = e;
		player = p;
		pointer = 0;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		System.out.println(player.inventory().size()+" items.");
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "--YOUR ITEMS--", 0, camera.viewportHeight);
		int i=player.inventory().size()-1;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(":CCCCCCCCC");
		}
		for (float f=15; f<=camera.viewportHeight-15 && i>=0; f+=15){
			if (i==pointer) game.font.draw(game.batch, "  >"+player.inventory().get(i).name(), 0, f);
			else game.font.draw(game.batch, player.inventory().get(i).name(), 0, f);
			i--;
		}
		game.batch.end();
		if (Gdx.input.isTouched()) game.setScreen(ret);
		else if (Gdx.input.isKeyPressed(Keys.DOWN) && pointer < player.inventory().size()-1) pointer++;
		else if (Gdx.input.isKeyPressed(Keys.UP) && pointer > 0) pointer--;
		else if (Gdx.input.isKeyPressed(Keys.ENTER) && player.inventory().size() > 0) game.setScreen(new ItemScreen(game, ret, player, player.inventory().get(pointer), true, false));
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
