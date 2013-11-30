package server;

import java.util.ArrayList;

/**
 * Class: GameRoom
 * Purpose: Provide a collection of client handlers 
 * 			that will participate in a game.
 */
public class GameRoom {

	public ArrayList<ClientHandler> players;
	
	public GameRoom() {
		players = new ArrayList<ClientHandler>();
	}
	
	public boolean addPlayer(ClientHandler p) {
		if (!isFull()) {
			players.add(p);
			if (players.size() > 1) {
				players.get(0).setOpponent(players.get(1).playerName);
				players.get(1).setOpponent(players.get(0).playerName);
			}
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(ClientHandler p) {
		if (players.contains(p)) {
			players.remove(p);
			return true;
		}
		return false;
	}
	
	public boolean isFull() {
		return players.size() == Server.MAX_PLAYERS ? true : false;
	}	
}