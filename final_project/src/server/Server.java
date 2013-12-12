package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import game_commands.GameCommand;
import server_commands.*;
import shared.Attribute;
import shared.Game;
import shared.Game.WinCondition;
import unit.DemolitionUnit;
import unit.EngineerUnit;
import unit.ExplosivesUnit;
import unit.MeleeUnit;
import unit.RocketUnit;
import unit.SoldierUnit;
import server_commands.CanStartGame;
import server_commands.SendingGame;
import server_commands.SendingUserInfo;
import server_commands.ValidLogin;
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
			System.out.println("Server started, waiting for connections on port " + PORT_NUMBER);

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
	
	public boolean login(String name, String password, ClientHandler ch) {
		if (!database.isValidUser(name, password)) 
			return false;
		else if (database.getUser(name).isLoggedOn())
			return false;
		else {
			database.getUser(name).setLoggedOn(true);
			playerMap.put(name, ch);
			ch.setPlayerName(name);
			ch.sendCommand(new ValidLogin());
			return true;
		}
	}

	public boolean setupNewUser(String name, String password, ClientHandler ch) {
		boolean ret = database.addUser(name, password);
		if (ret) {
			ch.setPlayerName(name);
			playerMap.put(name, ch);
			ch.sendCommand(new ValidLogin());
		}
		return ret;
	}

	public boolean userJoinGame(int gr, ClientHandler ch) {
		if (gamerooms.containsKey(gr))
			return gamerooms.get(gr).addPlayer(ch, gr);
		return false;
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
	
	public boolean newComputerPlayer(int gr) {
		gamerooms.get(gr).setComputerPlayerGame(true);
		ComputerPlayer p = new ComputerPlayer(gr);
		Thread t = new Thread(p);
		t.start();
		return true;
	}
	
	public boolean computerPlayerJoin(int gr, ClientHandler ch) {
		return gamerooms.get(gr).addPlayer(ch, gr);
	}
	
	public boolean setComputerPlayerDifficulty(int gr, int level) {
		return gamerooms.get(gr).setComputerPlayerLevel(level);
	}
	
	public boolean createNewGameRoom(ClientHandler ch) {
		GameRoom g = new GameRoom();
		int index = gamerooms.size();
		gamerooms.put(index, g);
		return gamerooms.get(index).addPlayer(ch, index);
	}
	
	public boolean newUnit(String source, String name, UnitClass type) {
		
		if (type == UnitClass.Melee)
			return database.getUser(source).addUnit(new MeleeUnit(name));
		else if (type == UnitClass.Rocket)
			return database.getUser(source).addUnit(new RocketUnit(name));
		else if (type == UnitClass.Engineer)
			return database.getUser(source).addUnit(new EngineerUnit(name));
		else if (type == UnitClass.Demolition)
			return database.getUser(source).addUnit(new DemolitionUnit(name));
		else if (type == UnitClass.Soldier)
			return database.getUser(source).addUnit(new SoldierUnit(name));
		else if (type == UnitClass.Explosives)
			return database.getUser(source).addUnit(new ExplosivesUnit(name));
		else
			return false;
		}
	
	public boolean sendMessage(String source, int gr, String message) {
		for (ClientHandler ch : gamerooms.get(gr).players)
			ch.sendCommand(new PlayerMessage(source, message));
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
	
	public void updateOpenGameRooms() {
		System.out.println("updating game rooms");
		for (ClientHandler ch : playerMap.values()) {
			ch.sendCommand(new OpenGameRooms(openGameRooms()));
		}
	}
	
	public HashMap<Integer, String> openGameRooms() {
		HashMap<Integer, String> openGameRooms = new HashMap<Integer, String>();
		for (int i=0 ; i <gamerooms.size() ; i++) {
			if (gamerooms.containsKey(i) && gamerooms.get(i).waitingForOpponent())
				openGameRooms.put(i, gamerooms.get(i).playerOne);	 
		}
		return openGameRooms;	
	}
	
	public boolean suspendSession(String source, int gr, ClientHandler ch) {
		database.saveGameSesseion(source, gamerooms.get(gr).game);
		gamerooms.get(gr).cleanOutGameRoom();
		System.out.println("saved session for " + source);
		return true;
	}
	
	public boolean resumeSession(String source, ClientHandler ch) {
		if (database.isSavedGame(source)) {
			Game g = database.getSavedGame(source);
			ch.sendCommand(new SendingGame(g));
			return true;
		}
		else return false;
	}
	
	// returns ComputerPlayer units based on selected difficulty level
	public ArrayList<Unit> generateComputerUnits(int level) {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new SoldierUnit("AA4-001"));
		units.add(new SoldierUnit("B03-222"));
		units.add(new SoldierUnit("C35-3-3"));
		units.add(new SoldierUnit("D49-440"));
		units.add(new SoldierUnit("E0E-555"));
		double modifier = level * 0.5;
		
		if (modifier > 0.0) {
			for (Unit u : units) {
				u.upgrade(Attribute.Strength, modifier);
				u.upgrade(Attribute.Defense, modifier);
				u.upgrade(Attribute.MaxActionPoints, modifier*2);
				u.upgrade(Attribute.MaxHitPoints, modifier*2);
			}
		}
		return units;
	}
	
	public boolean playersAreReady(String playerOne, String playerTwo) {
		if (!(database.hasUser(playerOne) && database.hasUser(playerTwo)))
			return false;
		return database.getUser(playerTwo).isReady() &&
		database.getUser(playerOne).isReady();
	}

	public boolean startGame(String source, int gr, WinCondition wc, MapBehavior map) {
		if (!gamerooms.containsKey(gr))
			return false;
		
		ArrayList<Unit> player1Units;
		ArrayList<Unit> player2Units;
		
		if (gamerooms.get(gr).isComputerPlayerGame) {
			player1Units = database.getUser(source).getUnits();
			player2Units = generateComputerUnits(gamerooms.get(gr).computerPlayerLevel);
			
			Game g = new Game(player1Units, player2Units, wc, map);
			System.out.println("Sending game to gameRoom: " + gr);
			return gamerooms.get(gr).sendNewGame(g);
		}
		else {
			String playerOne = gamerooms.get(gr).playerOne;
			String playerTwo = gamerooms.get(gr).playerTwo;
			if (!playersAreReady(playerOne, playerTwo)) {
				System.out.println("both players must be ready!");
				return false;
			}
			player1Units = database.getUnits(playerOne);
			player2Units = database.getUnits(playerTwo);
			
			Game g = new Game(player1Units, player2Units, wc, map);
			System.out.println("Sending game to gameRoom: " + gr);
			return gamerooms.get(gr).sendNewGame(g);
		}
	}
	
	public void executeGameCommand(int gameRoom, ClientHandler ch, GameCommand gc) {
		if (gamerooms.containsKey(gameRoom))
			gamerooms.get(gameRoom).executeCommand(ch, gc);
	}
	
	public ClientHandler getClientHandler( String player ) {
		return playerMap.get(player);
	}
	
	public UserAccount getUserInfo( String username ) {
		return database.getUser( username );
	}
	
	public boolean playerSurrender(String source, int gr, ClientHandler ch) {
		if (gamerooms.containsKey(gr)) {
			boolean ret = gamerooms.get(gr).playerSurrender(source, ch);
			gamerooms.remove(gr);
			return ret;
		}
		return false;
	}
}
