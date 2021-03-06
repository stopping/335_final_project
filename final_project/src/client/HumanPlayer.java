package client;

import game.Game;
import game_commands.GameCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import map.MapBehavior;
import server.Command;
import unit.Unit;
import win_condition.WinCondition;

public abstract class HumanPlayer {
	
	protected int credits;
	protected ArrayList<Unit> gameUnits;
	protected Client client;
	protected Game game;
	protected List<Unit> units;
	protected String name;
	protected boolean hasWon;
	
	public HumanPlayer() {
		units = new ArrayList<Unit>();
		client = new Client("localhost", 4008, this);
		hasWon = false;
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

	public void setGame(Game g, int playerNum) {
		this.game = g;
		units.clear();

		if (playerNum == 0) {
			List<Unit> u = g.getRedUnitList();
			for (Unit unit : u)
				units.add(unit);
		}
		else if (playerNum == 1) {
			List<Unit> u = g.getBlueUnitList();
			for (Unit unit : u)
				units.add(unit);
		}

	}
	
	public void setUnits(ArrayList<Unit> u) {
		units.clear();
		for (Unit unit : u)
			units.add(unit);
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
	
	public abstract void updateAvailGameRooms(ArrayList<String> names, ArrayList<String> types);

	public void showGamePanel() {		
	
	}
	
	public abstract void playerSurrendered(String name);
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract void updateUserInfo();
	
	public abstract void updateGameType(WinCondition wc, MapBehavior mb);

	public boolean isExecuting() {
		return false;
	}

	public void setVictory(boolean condition) {
		System.out.println(hasWon);
		hasWon = condition;
		System.out.println(hasWon);
		update();
	}

	public boolean hasWon() {
		return hasWon;
	}

}
