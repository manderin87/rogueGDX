package com.jayheart.dungeonScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Coordinate;
import com.jayheart.dungeonGame.FloorMap;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonGame.Tile;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class MapScreen implements Screen {
	//MapScreen. It's a... map screen. Exactly as you would expect - no turns pass during the use of the MapScreen; it just lets you look around.
	
	final Jayheart game;
	private FloorMap map;
	private int tileSize;
	private OrthographicCamera camera;
	private ActorJ player; 
	private ExploreScreen ret;
	
	public MapScreen(final Jayheart g, FloorMap f, ActorJ p, int ts, ExploreScreen rt){
		game = g;
		map = f;
		tileSize = ts;
		player = p;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		ret = rt;
	}

	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Tile[][] dungeonMap = map.map();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		int x=0;
		int y=0;
		
		for (Tile[] mp : dungeonMap){
			for (Tile t : mp){
				if (player.AI().isKnown(map, new Coordinate(x, y)) && (camera.frustum.pointInFrustum(x*tileSize-tileSize, y*tileSize-tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize-tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize-tileSize, y*tileSize+tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize+tileSize, 0)))
					game.batch.draw(t.texture(),x*tileSize,y*tileSize);
				y++;
			}
			y=0;
			x++;
		}
		game.batch.end();
		camera.update();
		if (Gdx.input.isKeyPressed(Keys.LEFT)) camera.position.x-=tileSize;
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) camera.position.x+=tileSize;
		else if (Gdx.input.isKeyPressed(Keys.UP)) camera.position.y+=tileSize;
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) camera.position.y-=tileSize;
		else if (Gdx.input.isTouched()) game.setScreen(ret);
		camera.update();
	}
	
	@Override
	public void show() {
		camera.translate(player.pos().x*tileSize-camera.position.x, player.pos().y*tileSize-camera.position.y);
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
