package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;
import shared.Game;
import unit.*;

/**
 * Class:	Server
 * Purpose:	Wait for new clients and start in new threads. Send
 * 				Commands to all clients when player ends turn.
 */
public class Server implements Runnable {
	
	public static final int PORT_NUMBER = 4009;
	public static final int MAX_PLAYERS = 2;
	private ArrayList<GameRoom> runningGames;
	private UserDatabase database;
	
	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		database = new UserDatabase();
		runningGames = new ArrayList<GameRoom>();
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
	
	private ArrayList<Unit> setNewUserUnits() {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new RocketUnit("Alice"));
		units.add(new Unit("Bob"));
		units.add(new Unit("Charles"));
		units.add(new Unit("Dan"));
		units.add(new Unit("Eric"));
		return units;
	}
	
	public void joinGame(int gameNumber, ClientHandler c) {
		runningGames.get(gameNumber).addPlayer(c);
	}
	
	public void processUser(String name, String password) {
		if (!database.isValidUser(name, password)) 
			database.addUser(name, password, setNewUserUnits());
		database.getUser(name).setLoggedOn(true);
	}
	
	public int numGameRooms() {
		return this.runningGames.size();
	}
	
	public void requestNewGameRoom(ClientHandler c) {
		GameRoom g = new GameRoom();
		g.addPlayer(c);
		runningGames.add(g);
	}
	
	public void playerForfeit(int game, ClientHandler c) {
		runningGames.get(game).removePlayer(c);
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
	
	// send the opponent commands in FIFO. if playing the AI, passes him his turn
	public void updateClients( int gameNumber, int playerNumber,  Deque<Command> playerCommands, boolean isAIGame) {
		
		int playerToSendCommandsTo = playerNumber % 2 == 0 ? playerNumber+1 : playerNumber-1;
		ClientHandler client = runningGames.get(gameNumber).players.get(playerToSendCommandsTo);

		while (!playerCommands.isEmpty()) {
			client.sendCommand(playerCommands.removeFirst());
		}
		
		if (isAIGame && playerToSendCommandsTo == 1) 
			client.sendCommand(new ClientServerCommand(ClientServerCommandType.ComputerTurn, null));
	}
	
	// send the clients the starting game
	public void sendNewGame(Game g, int gameNumber, int computerPlayerLevel) {	
		int numAIS = Server.MAX_PLAYERS - runningGames.get(gameNumber).players.size();

		for (int i=0 ; i<numAIS ; i++) {
			ComputerPlayer p = new ComputerPlayer(gameNumber, computerPlayerLevel);
			Thread t = new Thread(p);
			t.start();
		}
		for (int i=0 ; i< runningGames.get(gameNumber).players.size() ; i++) 
			runningGames.get(gameNumber).players.get(i).sendGame(g);
	}
}