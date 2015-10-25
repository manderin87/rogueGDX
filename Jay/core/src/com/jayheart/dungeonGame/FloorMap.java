package com.jayheart.dungeonGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
public class FloorMap {
	//Class containing each individual floor! :)
	//Also contains random floor generation
	private Tile[][] map;      //tile map
	private ActorJ[][] actors;  //actors (players+enemies) map
	private Coordinate[][] stairs;  //exits and entrances map
	public Coordinate[][] stairs(){ return stairs; }
	private List<Item>[][] items; //items map
	public ActorJ[][] actors() { return this.actors; }
	public ActorJ actor(int x, int y){ return actors[x][y]; }    //actors getter

	public void actor(int x, int y, ActorJ a){ 
		actors[x][y]=a; 
		if (a != null) {
			a.AI().makeKnown(this, new Coordinate(x, y));
		}

	} //actors setter
	public Tile[][] map(){ return map; }						
	private int floor;         									//floor of the dungeon this FloorMap represents, from start floor 0 to however deep it happens to be
	public Tile getTile(int x, int y){ return map[x][y]; }      //map getter
	public int getFloor(){ return floor; }                      //floor getter
	private Coordinate entry;                                   //Coordinate of entrance
	private Coordinate exit;                                    //"..." exit
	public Coordinate entry(){                                   
		return entry;
	}
	public Coordinate exit(){                                        			   //exit "..."
		return exit;
	}
	@SuppressWarnings("unchecked")
	public FloorMap(int flr, int width, int height, int clr, double d){            //initializer, takes one argument (the floor); could take room density if I wanted I guess
		this.map = new Tile[width][height];
		this.floor = flr;
		this.actors = new ActorJ[width][height];
		this.stairs = new Coordinate[width][height];
		this.items = (ArrayList<Item>[][]) new ArrayList<?>[width][height];
		for (int x=0; x<width; x++){
			for (int y=0; y<height; y++){
				items[x][y] = new ArrayList<Item>();
				stairs[x][y] = null;
			}
		}
		generateRandomMap(clr, width, height, d);
	}

	//PATHFINDING
	public List<Coordinate> findPathAStar(Coordinate startc, Coordinate endc){
		//Finds an A* "node path"
		//This is a standard A* implementation. It's the same one I used for AI class, small changes to the data structure aside. 
		//Early exit code should probably be added in. As of now, it defaults to 50,000, but will never reach that in a reasonable amount of time. 
		List<Node> frontier = new ArrayList<Node>();
		Node start = new Node(startc);
		Node end = new Node(endc);
		start.value = start.heuristic(end);
		start.cost = 0;
		frontier.add(start);
		List<Node> visited = new ArrayList<Node>();
		int n = 0; //tracks how many nodes have been opened
		Node node;

		while (!frontier.isEmpty()){
			//grabs first in queue
			node = frontier.remove(0);
			n++;
			visited.add(node);
			List<Node> l2 = node.neighbors(node, this); //gets neighbors
			for (Node l : l2){
				l.Par = node;
				if (l.pos.x==node.pos.x | l.pos.y==node.pos.y) l.cost = node.cost + 1;
				else l.cost = node.cost + Math.sqrt(2);
				l.value = l.heuristic(end);
				boolean go = true;
				for (Node nu : visited){
					if (nu.pos.x == l.pos.x && nu.pos.y == l.pos.y) { 
						go = false;
					}
				}
				if (go && !frontier.contains(l)){
					//places the node in the correct place in the queue
					boolean added = false;
					for (int i=0; i<frontier.size(); i++){
						if (l.cost+l.value < frontier.get(i).cost+frontier.get(i).value){
							frontier.add(i, l);
							added = true;
							break;
						}
					}
					if (!added) {
						frontier.add(l);
					}
				}
			}
			if (node.equals(end)){
				break;}
			if (n > 50000){
				System.out.println("Node limit reached"); return null;}
		}
		return Backwards(visited, start);

	}

	public static List<Coordinate> Backwards(List<Node> visits, Node start){
		//returns path from Start to Goal as a List. This is part of the A* code, and simply traces back through the visited nodes and finds the ideal path.
		List<Coordinate> bw = new ArrayList<Coordinate>();
		if (visits.size() == 0) return bw;
		Node c = visits.get(visits.size() - 1);
		while (c!= null && c.Par!=null && (c.pos.x!=start.pos.x || c.pos.y!=start.pos.y)){
			bw.add(c.pos);
			c = c.Par;
		}
		Collections.reverse(bw);
		return bw;
	}

	private class Node{
		//Private class used for pathfinding. A node is just a coordinate, but with pathfinding cost, value, and a parent. 
		public Coordinate pos;
		public double cost;
		public double value;
		public Node Par;
		public Node(Coordinate c){
			this.pos = c;
			this.cost = 0;
			this.value = 0; //heuristic value of a node - kept because recalculating it all the time is expensive! :V
			this.Par=null;
		}

