package com.jayheart.dungeonScreens.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Item;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.buttons.ExitButtonListener;
import com.jayheart.dungeonScreens.buttons.ItemScreenButtonListener;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class AndroidItemScreen implements Screen {
	//Screen for using in item. 
	final Jayheart game;
	private OrthographicCamera camera;
	private ExploreScreen ret;
	private ActorJ player;
	private Item i;
	private boolean inventoryItem;
	private boolean wieldedItem;
	private Stage stage;
	private Skin skin;
	
	public AndroidItemScreen(final Jayheart g, ExploreScreen e, ActorJ p, Item item, boolean inventory, boolean wielded){
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
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
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

		TextButton exit = new TextButton("Quit", textButtonStyle);
		exit.setPosition(0, 25);
		exit.addListener(new ExitButtonListener(game, ret));
		stage.addActor(exit);
		
		if (inventoryItem){
			TextButton wield = new TextButton("Wield", textButtonStyle);
			wield.setPosition(75, 25);
			wield.addListener(new ItemScreenButtonListener(game,ret,player,i,"Wield"));
			stage.addActor(wield);
			
			TextButton equip = new TextButton("Equip", textButtonStyle);
			equip.setPosition(150, 25);
			equip.addListener(new ItemScreenButtonListener(game,ret,player,i,"Equip"));
			stage.addActor(equip);
			
			TextButton eat = new TextButton("Eat", textButtonStyle);
			eat.setPosition(225, 25);
			eat.addListener((new ItemScreenButtonListener(game,ret,player,i,"Eat")));
			stage.addActor(eat);
			
			TextButton quaff = new TextButton("Quaff", textButtonStyle);
			eat.setPosition(275, 25);
			eat.addListener((new ItemScreenButtonListener(game,ret,player,i,"Quaff")));
			stage.addActor(quaff);
		}
		
		else if (wieldedItem){
			TextButton unwield = new TextButton("Unwield", textButtonStyle);
			unwield.setPosition(75, 25);
			unwield.addListener(new ItemScreenButtonListener(game,ret,player,i,"Unwield"));
			stage.addActor(unwield);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "--"+i.name()+"--", 0, camera.viewportHeight);
		game.font.draw(game.batch, i.description(), 0, camera.viewportHeight-15);
		game.batch.end();
		stage.draw();
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
