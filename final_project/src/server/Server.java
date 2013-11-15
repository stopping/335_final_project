package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;

import shared.Command;
import shared.Game;

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

	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		clients = new ArrayList<ClientHandler>();
		whoseTurn = 0;
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
	
	private void updateClients() {
		
		while (!playerCommands.isEmpty()) {
			for (ClientHandler client : clients) {
				if (client.playerNumber != whoseTurn)
					client.sendCommand(playerCommands.removeFirst());
			}
		}
		
		whoseTurn = (whoseTurn < numPlayers) ? whoseTurn++ : 0;
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
			if (game == null) {
				game = new Game();
			}
			
			try {
				output = new ObjectOutputStream(clientSock.getOutputStream());
				input = new ObjectInputStream(clientSock.getInputStream());
				output.writeObject(game);
				
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
				break;
				
			case NewAI:
				newComputerPlayer();
				break;
				
			case NewGame:
				break;
				
			case Quit:
				clients.remove(playerNumber);
				numPlayers--;
				break;

			default:
				if (game.isLegalPlay(com, whoseTurn, playerNumber)) {
					playerCommands.add(com);
					sendCommand(com);
				}
				break;
			}
		}

		@Override
		public void run() {
			
			try {					
				while(true) {
					Command com = (Command) input.readObject();
					parseCommand(com);
				}			
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
