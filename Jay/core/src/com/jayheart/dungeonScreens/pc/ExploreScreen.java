package com.jayheart.dungeonScreens.pc;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jayheart.dungeonAI.PlayerAI;
import com.jayheart.dungeonGame.Action;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Coordinate;
import com.jayheart.dungeonGame.FieldOfView;
import com.jayheart.dungeonGame.FloorMap;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonGame.Tile;
import com.jayheart.dungeonScreens.LogScreen;
import com.jayheart.dungeonScreens.MapScreen;

public class ExploreScreen implements Screen{

	final Jayheart game;
	private FloorMap map;
	private ActorJ player;
	private int tileSize;
	static Texture damage = new Texture(Gdx.files.internal("db.png"));
	static Texture health = new Texture(Gdx.files.internal("hb.png"));
	static Texture frame = new Texture(Gdx.files.internal("dframe.png"));
	static Texture stairs = new Texture(Gdx.files.internal("stairs.png"));
	private OrthographicCamera camera;

	public ExploreScreen(final Jayheart g, FloorMap f, ActorJ p, int ts){
		game = g;
		map = f;
		player = p;
		map.placeWhereEmpty(player);
		tileSize = ts;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
	}

	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		//Gdx.gl.glClearColor(1, 1, 1, 1); 
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyPressed(Keys.L)) game.setScreen(new LogScreen(game, this));
		if (Gdx.input.isKeyPressed(Keys.I)) game.setScreen(new InventoryScreen(game, this, player));
		if (Gdx.input.isKeyPressed(Keys.E)) game.setScreen(new EquipScreen(game, this, player));
		if (Gdx.input.isKeyPressed(Keys.M)) game.setScreen(new MapScreen(game, map, player, tileSize, this));
		Tile[][] dungeonMap = map.map();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		int x=0;
		int y=0;
		map = player.getMap();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(":CCCCCCCCC");
		}
		for (Tile[] m : dungeonMap){
			for (Tile t : m){
				//if (camera.frustum.pointInFrustum(x*tileSize-tileSize, y*tileSize-tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize-tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize-tileSize, y*tileSize+tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize+tileSize, 0)){
				if (camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize+tileSize, 0)){
					if (FieldOfView.LOS(map.obstructions(), player.pos(), player.sight(), FieldOfView.RadiusStrategy.CIRCLE, new Coordinate(x, y))){
						if (map.actor(x,y)==null && map.item(x,y).size()==0 && map.stairs()[x][y] == null) game.batch.draw(t.texture(),x*tileSize,y*tileSize);
						else if (map.stairs()[x][y] != null){ 
							game.batch.draw(stairs,x*tileSize,y*tileSize);
						}
						else if (map.actor(x, y)!=null) {
							game.batch.draw(map.actor(x, y).texture(),x*tileSize,y*tileSize);
							drawHealthBar(x*tileSize, y*tileSize, map.actor(x, y));
						}
						else {
							game.batch.draw(map.item(x, y).get(0).texture(), x*tileSize, y*tileSize);
						}
					}
					else if (player.AI().isKnown(map, new Coordinate(x, y))){
						game.batch.enableBlending();
						game.batch.draw(map.map()[x][y].texture(),x*tileSize,y*tileSize);
						Sprite s = Tile.invisible;
						s.setAlpha(0.6f);
						s.setCenterX(x*32+16);
						s.setCenterY(y*32+16);
						s.draw(game.batch);
					}
					else game.batch.draw(Tile.VOID.texture(),x*tileSize,y*tileSize);

				}
				y++;
			}
			y=0;
			x++;
		}
		game.font.draw(game.batch, ((PlayerAI)player.AI()).nutrition()+"/"+1000,camera.position.x,camera.position.y);
		game.batch.end();
		if (Gdx.input.isKeyPressed(Keys.L)){
			game.setScreen(new LogScreen(game, this));
		}
		else if (Gdx.input.isKeyPressed(Keys.I)){
			game.setScreen(new InventoryScreen(game, this, player));
		}
		else if (Gdx.input.isKeyPressed(Keys.LEFT)){
			Action a = new Action(){
				public void execute(){
					player.AI().go("Left");
				}
			};
			player.AI().queueAction(a);
			playTurn();
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)){
			Action a = new Action(){
				public void execute(){
					player.AI().go("Down");
				}
			};
			player.AI().queueAction(a);
			playTurn();
		}
		else if (Gdx.input.isKeyPressed(Keys.UP)){
			Action a = new Action(){
				public void execute(){
					player.AI().go("Up");
				}
			};
			player.AI().queueAction(a);
			playTurn();
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)){
			Action a = new Action(){
				public void execute(){
					player.AI().go("Right");
				}
			};
			player.AI().queueAction(a);
			playTurn();
		}
		else if (Gdx.input.isKeyPressed(Keys.U)){
			Action a = new Action(){
				public void execute(){
					player.AI().goUp();
				}
			};
			player.AI().queueAction(a);
			playTurn();
		}
		else if (Gdx.input.isKeyPressed(Keys.F)){
			Action a = new Action(){
				public void execute(){
					player.AI().goDown();
				}
			};
			player.AI().queueAction(a);
			playTurn();
		}
		else if (Gdx.input.isKeyPressed(Keys.G)){
			Action a = new Action(){
				public void execute(){
					player.AI().pickup();
				}
			};
			player.AI().queueAction(a);
			playTurn();

		}
		camera.translate(player.pos().x*tileSize-camera.position.x, player.pos().y*tileSize-camera.position.y);
		camera.update();

	}

	public void playTurn(){
		List<ActorJ> ac = new ArrayList<ActorJ>();
		for (ActorJ[] a: map.actors()){
			for (ActorJ a2 : a){
				if (a2 != null) ac.add(a2);
			}
		}
		ac.remove(player);
		ac.add(0, player);
		for (ActorJ ac2 : ac){
			if (ac2.alive) { 
				ac2.update();
				for (Item i : ac2.inventory()){
					i.onUpdate();
				}
			}
		}
		camera.translate(player.pos().x*tileSize-camera.position.x, player.pos().y*tileSize-camera.position.y);
		camera.update();

	}

	public void drawTextBox(){
		//Draws text box. Requires the batch to have been begun and end OUTSIDE the method :V
	}

	public void drawHealthBar(int sx, int sy, ActorJ a){
		//Draws health bar. Requires the batch to have been begun and end OUTSIDE the method :V
		if (a.HP()==a.mHP()) return;
		int perc = (int)((float)a.HP()/(float)a.mHP()*20);
		game.batch.draw(frame, sx, sy);
		for (int x=0; x<perc; x++){
			game.batch.draw(health, (sx+x)+1, sy+1);
		}
		for (int x=perc; x<20; x++){
			game.batch.draw(damage, (sx+x)+1, sy+1);
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
