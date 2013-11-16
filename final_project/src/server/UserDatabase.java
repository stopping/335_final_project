package server;

import java.util.ArrayList;
import java.util.HashMap;

import shared.Unit;

public class UserDatabase {

	private HashMap<String, ArrayList<Unit>> database;
	
	public UserDatabase() {
		database = new HashMap<String, ArrayList<Unit>>();
	}
	
	public void addUser(String name, ArrayList<Unit> units) {
		database.put(name, units);
	}
	
	public  ArrayList<Unit> getUnits(String name) {
		return database.get(name);
	}
	
	public boolean hasUser(String name) {
		return database.containsKey(name);
	}
	
	
}