		public List<Node> neighbors(Node node, FloorMap floorMap) {
			//Neighbor finder! Needs to start factoring in for creatures :V
			List<Node> ret = new ArrayList<Node>();
			for (int x=Math.max(0, node.pos.x-1); x<=Math.min(floorMap.map().length-1, node.pos.x+1); x++){
				for (int y=Math.max(0, node.pos.y-1); y<=Math.min(floorMap.map()[0].length-1, node.pos.y+1); y++){
					if (floorMap.map()[x][y].walktype()==1) ret.add(0, new Node(new Coordinate(x, y)));
				}
			}
			return ret;
		}

		public int heuristic(Node goal){
			//Heurisitc. 
			return (int) (Math.sqrt((goal.pos.x-pos.x)*(goal.pos.x-pos.x) + (goal.pos.y-pos.y)*(goal.pos.y-pos.y)));
		}

		@Override
		public String toString(){
			//method for node-to-string conversion
			return pos.toString();
		}

		@Override
		public boolean equals(Object o) { 
			//returns true if the x and y values are equal
			if (!(o instanceof Node)) {
				return false;
			}
			Node n = (Node) o;
			return (this.pos.x == n.pos.x && this.pos.y == n.pos.y);
		}

	}

	//MOB SPAWNING
	private void spawnItems(){
		//Spawns test items on a floor - 10 ration, 10 weapon, and a copy of Algernon
		for(int i=0; i<100; i++){
			Item food = DCFactory.makeRations();
			placeWherePossible(food);
			Item blade = DCFactory.makeSnapperTooth();
			placeWherePossible(blade);
			Item hp = DCFactory.makeHealthPotion();
			placeWherePossible(hp);
		}
		Item algernon = DCFactory.makeAlgernon();
		placeWherePossible(algernon);
	}

	private void spawnMobs(){
		//As above, with snappers and scurriers. 
		for(int i=0; i<10; i++){
			ActorJ mob = DCFactory.makeScurrier(this.floor, this);
			placeWhereEmpty(mob);
			mob = DCFactory.makeSnapper(this.floor, this);
			placeWhereEmpty(mob);
		}
	}

	public void placeWhereEmpty(ActorJ a) {
		//Places the actor at a random empty position. 
		int x=0;
		int y=0;
		while (map[x][y].walktype()!=1 || actors[x][y] != null ){
			x = MathUtils.random(0, map.length-1);
			y = MathUtils.random(0, map[0].length-1);
		}
		a.x(x);
		a.y(y);
		actors[x][y]=a;
	}

	public void placeWherePossible(Item a) {
		//Places the item at a random possible position. 
		int x=0;
		int y=0;
		while (map[x][y].walktype()!=1){
			x = MathUtils.random(0, map.length-1);
			y = MathUtils.random(0, map[0].length-1);
		}
		items[x][y].add(a);
	}

	//GENERATION METHODS
	private void generateRandomMap(int clr, int width, int height, double scaleFactor){
		//Generates a random map
		//Step 1: creating the map as all walls and defining Regions, which is used to maintain walkability
		List<ArrayList<Coordinate>> regions = new ArrayList<ArrayList<Coordinate>>();
		for (int x=0; x<width; x++){
			for (int y=0; y<height; y++){
				this.map[x][y]=Tile.WALL;
			}
		}
		//Step 2: generating forest clearings
		for (int x=0; x<=clr; x++){
			regions.add(new ArrayList<Coordinate>());
			int w = MathUtils.random((int)(width*scaleFactor*0.5), (int)(width*scaleFactor));
			int h = MathUtils.random((int)(width*scaleFactor*0.5), (int)(height*scaleFactor));
			int sx = MathUtils.random(1, width-2-w);
			int sy = MathUtils.random(1, height-2-h);
			fillRect(sx, sy, sx+w, sy+h, Tile.GRASS, regions, regions.size()-1);
		}
		//Step 3: generating forest features (NYI; since the player can fly rivers, etc don't affect movement as much)
		//Step 4: connecting all regions 
		while (regions.size() > 1){
			reduceRegions(regions, Tile.GRASS);
		}
		//Step 5: spawn mobs
		spawnMobs();
		spawnItems();
	}

	private void addStairs(FloorMap above, FloorMap below){
		if (above != null) { 
			for (int i=0; i<3; i++){
				int x=0, y=0, dx=0, dy=0;
				while (map[x][y].walktype()!=1 || stairs[x][y]!=null){
					x = MathUtils.random(0, map.length-1);
					y = MathUtils.random(0, map[0].length-1);
				}

				while (above.map()[dx][dy].walktype()!=1 || above.stairs()[dx][dy]!=null){
					dx = MathUtils.random(0, map.length-1);
					dy = MathUtils.random(0, map[0].length-1);
				}
				
				stairs[x][y] = new Coordinate(dx, dy);
				stairs[x][y].map = above;
				
				above.stairs()[dx][dy] = new Coordinate(x, y);
				above.stairs()[dx][dy].map = this;		
			}
		}
		
		if (below != null){
			for (int i=0; i<3; i++){
				int x=0, y=0, dx=0, dy=0;
				while (map[x][y].walktype()!=1 || stairs[x][y]!=null){
					x = MathUtils.random(0, map.length-1);
					y = MathUtils.random(0, map[0].length-1);
				}

				while (below.map()[dx][dy].walktype()!=1 || below.stairs()[dx][dy]!=null){
					dx = MathUtils.random(0, map.length-1);
					dy = MathUtils.random(0, map[0].length-1);
				}
				
				stairs[x][y] = new Coordinate(dx, dy);
				stairs[x][y].map = below;
				
				below.stairs()[dx][dy] = new Coordinate(x, y);
				below.stairs()[dx][dy].map = this;		
			}
		}
	}

