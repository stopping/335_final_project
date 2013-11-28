package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;
import shared.Attribute;
import shared.Game;
import shared.Occupant;
import unit.*;
import unit.Unit.UnitClass;

/**
 * Class:	Server
 * Purpose:	Wait for new clients and start in new threads. Send
 * 				Commands to all clients when player ends turn.
 */
public class Server implements Runnable {
	
	public static final int PORT_NUMBER = 4009;
	public static final int MAX_PLAYERS = 2;
	private ArrayList<GameRoom> gameRooms;
	private UserDatabase database;
	
	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		database = new UserDatabase();
		gameRooms = new ArrayList<GameRoom>();
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
				System.out.println("new player connected");
				newThread.start();		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void joinGame(int gameNumber, ClientHandler c) {
		gameRooms.get(gameNumber).addPlayer(c);
	}
	
	public boolean processUser(String name, String password) {
		if (!database.isValidUser(name, password)) 
			return false;
		else {
			database.getUser(name).setLoggedOn(true);
			return true;
		}
	}
	
	public void newUser(String name, String password) {
		database.addUser(name, password);
	}
	
	public int numGameRooms() {
		return this.gameRooms.size();
	}
	
	public void requestNewGameRoom(ClientHandler c) {
		GameRoom g = new GameRoom();
		g.addPlayer(c);
		gameRooms.add(g);
	}
	
	public void playerForfeit(int game, ClientHandler c) {
		gameRooms.get(game).removePlayer(c);
//		runningGames.get(gameNumber).players.get(0).sendCommand(
//				new ClientServerCommand(ClientServerCommandType.OpponentForfeit, null);
	}
	
	public void userSetReady(String player) {
		database.getUser(player).setIsReady(true);
	}
	
	public ArrayList<Unit> getUserUnits(String player) {
		return database.getUnits(player);
	}
	
	public boolean playersAreReady(String p1, String p2) {
		return database.getUser(p1).isReady() &&
		database.getUser(p2).isReady();
	}	
	
	public boolean modifyUnit(String player, String unit, Attribute a) {
		UserAccount user = database.getUser(player);
		for (Unit u : user.getUnits() ) {
			Occupant o = (Occupant)u;
			if (o.getName().equals(unit)) {
				// if (user.getNumCredits() ) 	TODO: decide cost for things, how we check it & how to inform the player
				return true;
			}
		}
		return false;
	}
	
	public void newUnit(String player, String unit, UnitClass type) {
		UserAccount user = database.getUser(player);
		user.addUnit(unit, type);
	}
	
	// send chat message to all members in game room
	public void sendMessage(int gameNumber, String player, ClientServerCommand com, String playerNumber) {
		for (int i =0 ; i < gameRooms.get(gameNumber).players.size() ; i++)
			gameRooms.get(gameNumber).players.get(i).sendCommand(new ClientServerCommand(
					ClientServerCommandType.Message, new String[] { player,  playerNumber, com.getData().get(0) }));
	}
	
	// send the opponent commands in FIFO. if playing the AI, passes him his turn
	public void updateClients( int gameNumber, int playerNumber,  Deque<Command> playerCommands, boolean isAIGame) {
		
		int playerToSendCommandsTo = playerNumber % 2 == 0 ? playerNumber+1 : playerNumber-1;
		ClientHandler client = gameRooms.get(gameNumber).players.get(playerToSendCommandsTo);

		while (!playerCommands.isEmpty()) {
			client.sendCommand(playerCommands.removeFirst());
		}
		
		if (isAIGame && playerToSendCommandsTo == 1) 
			client.sendCommand(new ClientServerCommand(ClientServerCommandType.ComputerTurn, null));
	}
	
	// send the clients the starting game
	public void sendNewGame(Game g, int gameNumber, int computerPlayerLevel) {	
		int numAIS = Server.MAX_PLAYERS - gameRooms.get(gameNumber).players.size();

		for (int i=0 ; i<numAIS ; i++) {
			ComputerPlayer p = new ComputerPlayer(gameNumber, computerPlayerLevel);
			Thread t = new Thread(p);
			t.start();
		}
		for (int i=0 ; i< gameRooms.get(gameNumber).players.size() ; i++)  {
			gameRooms.get(gameNumber).players.get(i).sendCommand(new ClientServerCommand(
					ClientServerCommandType.SendingGame, null));
			gameRooms.get(gameNumber).players.get(i).sendGame(g);
		}
	}
}