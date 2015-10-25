package com.jayheart.dungeonScreens.buttons;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.jayheart.dungeonGame.Action;
import com.jayheart.dungeonGame.ActorJ;
import com.jayheart.dungeonGame.Jayheart;
import com.jayheart.dungeonScreens.android.AndroidEquipScreen;
import com.jayheart.dungeonScreens.android.AndroidInventoryScreen;
import com.jayheart.dungeonScreens.pc.ExploreScreen;

public class ExploreButtonListener extends ChangeListener{
	private final Jayheart g;
	private ExploreScreen e;
	private ActorJ p;
	private String type;
	
	public ExploreButtonListener(final Jayheart g, ExploreScreen e, ActorJ p, String type){
		this.g = g;
		this.e = e; 
		this.p = p;
		this.type = type;
	}
	
	@Override
	public void changed(ChangeEvent event, Actor actor) {
		System.out.println("TRIG");
		if (type.equals("Equip")){
			g.setScreen(new AndroidEquipScreen(g, e, p));
		}
		else if (type.equals("Inventory")){
			g.setScreen(new AndroidInventoryScreen(g, e, p));
		}
		else if (type.equals("up")){
			Action a = new Action(){
				public void execute(){
					p.AI().go("Up");
				}
			};
			p.AI().queueAction(a);
			e.playTurn();
		}
		else if (type.equals("down")){
			Action a = new Action(){
				public void execute(){
					p.AI().go("Down");
				}
			};
			p.AI().queueAction(a);
			e.playTurn();
		}
		else if (type.equals("left")){
			Action a = new Action(){
				public void execute(){
					p.AI().go("Left");
				}
			};
			p.AI().queueAction(a);
			e.playTurn();
		}
		else if (type.equals("right")){
			Action a = new Action(){
				public void execute(){
					p.AI().go("Right");
				}
			};
			p.AI().queueAction(a);
			e.playTurn();
		}
	}

}
