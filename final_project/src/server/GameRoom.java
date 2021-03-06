package server;

import game.Game;
import game_commands.EndTurnCommand;
import game_commands.GameCommand;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import map.MapBehavior;
import map.StandardMap;
import server_commands.ComputerDifficultySet;
import server_commands.ComputerTurn;
import server_commands.DeclareVictory;
import server_commands.IllegalOption;
import server_commands.SendingGame;
import server_commands.Surrender;
import unit.Unit;
import win_condition.DeathmatchCondition;
import win_condition.WinCondition;

/**
 * Class: GameRoom
 * Purpose: Provide a collection of client handlers 
 * 			that will participate in a game.
 */
public class GameRoom {

	public ArrayList<ClientHandler> players;
	protected Deque<GameCommand> playerCommands;	
	protected String playerOne;
	protected String playerTwo;
	protected Game game;
	private int whoseTurn;
	protected int computerPlayerLevel;
	protected boolean isComputerPlayerGame;
	private WinCondition wc;
	private MapBehavior mb;
	private ArrayList<Unit> player1Units;
	private ArrayList<Unit> player2Units;
	
	public GameRoom() {
		players = new ArrayList<ClientHandler>();
		playerCommands = new LinkedList<GameCommand>();
		wc = new DeathmatchCondition();
		mb = new StandardMap();
	}
	
	public void setWinCondition(WinCondition w) {
		wc = w;
	}
	
	public void setMap(MapBehavior m) {
		mb = m;
	}
	
	public boolean addPlayer(ClientHandler p, int gameRoom) {
		if (!isFull()) {
			players.add(p);
			if (players.size() <= 1)
				playerOne = p.playerName;
			else 
				playerTwo = p.playerName;
			p.setGameRoom(gameRoom);
			System.out.println("new player joined game room " + gameRoom);
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
	
	public boolean setComputerPlayerLevel(int level) {
		if (isComputerPlayerGame) {
			computerPlayerLevel = level;
			players.get(1).sendCommand(new ComputerDifficultySet(level));
			return true;
		}
		return false;
	}
	
	public boolean isEmpty() {
		return players.size() == 0;
	}
	
	public boolean isFull() {
		return players.size() == Server.MAX_PLAYERS ? true : false;
	}	
	
	public boolean waitingForOpponent() {
		return players.size() == 1 && !isComputerPlayerGame;
	}
	
	public void setComputerPlayerGame(boolean value) {
		this.isComputerPlayerGame = value;
	}
	
	public boolean sendNewGame(Game g) {
		this.game = g;	
		int player = 0;
		for (ClientHandler ch : players ) {
			ch.sendCommand(new SendingGame(g, player));
			player++;
		}
		return true;
	}
	
	public void executeCommand(ClientHandler ch, GameCommand gc) {
		
		if (!game.isWon() && ch.equals(players.get(whoseTurn))) {
			System.out.println("Currently player " + whoseTurn + "\'s turn");
			if (gc instanceof EndTurnCommand) {
				gc.executeOn(game);
				players.get(whoseTurn).sendCommand(gc);
				playerCommands.add(gc);
				updateOpponents();
				
			} else  {
				
				if (game.executeCommand(gc)) {
					playerCommands.add(gc);
					players.get(whoseTurn).sendCommand(gc);
					if(game.isWon()) {
						players.get(game.getWinner()).sendCommand(new DeclareVictory(true));
						gc = new EndTurnCommand();
						gc.executeOn(game);
						players.get(whoseTurn).sendCommand(gc);
						playerCommands.add(gc);
						updateOpponents();
						players.get((game.getWinner()+1) % 2).sendCommand(new DeclareVictory(false));
					}
				} else {
					System.out.println("player gave illegal option");
					players.get(whoseTurn).sendCommand(new IllegalOption());
				}
			}
		}
		else {
			
		}
	}
	
	private void updateOpponents() {	
		int i = whoseTurn == 0 ? 1 : 0;
		
		ClientHandler opp = players.get(i);
		while(!playerCommands.isEmpty()) {
			opp.sendCommand(playerCommands.removeFirst());
			if (!isComputerPlayerGame || (isComputerPlayerGame &&whoseTurn == 1))
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (isComputerPlayerGame && whoseTurn == 0)
			opp.sendCommand(new ComputerTurn());
		
		whoseTurn = whoseTurn == 0 ? 1 : 0;
		System.out.println("sent updates to player " + whoseTurn);
	}
	
	public boolean playerSurrender(String playerSurrendering, ClientHandler ch) {
		game.surrender();
		players.get(game.getWinner()).sendCommand(new DeclareVictory(true));
		GameCommand gc = new EndTurnCommand();
		gc.executeOn(game);
		players.get(whoseTurn).sendCommand(gc);
		playerCommands.add(gc);
		updateOpponents();
		players.get((game.getWinner()+1) % 2).sendCommand(new DeclareVictory(false));
		System.out.println("GAME IS WON " + game.isWon());
		for (ClientHandler c : players)
			if (!c.equals(ch)) {
				c.sendCommand(new Surrender(playerSurrendering));
			}
		cleanOutGameRoom();		
		return true;
	}
	
	public void cleanOutGameRoom() {
		for (ClientHandler ch : players)
			players.remove(ch);
	}
	
	public String toString() {
		return playerOne + ": " + wc.toString();
	}
	
	public Game getGame() {
		return game;
	}

	public WinCondition getWinCondition() {
		return wc;
	}
	
	public MapBehavior getMap() {
		return mb;
	}

}