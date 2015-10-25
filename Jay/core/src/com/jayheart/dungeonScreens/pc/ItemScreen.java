package com.jayheart.dungeonScreens.pc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jayheart.dungeonGame.Action;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;

public class ItemScreen implements Screen {
	//Screen for using in item. 
	final Jayheart game;
	private OrthographicCamera camera;
	private ExploreScreen ret;
	private ActorJ player;
	private Item i;
	private boolean inventoryItem;
	private boolean wieldedItem;

	public ItemScreen(final Jayheart g, ExploreScreen e, ActorJ p, Item item, boolean inventory, boolean wielded){
		game = g;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		ret = e;
		player = p;
		i = item;
		inventoryItem = inventory;
		wieldedItem = wielded;
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "--"+i.name()+"--", 0, camera.viewportHeight);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(":CCCCCCCCC");
		}
		game.font.draw(game.batch, i.description(), 0, camera.viewportHeight-15);
		if (inventoryItem){
			game.font.draw(game.batch, "This is yours. You can (w)ield it, (e)at it, (d)rink it, or e(q)uip it.", 0, camera.viewportHeight-30);	
		}
		else if (wieldedItem){
			game.font.draw(game.batch, "You are wielding this. You can (u)nwield it.", 0, camera.viewportHeight-30);	
		}
		game.batch.end();
		if (Gdx.input.isTouched()) game.setScreen(ret);
		
		else if (Gdx.input.isKeyPressed(Keys.E) && player.inventory().size() > 0 && inventoryItem){
			Action a = new Action(){
				public void execute(){
					player.AI().eat(i);
				}
			};
			player.AI().queueAction(a);
			ret.playTurn();
			game.setScreen(ret);
		}
		
		else if (Gdx.input.isKeyPressed(Keys.W) && player.inventory().size() > 0 && inventoryItem){
			Action a = new Action(){
				public void execute(){
					player.wield(i);
				}
			};
			player.AI().queueAction(a);
			ret.playTurn();
			game.setScreen(ret);
		}
		
		else if (Gdx.input.isKeyPressed(Keys.U) && wieldedItem){
			Action a = new Action(){
				public void execute(){
					player.unwield(i);
				}
			};
			player.AI().queueAction(a);
			ret.playTurn();
			game.setScreen(ret);
		}
		
		else if (Gdx.input.isKeyPressed(Keys.D)){
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
