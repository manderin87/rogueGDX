package com.jayheart.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jayheart.dungeonGame.Jayheart;

public class DesktopLauncher {
	public static void main (String[] arg) {
	      LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	      config.title = "Drogue";
	      config.width = 800;
	      config.height = 480;
	      new LwjglApplication(new Jayheart(), config);
	   }
}