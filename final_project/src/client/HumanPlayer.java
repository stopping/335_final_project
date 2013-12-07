package client;

import game_commands.GameCommand;

import java.util.ArrayList;
import java.util.HashMap;

import shared.Command;
import shared.Game;
import unit.Unit;

public abstract class HumanPlayer {
	
	protected int credits;
	protected ArrayList<Unit> units;
	protected Game game;
	protected Client client;
	
	public HumanPlayer() {
		client = new Client("localhost", 4009, this);
		Thread t = new Thread(client);
		t.start();
	}
	
	public boolean executeGameCommand(GameCommand com) {
		boolean ret = game.executeCommand(com);
		System.out.println(ret);
		return ret;
	}
	
	public void update() {
		System.out.println(game.toString());
	}

	public void setGame(Game g) {
		this.game = g;
	}
	
	public void setUnits(ArrayList<Unit> u) {
		units = u;
		
	}

	public void setCredits(int c) {
		credits = c;
	}
	
	public void sendCommand(Command com) {
		client.sendCommand(com);
	}
	
	public abstract void login();
	
	public abstract void failLogin();
	
	public abstract void canStartGame();
	
	public abstract void receiveMessage(String source, String message);
	
	public abstract void updateAvailGameRooms(HashMap<String, Integer> rooms);

	public void showGamePanel() {		
	
	}

}
