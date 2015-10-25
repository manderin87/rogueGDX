package com.jayheart.dungeonScreens.android;

import java.util.ArrayList;
import java.util.List;

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
import com.jayheart.dungeonScreens.buttons.InventoryScreenButtonListener;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class AndroidEquipScreen implements Screen {
	//Displays player inventory
	final Jayheart game;
	private OrthographicCamera camera;
	private ExploreScreen ret;
	private ActorJ player;
	private Stage stage;
	private Skin skin;
	private List<Item> equips;
	public AndroidEquipScreen(final Jayheart g, ExploreScreen e, ActorJ p){
		game = g;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		ret = e;
		player = p;
	}

	@Override
	public void show() {
		equips = new ArrayList<Item>();
		for (String key : player.weapons.keySet()){
			if (player.weapons.get(key) != null && !equips.contains(player.weapons.get(key))){
				equips.add(player.weapons.get(key));
			}
		}

		// TODO Auto-generated method stub
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

		int i=equips.size()-1;
		for (float f=25; f<=camera.viewportHeight-25 && i>=0; f+=15){
			TextButton b = new TextButton(equips.get(i).name(), textButtonStyle);
			b.addListener(new InventoryScreenButtonListener(game, ret, player, equips.get(i), false, true));
			b.setPosition(0, f);
			stage.addActor(b);
			i--;
		}
		TextButton exit = new TextButton("EXIT", textButtonStyle);
		exit.addListener(new ExitButtonListener(game, ret));
		stage.addActor(exit);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "--YOUR WEAPONS--", 0, camera.viewportHeight);
		stage.draw();
		game.batch.end();

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
