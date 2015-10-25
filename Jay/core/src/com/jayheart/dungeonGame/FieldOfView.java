package com.jayheart.dungeonGame;

import java.util.Arrays;
import java.util.List;

public class FieldOfView {
	/**
	* Calculates the Field Of View for the provided map from the given x, y
	* coordinates. Returns a lightmap for a result where the values represent a
	* percentage of fully lit.
	*
	* A value equal to or below 0 means that cell is not in the
	* field of view, whereas a value equal to or above 1 means that cell is
	* in the field of view.
	*
	* @param resistanceMap the grid of cells to calculate on where 0 is transparent and 1 is opaque
	* @param startx the horizontal component of the starting location
	* @param starty the vertical component of the starting location
	* @param radius the maximum distance to draw the FOV
	* @param radiusStrategy provides a means to calculate the radius as desired
	* @return the computed light grid
	*/
	
	float[][] resistanceMap, lightMap; int startx, starty, width, height; float radius; RadiusStrategy rStrat;
	
	public static boolean LOS(float[][] resistanceMap, Coordinate start, float radius, RadiusStrategy rStrat, Coordinate pos){
		return new FieldOfView().calculateFOV(resistanceMap, start, radius, rStrat)[pos.x][pos.y]>0;
	}
	public FieldOfView(){ }
	
	public float[][] calculateFOV(float[][] resistanceMap, Coordinate pos, float radius, RadiusStrategy rStrat) { 
		this.startx = pos.x;
	    this.starty = pos.y;
	    this.radius = radius;
	    this.rStrat = rStrat;
	    this.resistanceMap = resistanceMap;
			
	    width = resistanceMap.length;
	    height = resistanceMap[0].length;
	    lightMap = new float[width][height];
	 
	    lightMap[pos.x][pos.y] = 10;//light the starting cell
	    for (Direction d : Direction.DIAGNOLS) {
	        castLight(1, 1.0f, 0.0f, 0, d.deltaX, d.deltaY, 0);
	        castLight(1, 1.0f, 0.0f, d.deltaX, 0, 0, d.deltaY);
	    }
	    return lightMap;
	}
	 
	private void castLight(int row, float start, float end, int xx, int xy, int yx, int yy) {
	    float newStart = 0.0f;
	    if (start < end) {
	        return;
	    }
	    boolean blocked = false;
	    for (int distance = row; distance <= radius && !blocked; distance++) {
	        int deltaY = -distance;
	        for (int deltaX = -distance; deltaX <= 0; deltaX++) {
	            int currentX = startx + deltaX * xx + deltaY * xy;
	            int currentY = starty + deltaX * yx + deltaY * yy;
	            float leftSlope = (deltaX - 0.5f) / (deltaY + 0.5f);
	            float rightSlope = (deltaX + 0.5f) / (deltaY - 0.5f);
	 
	            if (!(currentX >= 0 && currentY >= 0 && currentX < this.width && currentY < this.height) || start < rightSlope) {
	                continue;
	            } else if (end > leftSlope) {
	                break;
	            }
	 
	            //check if it's within the lightable area and light if needed
	            if (rStrat.radius(deltaX, deltaY) <= radius) {
	                float bright = (float) (1 - (rStrat.radius(deltaX, deltaY) / radius));
	                lightMap[currentX][currentY] = bright;
	            }
	 
	            if (blocked) { //previous cell was a blocking one
	                if (resistanceMap[currentX][currentY] >= 1) {//hit a wall
	                    newStart = rightSlope;
	                    continue;
	                } else {
	                    blocked = false;
	                    start = newStart;
	                }
	            } else {
	                if (resistanceMap[currentX][currentY] >= 1 && distance < radius) {//hit a wall within sight line
	                    blocked = true;
	                    castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
	                    newStart = rightSlope;
	                }
	            }
	        }
	    }
	}
	
	private static class Direction {
		public static final List<Direction> DIAGNOLS = Arrays.asList(new Direction(1, 1), new Direction(1, -1), new Direction(-1, -1), new Direction(-1, 1));
		
		public int deltaX;
		public int deltaY;
		
		public Direction(int x, int y){
			deltaX = x;
			deltaY = y;
		}
	}

	public static class RadiusStrategy {
		public static final RadiusStrategy CIRCLE = new RadiusStrategy(){
			public double radius(int deltaX, int deltaY){
				return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			}
		};
		
		public double radius(int deltaX, int deltaY){
			return 0;
		}
	}
}
