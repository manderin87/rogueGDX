package com.jayheart.overworldGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Dragon {
//A class that represents a dragon. 
	private String name;
	private int HP;
	private int mHP;
	private int atk;
	private int def;
	private int age;
	private int sex; //-10 for none; 1 for f; 2 for m; 3 for fm
	private int getSex() { return sex; }
	private Map<String, String> bodyParts;
	private List<Dragon> children;
	private Dragon mother;
	private Dragon father;
	
	private Dragon(String nameN){
		name = nameN;
		HP = 20;
		mHP = 20;
		atk = 5;
		def = 5;
		age = 0;
		sex = new Random().nextInt(2)+1;
		bodyParts = new HashMap<String, String>();
		bodyParts.put("left wing", "healthy");
		bodyParts.put("right wing", "healthy");
		bodyParts.put("left foreleg", "healthy");
		bodyParts.put("right foreleg", "healthy");
		bodyParts.put("left backleg", "healthy");
		bodyParts.put("right backleg", "healthy");
		bodyParts.put("tail", "healthy");
		bodyParts.put("head", "healthy");
		bodyParts.put("torso", "healthy");
		children = new ArrayList<Dragon>();
		mother = null;
		father = null;
	}
	
	public Dragon breed(Dragon mate){
		if (sex+mate.getSex() < 3 || (sex+mate.getSex() < 6 && (sex+mate.getSex())%2==0)) return null;
		return null;
	}

	
	
}
