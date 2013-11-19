package server;

import java.util.ArrayList;
import java.util.HashMap;

import shared.Unit;

public class UserDatabase {

	private HashMap<String, UserAccount> database;
	
	public UserDatabase() {
		database = new HashMap<String, UserAccount>();
	}
	
	public void addUser(String name, ArrayList<Unit> units, int playerNumber) {
		database.put(name, new UserAccount(units, playerNumber));
	}
	
	public ArrayList<Unit> getUnits(String name) {
		return database.get(name).getUnits();
	}
	
	public boolean hasUser(String name) {
		return database.containsKey(name);
	}
	
	public UserAccount getUser(String name) {
		return database.get(name);
	}

}
