package com.jayheart.dungeonGame;

public class Coordinate{
	//Coordinate
	//A very simple position storage class. Used in any NUMBER of places
		public int x;
		public int y;
		public FloorMap map; 
		public Coordinate(int x, int y){
			this.x=x;
			this.y=y;
			this.map = null;
		}
		public boolean equals(Coordinate c){
			return this.x==c.x && this.y==c.y;
		}
		@Override
		public String toString(){
			return "("+x+","+y+")";
		}
		
		public double distanceFrom(Coordinate c){
			return Math.sqrt((c.x-x)*(c.x-x) + (c.y-y)*(c.y-y));
		}
	}