package server;

import game_commands.EndTurnCommand;
import game_commands.GameCommand;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import server_commands.ComputerTurn;
import server_commands.IllegalOption;
import server_commands.SendingGame;
import shared.Game;

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
	protected boolean isComputerPlayerGame;
	
	public GameRoom() {
		players = new ArrayList<ClientHandler>();
		playerCommands = new LinkedList<GameCommand>();
	}
	
	public boolean addPlayer(ClientHandler p, int gameRoom) {
		if (!isFull()) {
			players.add(p);
			if (!isFull())
				playerOne = p.playerName;
			else 
				playerTwo = p.playerName;
			p.setGameRoom(gameRoom);
			System.out.println("new player goined game room " + gameRoom);
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
	
	public boolean waitingForOpponent() {
		return players.size() == 1;
	}
	
	public void setComputerPlayerGame(boolean value) {
		this.isComputerPlayerGame = value;
	}
	
	public boolean sendNewGame(Game g) {
		this.game = g;
		for (ClientHandler ch : players )
			ch.sendCommand(new SendingGame());
		for (ClientHandler ch : players )
			ch.sendGame(g);
		return true;
	}
	
	public void executeCommand(ClientHandler ch, GameCommand gc) {
		
		if (!game.isWon() && game.isCurrentPlayer(whoseTurn)) {

			if (gc instanceof EndTurnCommand) {
				gc.executeOn(game);
				players.get(whoseTurn).sendCommand(gc);
				playerCommands.add(gc);
				updateOpponents();
				
			} else  {
				
				if (game.executeCommand(gc)) {
					playerCommands.add(gc);
					players.get(whoseTurn).sendCommand(gc);
					if(game.isWon() && whoseTurn != game.getWinner()) {
						gc = new EndTurnCommand();
						gc.executeOn(game);
						players.get(whoseTurn).sendCommand(gc);
						playerCommands.add(gc);
						updateOpponents();
					}
				} else {
					System.out.println("player gave illegal option");
					players.get(whoseTurn).sendCommand(new IllegalOption());
				}
			}
		}
	}
	
	private void updateOpponents() {	
		int i = whoseTurn == 0 ? 1 : 0;
		
		ClientHandler opp = players.get(i);
		while(!playerCommands.isEmpty()) {
			opp.sendCommand(playerCommands.removeFirst());
		}
		if (isComputerPlayerGame && whoseTurn == 0)
			opp.sendCommand(new ComputerTurn());
		
		whoseTurn = whoseTurn == 0 ? 1 : 0;
		System.out.println("sent updates to player " + whoseTurn);
	}

}