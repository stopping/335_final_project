package server;

import game_commands.GameCommand;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import server_commands.CanStartGame;
import server_commands.SendingGame;
import server_commands.ValidLogin;
import shared.Game;
import shared.Game.WinCondition;
import shared.MapBehavior;
import unit.Unit;
import unit.Unit.UnitClass;

public class Server implements Runnable {
	
	public static final int PORT_NUMBER = 4009;
	public static final int MAX_PLAYERS = 2;
	private UserDatabase database;
	private HashMap<String, ClientHandler> playerMap;
	private HashMap<Integer, GameRoom> gamerooms;
	
	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		database = new UserDatabase();
		playerMap = new HashMap<String, ClientHandler>();
		gamerooms = new HashMap<Integer, GameRoom>();
	}

	@Override
	public void run() {
		Socket clientHandle = null;
		ServerSocket sockServer = null;
		
		try {
			sockServer = new ServerSocket(PORT_NUMBER);
			System.out.println("Server started");

			while(true) {	
				clientHandle = sockServer.accept();		
				ClientHandler newClient = new ClientHandler(clientHandle, this);
				Thread newThread = new Thread(newClient);
				System.out.println("New Socket Opened");
				newThread.start();		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean login(String source, String name, String password, ClientHandler ch) {
		if (!database.isValidUser(name, password)) 
			return false;
		else if (database.getUser(name).isLoggedOn())
			return false;
		else {
			database.getUser(name).setLoggedOn(true);
			ch.setPlayerName(source);
			ch.sendCommand(new ValidLogin());
			return true;
		}
	}

	public boolean setupNewUser(String source, String password, ClientHandler ch) {
		boolean ret = database.addUser(source, password);
		if (ret) {
			ch.setPlayerName(source);
			ch.sendCommand(new ValidLogin());
		}
		return ret;
	}

	public boolean userJoinGame(int gr, ClientHandler ch) {
		return gamerooms.get(gr).addPlayer(ch, gr);
	}
	
	public boolean logout(String source, int gr, ClientHandler ch) {
		if (database.getUser(source).isLoggedOn()) {
			database.getUser(source).setLoggedOn(false);
			database.getUser(source).setIsReady(false);
		}
		return true;
	}
	
	public boolean removeClient(String source, int gr, ClientHandler ch) {
		playerMap.remove(source);
		gamerooms.get(gr).removePlayer(ch);
		ch.disconnect();
		return true;
	}
	
	public boolean newComputerPlayer(int gr, int level) {
		gamerooms.get(gr).setComputerPlayerGame(true);
		ComputerPlayer p = new ComputerPlayer(gr, level);
		Thread t = new Thread(p);
		t.start();
		return true;
	}
	
	public boolean computerPlayerJoin(int gr, ClientHandler ch) {
		
		
		return gamerooms.get(gr).addPlayer(ch, gr);
	}
	
	public boolean createNewGameRoom(ClientHandler ch) {
		GameRoom g = new GameRoom();
		int index = gamerooms.size();
		gamerooms.put(index, g);
		return gamerooms.get(index).addPlayer(ch, index);
	}
	
	public boolean newUnit(String source, String name, UnitClass type) {
		return database.getUser(source).addUnit(name, type);
	}
	
	public boolean sendMessage(String source, int gr, String message) {
//		for (ClientHandler ch : gamerooms.get(gr).players)
//			ch.sendCommand(new PlayerMessage(source, message));
		return true;
	}
	
	public boolean setReady(String source, ClientHandler ch) {
		if (database.hasUser(source)) {
			database.getUser(source).setIsReady(true);
			ch.sendCommand(new CanStartGame());
			return true;
		}
		else return false;
	}
	
	public boolean getOpenGameRooms(ClientHandler ch) {
		HashMap<Integer, String> openGameRooms = new HashMap<Integer, String>();
		for (int i=0 ; i <gamerooms.size() ; i++)
			if (gamerooms.get(i).waitingForOpponent())
				openGameRooms.put(i, gamerooms.get(i).playerOne);
		//ch.sendCommand(new OpenGameRooms(openGameRooms));		
		return true;	
	}
	
	public boolean suspendSession(String source, ClientHandler ch) {
//		while (!ch.playerCommands.isEmpty()) {
//			ch.game.executeCommand((ch.playerCommands.removeFirst()));
//		}
//		database.saveGameSesseion(source, ch.game);
		System.out.println("saved session for " + source);
		return true;
	}
	
	public boolean resumeSession(String source, ClientHandler ch) {
		if (database.isSavedGame(source)) {
			Game g = database.getSavedGame(source);
			ch.sendCommand(new SendingGame());
			ch.sendGame(g);
			return true;
		}
		else return false;
	}
	
	public boolean startGame(String source, int gr, WinCondition wc, MapBehavior map) {
		ArrayList<Unit> player1Units = database.getUnits(source);
		ArrayList<Unit> player2Units;
		
		if (gamerooms.get(gr).isComputerPlayerGame) 
			player2Units = ComputerPlayer.generateAIUnits(3);
		
		else {
			String playerTwo = gamerooms.get(gr).playerTwo;
			player2Units = database.getUnits(playerTwo);
		}
		
		Game g = new Game(player1Units, player2Units, wc, map);
		System.out.println("Sending game to gameRoom: " + gr);
		return gamerooms.get(gr).sendNewGame(g);
	}
	
	public void executeGameCommand(int gameRoom, ClientHandler ch, GameCommand gc) {
		gamerooms.get(gameRoom).executeCommand(ch, gc);
	}
}
