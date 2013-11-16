package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import shared.Command;
import shared.Game;
import shared.Unit;

/**
 * Class:	Server
 * Purpose:	Wait for new clients and start in new threads. Send
 * 				Commands to all clients when game state changes.
 * @author  Kyle Criddle
 */
public class Server implements Runnable {
	
	public static final int PORT_NUMBER = 4009;
	private static ArrayList<ClientHandler> clients;
	private Deque<Command> playerCommands;			// command queue
	private int numPlayers;  									// total number of players including AI
	private int whoseTurn; 										// reference to the index of clients whose turn it is
	private Game game;											// Server's reference to the Game
	private UserDatabase database;
	private ArrayList<String> currentPlayers;
	private ComputerPlayer computerPlayer;
	
	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		clients = new ArrayList<ClientHandler>();
		whoseTurn = 0;
		database = new UserDatabase();
		currentPlayers = new ArrayList<String>();
	}

	@Override
	public void run() {
		Socket clientHandle = null;
		ServerSocket sockServer = null;
		int numPlayers = 0;
		
		try {
			sockServer = new ServerSocket(PORT_NUMBER);
			System.out.println("Server started");

			while(true) {
				
				clientHandle = sockServer.accept();		
				ClientHandler newClient = new ClientHandler(clientHandle, numPlayers);
				Thread newThread = new Thread(newClient);

				clients.add(newClient);
				numPlayers++;
				newThread.start();		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void newComputerPlayer() {
		ComputerPlayer cp = new ComputerPlayer();
		Thread t = new Thread(cp);
		t.start();
		numPlayers++;

	}
	
	private ArrayList<Unit> setNewUserUnits() {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new Unit("Zander"));
		units.add(new Unit("Yvonne"));
		units.add(new Unit("Xavier"));
		units.add(new Unit("Will"));
		units.add(new Unit("Van"));
		return units;
	}
	
	// send all clients the current user's commands
	private void updateClients() {
		
		while (!playerCommands.isEmpty()) {
			for (ClientHandler client : clients) {
				if (client.playerNumber != whoseTurn)
					client.sendCommand(playerCommands.removeFirst());
			}
		}
		
		whoseTurn = (whoseTurn < numPlayers) ? whoseTurn++ : 0;
	}
	
	// send the clients the starting game
	private void sendNewGame() {
		for (ClientHandler client : clients) {
			if (client.playerNumber != whoseTurn)
				client.sendGame();
		}		
	}
	
	/**
	 * Class:	ClientHandler
	 * Purpose:	Handle input from a single client
	 * @param   Socket clientSock:	Socket connection to the client
	 * @author  Kyle Criddle
	 */
	private class ClientHandler implements Runnable {

		private ObjectInputStream input;
		private ObjectOutputStream output;
		private int playerNumber;
		
		public ClientHandler(Socket clientSock, int clientNum) {
			
			playerNumber = clientNum;
			
			try {
				output = new ObjectOutputStream(clientSock.getOutputStream());
				input = new ObjectInputStream(clientSock.getInputStream());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void parseCommand(Command com) {
			
			switch (com.getCommandType()) {

			case EndTurn:
				if (whoseTurn == playerNumber)
					updateClients();
				break;
				
			case Login:
				// TODO: check if there are games already running and 
				// determine if a new game should be started
				
				String name = com.getMessage();
				if (database.hasUser(name)) {
					database.getUnits(name);
					currentPlayers.add(name);
				}
				else {
					database.addUser(name, setNewUserUnits());
				}
				
				break;
				
			case NewAI:
				new ComputerPlayerHandler();
				currentPlayers.add("ComputerPlayer");
				break;
				
			case NewGame:
				break;
				
			case StartGame:
				if (currentPlayers.size() == 2)
					game = new Game(database.getUnits(currentPlayers.get(0)),
							database.getUnits(currentPlayers.get(1)));
				
				else {
					ArrayList<Unit> aiUnits = setNewUserUnits();
					game = new Game(database.getUnits(currentPlayers.get(0)), aiUnits);
				}
				sendNewGame();
				break;
				
			case Quit:
				clients.remove(playerNumber);
				numPlayers--;
				break;

			default:
				playerCommands.add(com);
				sendCommand(com);
				break;
			}
		}

		@Override
		public void run() {
			
			try {					
				while(true) {
							
					Command com = (Command) input.readObject();
					if (com != null)
						parseCommand(com);
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void sendGame() {
			try {
				output.writeObject(game);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (computerPlayer != null) 
				computerPlayer.setGame(game);
		}
		
		private void sendCommand(Command c) {
			try {
				output.writeObject(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	
	private class ComputerPlayerHandler implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	

}