	private void fillRect(int sx, int sy, int gx, int gy, Tile tile, List<ArrayList<Coordinate>> regions, int region){
		//Fills a rectangle with the provided tiletype
		for (int x=sx; x<=gx; x++){
			for (int y=sy; y<=gy; y++){
				this.map[x][y]=tile;
				regions.get(region).add(new Coordinate(x,y));
			}
		}
		checkRegions(region, regions);
	}

	private void checkRegions(int region, List<ArrayList<Coordinate>> regions){
		int reg = region;
		//Checks if any regions overlap with the passed one
		boolean comb = true;
		for (int i=0; i<regions.size(); i++){
			if (i == reg) continue;
			//Find if region and i region overlap:
			for (Coordinate c:regions.get(reg)){
				for (Coordinate c2: regions.get(i)){
					if (c.equals(c2)){
						System.out.println("OVERLAP: "+i+" "+region );
						combineRegions(reg, i, regions);
						i--;
						reg--;
						comb = false;
						break;
					}
				}
				if (!comb){
					comb = true;
					break;
				}
			}
		}
	}

	private void combineRegions(int r1, int r2, List<ArrayList<Coordinate>> regions){
		//Combines two OVERLAPPING regions; does NOT affect map
		ArrayList<Coordinate> temp = new ArrayList<Coordinate>();
		for (Coordinate c: regions.get(r1)){
			temp.add(c);
		}
		for (Coordinate c: regions.get(r2)){
			if (!temp.contains(c)){
				temp.add(c);
			}
		}
		regions.remove(r1);
		regions.remove(r2);
		regions.add(temp);
	}

	private void reduceRegions(List<ArrayList<Coordinate>> regions, Tile t){
		//creates corridor from r1 to r2 to decrease the number of regions available
		//each run of ReduceRegions decreases the number of regions in the map by 1. Strictly it might actually connect more than that, but checking for that would be expensive and give LESS interesting results :V
		int r1 = MathUtils.random(0, regions.size()-1);
		int r2 = MathUtils.random(0, regions.size()-1);
		int n = 0;
		while (r2 == r1){
			r2 = MathUtils.random(0, regions.size()-1);
			n++;
			if (n > 25) return;
		}
		ArrayList<Coordinate> c = new ArrayList<Coordinate>();
		Coordinate s = regions.get(r1).get(MathUtils.random(0, regions.get(r1).size() - 1));
		Coordinate g = regions.get(r2).get(MathUtils.random(0, regions.get(r2).size() - 1));
		drawCorridor(r1, r2, s, g, regions, t);
		for (Coordinate c2:regions.get(r1)){ if (!c.contains(c2)) c.add(c2); }
		for (Coordinate c2:regions.get(r2)){ if (!c.contains(c2)) c.add(c2); }
		regions.remove(r1);
		if (r2 > r1) r2--;
		regions.remove(r2);
		regions.add((ArrayList<Coordinate>) c);
	}

	private void drawCorridor(int r1, int r2, Coordinate s, Coordinate g, List<ArrayList<Coordinate>> regions, Tile t){
		ArrayList<Coordinate> c = new ArrayList<Coordinate>();
		for (Coordinate cor: regions.get(r1)) c.add(cor);
		for (Coordinate cor: regions.get(r2)) c.add(cor);
		if (s.x<=g.x){
			for (int x=s.x; x<=g.x; x++){
				map[x][g.y] = t;
			}
		}
		else { 
			for (int x=g.x; x<=s.x; x++){
				map[x][g.y] = t;
			}

		}
		if (s.y<=g.y){
			for (int y=s.y; y<=g.y; y++){
				map[s.x][y] = t;
			}
		}
		else { 
			for (int y=g.y; y<=s.y; y++){
				map[s.x][y] = t;
			}


		}
	}
	public List<Item> item(int x, int y) {
		return items[x][y];
	}

	public float[][] obstructions() {
		float[][] obstructions = new float[map.length][map[0].length];
		for (int x=0; x<obstructions.length; x++){
			for (int y=0; y<obstructions[0].length; y++){
				if (map[x][y].obstruction()){
					obstructions[x][y] = 1;
				}
				else obstructions[x][y]=0;
			}
		}
		return obstructions;
	}
}
