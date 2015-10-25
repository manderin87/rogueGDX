package com.jayheart.dungeonScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.DCFactory;
import com.jayheart.dungeonGame.FloorMap;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class MainMenuScreen implements Screen {
	//Main menu. Pretty bland right now; exactly what you would expect it to be. 
	final Jayheart game;
	OrthographicCamera camera;

	public MainMenuScreen(final Jayheart g){
		game = g;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
	}
	
	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "--Welcome to this game!--", 100, 150);
		game.font.draw(game.batch, "----", 100, 100);
		game.batch.end();
		if (Gdx.input.isTouched()){
			ActorJ p = DCFactory.makePlayer(1, null, game, "Fighter");
			FloorMap f = new FloorMap(1, 125, 125, 40, 0.1);
			p.map(f);
			game.setScreen(new ExploreScreen(game, f, p, 32));
			//game.setScreen(new TestScreen(game));
		}
	}
	
	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {	
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {	
	}

	@Override
	public void dispose() {
	}
}
