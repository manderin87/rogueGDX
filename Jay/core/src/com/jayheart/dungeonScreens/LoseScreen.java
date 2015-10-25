package com.jayheart.dungeonScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jayheart.dungeonGame.Jayheart;

public class LoseScreen implements Screen {
	//Lose screen. Bland right now; hopefully will contain its own Top Scores sheet eventually. 
	final Jayheart game;
	OrthographicCamera camera;

	public LoseScreen(final Jayheart g){
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
		game.font.draw(game.batch, "--You lose!--", 100, 150);
		game.font.draw(game.batch, "--Click to restart!--", 100, 100);
		game.batch.end();
		if (Gdx.input.isTouched()){
			game.setScreen(new MainMenuScreen(game));
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
