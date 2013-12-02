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
		
	public int joinGame(String player, ClientHandler c) {
		for (int i=0 ; i<gameRooms.size() ; i++)
			if (gameRooms.get(i).waitingForOpponent())
				if (gameRooms.get(i).players.get(0).playerName.equals(player)) {
					gameRooms.get(i).addPlayer(c);
					System.out.println("joining game with " + player);
					gameRooms.get(i).players.get(0).sendCommand(
							new ClientServerCommand(ClientServerCommandType.StartGame, null));
					gameRooms.get(i).players.get(1).sendCommand(
							new ClientServerCommand(ClientServerCommandType.StartGame, null));
					return i;
				}
		return -1;
	}
	
	public boolean processUser(String name, String password) {
		if (!database.isValidUser(name, password)) 
			return false;
		else if (database.getUser(name).isLoggedOn())
			return false;
		else {
			database.getUser(name).setLoggedOn(true);
			return true;
		}
	}
	
	public void logoutUser(String name) {
		if (database.getUser(name).isLoggedOn())
			database.getUser(name).setLoggedOn(false);
	}
	
	public boolean newUser(String name, String password) {
		return database.addUser(name, password);
	}
	
	public int numGameRooms() {
		return this.gameRooms.size();
	}
	
	public void requestNewGameRoom(ClientHandler c) {
		GameRoom g = new GameRoom();
		g.addPlayer(c);
		gameRooms.add(g);
	}
	
	public String[] getUnitInfo(String player) {
		String ret[] = new String[10];
		ArrayList<Unit> units = database.getUnits(player);
		int i = 0;
		for (Unit u: units) {
			ret[i] = u.getName();
			ret[i+1] = u.toString();
			i+=2;
		}
		return ret;
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
	
	// store the user's game session
	public void saveGameSession(String name, Game g) {
		database.saveGameSesseion(name, g);
	}
	
	public Game getGameSession(String name) {
		if (database.isSavedGame(name))
			return database.getSavedGame(name);
		else
			return null;
	}
	
	// starts a new ComputerPlayer in its own thread
	public void addComputerPlayer(String player, int computerPlayerLevel) {
		ComputerPlayer p = new ComputerPlayer(player, computerPlayerLevel);
		Thread t = new Thread(p);
		t.start();
	}
	
	public String[] getOpenGameRooms() {
		String ret[] = new String[gameRooms.size()];
		int index = 0;
		for (GameRoom g : gameRooms)
			if (g.waitingForOpponent()) {
				ret[index] = g.players.get(0).playerName;
				index++;
			}
		return ret;
	}
	
	public void updateClientGameRoomStatus() {
		for (GameRoom g : gameRooms)
			for (ClientHandler handler : g.players)  {
				handler.sendCommand(new ClientServerCommand(
						ClientServerCommandType.OpenGameRooms, getOpenGameRooms()));
			}
			
	}
	
	// send the opponent commands in FIFO. if playing the AI, passes him his turn
	public void updateClients( int gameNumber, int playerNumber,  Deque<GameCommand> playerCommands, boolean isAIGame) {
		
		int playerToSendCommandsTo = playerNumber % 2 == 0 ? playerNumber+1 : playerNumber-1;
		ClientHandler client = gameRooms.get(gameNumber).players.get(playerToSendCommandsTo);

		while (!playerCommands.isEmpty()) {
			client.sendCommand(playerCommands.removeFirst());
		}
		
		if (isAIGame && playerToSendCommandsTo == 1) 
			client.sendCommand(new ClientServerCommand(ClientServerCommandType.ComputerTurn, null));
	}
	
	// send the clients the starting game
	public void sendNewGame(Game g, int gameNumber, String player, int computerPlayerLevel) {	
		System.out.println("gameRoomSize: " + gameRooms.get(gameNumber).players.size());
		System.out.println("Sending game to gameRoom: " + gameNumber);
		
		// wait until the room is full -- This is a hasty avoidance of possibly a race condition
		if (!gameRooms.get(gameNumber).isFull()) {
			addComputerPlayer(player, computerPlayerLevel);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (ClientHandler handler : gameRooms.get(gameNumber).players)  {
			System.out.println("gameRoomSize: " + gameRooms.get(gameNumber).players.size());
			handler.sendCommand(new ClientServerCommand(
					ClientServerCommandType.SendingGame, null));
			handler.sendGame(g);
		}
	}
}
