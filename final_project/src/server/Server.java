package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import shared.Command;
import shared.Command.CommandType;
import shared.Game;
import shared.Game.WinCondition;
import shared.Unit;

/**
 * Class:	Server
 * Purpose:	Wait for new clients and start in new threads. Send
 * 				Commands to all clients when player ends turn.
 */
public class Server implements Runnable {
	
	public static final int PORT_NUMBER = 4009;
	private static ArrayList<ClientHandler> clients;
	private Deque<Command> playerCommands;					
	private Game game;											
	private UserDatabase database;
	private ArrayList<ArrayList<Unit>> currentPlayers;
	private boolean isAIGame;
	
	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		clients = new ArrayList<ClientHandler>();
		database = new UserDatabase();
		currentPlayers = new ArrayList<ArrayList<Unit>>();
		playerCommands = new LinkedList<Command>();
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
				System.out.println("new player connected");
				newThread.start();		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// TODO: make a process for creating a new army for human player and AI
	private ArrayList<Unit> setNewUserUnits() {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new Unit("Zander"));
		units.add(new Unit("Yvonne"));
		units.add(new Unit("Xavier"));
		units.add(new Unit("Will"));
		units.add(new Unit("Van"));
		return units;
	}
	
	private ArrayList<Unit> generateAIUnits() {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new Unit("Zander"));
		units.add(new Unit("Yvonne"));
		units.add(new Unit("Xavier"));
		units.add(new Unit("Will"));
		units.add(new Unit("Van"));
		return units;
	}
	
	// send all clients the current user's commands in FIFO
	// if playing the AI, passes him his turn
	private void updateClients() {
		
		while (!playerCommands.isEmpty()) {
			for (ClientHandler client : clients) {
				if (game.isCurrentPlayer(client.playerNumber)) {
					System.out.println("sending command: " + 
							playerCommands.peekFirst().getCommandType() +
							" to player " + client.playerNumber);
					client.sendCommand(playerCommands.removeFirst());
				}
			}
		}
		
		if (isAIGame && game.isCurrentPlayer(1)) 
			clients.get(1).sendCommand(new Command(CommandType.AITurn,
					0, 0, 0, 0, null, null));
	}
	
	// send the clients the starting game
	private void sendNewGame() {
		for (ClientHandler client : clients) {
				client.sendGame();
		}		
	}
	
	/**
	 * Class:	ClientHandler
	 * Purpose:	Handle input from a single client
	 * @param   Socket clientSock:	Socket connection to the client
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
			
			System.out.println("player " + 
					playerNumber + " Commanded " + com.getCommandType());
			
			switch (com.getCommandType()) {

			case EndTurn:
				if (game.isCurrentPlayer(playerNumber))		
					sendCommand(com);
					game.executeCommand(com);
					updateClients();
				break;
				
			case Login:				
				String name = com.getMessage();
				if (!database.hasUser(name)) 
					database.addUser(name, setNewUserUnits());
				currentPlayers.add(database.getUnits(name));
				break;
				
			case NewAI:
				ArrayList<Unit> computerPlayerArmy = generateAIUnits();
				currentPlayers.add(computerPlayerArmy);
				new ComputerPlayer(computerPlayerArmy);
				isAIGame = true;
				break;
				
			case NewGame:
				break;
				
			case StartGame:
				if (currentPlayers.size() == 2)
					game = new Game(currentPlayers.get(0), currentPlayers.get(1),
							WinCondition.CTF);
				sendNewGame();
				break;
				
			case Quit:
				clients.remove(playerNumber);
				// TODO: game.playerForfeit(playerNumer);
				break;

			default:
				if (game.isCurrentPlayer(playerNumber)) {	
					playerCommands.add(com);
					sendCommand(com);
					game.executeCommand(com);
					break;
				}
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
		}
		
		private void sendCommand(Command c) {
			try {
				output.writeObject(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	

}
