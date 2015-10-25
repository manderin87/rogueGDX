package com.jayheart.dungeonScreens.android;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.jayheart.dungeonAI.PlayerAI;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Coordinate;
import com.jayheart.dungeonGame.FieldOfView;
import com.jayheart.dungeonGame.FloorMap;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonGame.Tile;
import com.jayheart.dungeonScreens.LogScreen;
import com.jayheart.dungeonScreens.MapScreen;
import com.jayheart.dungeonScreens.buttons.ExploreButtonListener;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class AndroidExploreScreen extends ExploreScreen{

	final Jayheart game;
	private FloorMap map;
	private ActorJ player;
	private int tileSize;
	static Texture damage = new Texture(Gdx.files.internal("db.png"));
	static Texture health = new Texture(Gdx.files.internal("hb.png"));
	static Texture frame = new Texture(Gdx.files.internal("dframe.png"));
	private OrthographicCamera camera;
	private Stage stage;
	private Skin skin;

	public AndroidExploreScreen(final Jayheart g, FloorMap f, ActorJ p, int ts){
		super(g, f, p, ts);
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
		if (Gdx.input.isKeyPressed(Keys.I)) game.setScreen(new AndroidInventoryScreen(game, this, player));
		if (Gdx.input.isKeyPressed(Keys.E)) game.setScreen(new AndroidEquipScreen(game, this, player));
		if (Gdx.input.isKeyPressed(Keys.M)) game.setScreen(new MapScreen(game, map, player, tileSize, this));
		Tile[][] dungeonMap = map.map();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		int x=0;
		int y=0;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.out.println(":CCCCCCCCC");
		}
		for (Tile[] m : dungeonMap){
			for (Tile t : m){
				//if (camera.frustum.pointInFrustum(x*tileSize-tileSize, y*tileSize-tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize-tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize-tileSize, y*tileSize+tileSize, 0) || camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize+tileSize, 0)){
				if (camera.frustum.pointInFrustum(x*tileSize+tileSize, y*tileSize+tileSize, 0) && camera.frustum.pointInFrustum(x*tileSize+tileSize+(camera.viewportWidth-20*tileSize), y*tileSize+tileSize, 0)){
					if (FieldOfView.LOS(map.obstructions(), player.pos(), player.sight(), FieldOfView.RadiusStrategy.CIRCLE, new Coordinate(x, y))){
						if (map.actor(x,y)==null && map.item(x,y).size()==0) game.batch.draw(t.texture(),x*tileSize,y*tileSize);
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
		stage.draw();
		if (Gdx.input.isKeyPressed(Keys.L)){
			game.setScreen(new LogScreen(game, this));
		}
		camera.translate(player.pos().x*tileSize-camera.position.x, player.pos().y*tileSize-camera.position.y);
		camera.update();

	}

	public void playTurn(){
		System.out.println(":V");
		List<ActorJ> ac = new ArrayList<ActorJ>();
		for (ActorJ[] a: map.actors()){
			for (ActorJ a2 : a){
				if (a2 != null) ac.add(a2);
			}
		}
		while (ac.contains(player)) ac.remove(player);
		ac.add(0, player);
		for (ActorJ ac2 : ac){
			if (ac2.alive) { 
				ac2.update();
				System.out.println(ac2.name()+"update ");
				for (Item i : ac2.inventory()){
					i.onUpdate();
					System.out.println("update item");
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
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		skin = new Skin();
		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(100, 15, Format.RGBA8888);
		pixmap.setColor(Color.BLUE);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		BitmapFont bfont=new BitmapFont();
		bfont.setScale(1f);
		skin.add("default",bfont);

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

		textButtonStyle.font = skin.getFont("default");

		skin.add("default", textButtonStyle);

		TextButton equip = new TextButton("Equipment", textButtonStyle);
		equip.setPosition(camera.viewportWidth-(camera.viewportWidth-(20*tileSize))+tileSize, 300);
		equip.addListener(new ExploreButtonListener(game, this, player, "Equip"));
		stage.addActor(equip);

		TextButton inventory = new TextButton("Inventory", textButtonStyle);
		inventory.setPosition(camera.viewportWidth-(camera.viewportWidth-20*tileSize)+tileSize, 330);
		inventory.addListener(new ExploreButtonListener(game, this, player, "Inventory"));
		stage.addActor(inventory);

		TextButton up = new TextButton("up", textButtonStyle);
		up.setPosition(camera.viewportWidth-(camera.viewportWidth-20*tileSize)+tileSize, 30);
		up.addListener(new ExploreButtonListener(game, this, player, "up"));
		stage.addActor(up);

		TextButton down = new TextButton("down", textButtonStyle);
		down.setPosition(camera.viewportWidth-(camera.viewportWidth-20*tileSize)+tileSize, 60);
		down.addListener(new ExploreButtonListener(game, this, player, "down"));
		stage.addActor(down);

		TextButton left = new TextButton("left", textButtonStyle);
		left.setPosition(camera.viewportWidth-(camera.viewportWidth-20*tileSize)+tileSize, 90);
		left.addListener(new ExploreButtonListener(game, this, player, "left"));
		stage.addActor(left);

		TextButton right = new TextButton("right", textButtonStyle);
		right.setPosition(camera.viewportWidth-(camera.viewportWidth-20*tileSize)+tileSize, 120);
		right.addListener(new ExploreButtonListener(game, this, player, "right"));
		stage.addActor(right);
		
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
