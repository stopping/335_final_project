package server;

import java.util.ArrayList;
import java.util.HashMap;

import shared.Game;
import unit.Unit;

public class UserDatabase {

	private HashMap<String, UserAccount> database;
	
	// A hashmap containing the user name and her password
	private HashMap<String, String> loginAndPassword;
	private HashMap<String, Game> savedGames;
	
	public UserDatabase() {
		database = new HashMap<String, UserAccount>();
		loginAndPassword = new HashMap<String, String>();
		savedGames = new HashMap<String, Game>();
	}
	
	public boolean addUser(String name, String password) {
		if (database.containsKey(name))
			return false;
		database.put(name, new UserAccount());
		loginAndPassword.put(name, password);
		return true;
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
	
	public boolean isSavedGame(String name) {
		return savedGames.containsKey(name);
	}
	
	public Game getSavedGame(String name) {
		return savedGames.get(name);
	}
	
	public void saveGameSesseion(String name, Game g) {
		if (savedGames.containsKey(name))
			savedGames.remove(name);
		savedGames.put(name, g);
	}

}
