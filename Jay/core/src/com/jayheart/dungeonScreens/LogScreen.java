package com.jayheart.dungeonScreens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class LogScreen implements Screen {
	//Displays the Log, containing any messages from the Log scroller.
	final Jayheart game;
	private OrthographicCamera camera;
	private ExploreScreen ret;
	private static List<String> log = new ArrayList<String>();
	public static void log(String s){ 
		log.add(s);
		if (log.size() > 99) {
			log.remove(0);
		}
	}
	
	public LogScreen(final Jayheart g, ExploreScreen e){
		game = g;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		ret = e;
	}

	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "--YOUR LOG--", 0, camera.viewportHeight);
		int i=log.size()-1;
		for (float f=15; f<=camera.viewportHeight-15; f+=15){
			if (i<0) break;
			game.font.draw(game.batch, log.get(i), 0, f);
			i--;
		}
		game.batch.end();
		if (Gdx.input.isTouched()){
			game.setScreen(ret);
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
