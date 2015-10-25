package com.jayheart.dungeonGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public enum Tile {
	//Enum for tiletypes. Contains every possible typetype, its walktype (0 for wall, 1 for walkable - in the future, possibly 2 for water, etc)
	//its image, and whether it obstructs vision.
	GRASS (1, new Texture(Gdx.files.internal("grass.png")), false),
	WALL (0, new Texture(Gdx.files.internal("wall.png")), true),
	VOID (0, new Texture(Gdx.files.internal("void.png")), true);
	
	public static final Sprite invisible = new Sprite(new Texture(Gdx.files.internal("graytrans60.png")));
	private int walktype;
	public int walktype() { return walktype; }
	private Texture texture;
	public Texture texture() { return texture; }
	private boolean obstruction;
	public boolean obstruction() { return obstruction; }
	
	private Tile(int walktype, Texture tex, boolean ob){
		this.walktype = walktype;
		this.texture = tex;
		this.obstruction = ob;
	}
}
