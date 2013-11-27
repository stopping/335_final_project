package server;

import java.util.ArrayList;
import java.util.HashMap;

import unit.Unit;

public class UserDatabase {

	private HashMap<String, UserAccount> database;
	
	// A hashmap containing the user name and her password
	private HashMap<String, String> loginAndPassword;
	
	public UserDatabase() {
		database = new HashMap<String, UserAccount>();
		loginAndPassword = new HashMap<String, String>();
	}
	
	public void addUser(String name, String password, ArrayList<Unit> units) {
		database.put(name, new UserAccount(units));
		loginAndPassword.put(name, password);
	}
	
	public ArrayList<Unit> getUnits(String name) {
		return database.get(name).getUnits();
	}
	

	
	public boolean isValidUser(String name, String password) {
		return (loginAndPassword.containsKey(name) && loginAndPassword.get(name).equals(password));
	}
	
	public UserAccount getUser(String name) {
		return database.get(name);
	}

}
