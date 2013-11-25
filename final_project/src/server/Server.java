package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;
import shared.Game;
import shared.Game.WinCondition;
import unit.Unit;


/**
 * Class:	Server
 * Purpose:	Wait for new clients and start in new threads. Send
 * 				Commands to all clients when player ends turn.
 */
public class Server implements Runnable {
	
	public static final int PORT_NUMBER = 4009;
	private static ArrayList<ClientHandler> clients;						
	private UserDatabase database;
	private int numPlayers;
	
	public static void main(String[] args) {
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
		new Server();
	}
	
	private Server() {
		clients = new ArrayList<ClientHandler>();
		database = new UserDatabase();
	}

	@Override
	public void run() {
		Socket clientHandle = null;
		ServerSocket sockServer = null;
	   numPlayers = 0;
		
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
	
	private void setOpponent(String playerToSetOpponentOf, String opponentName) {
		int playerNum = database.getUser(playerToSetOpponentOf).getPlayerNumber();
		clients.get(playerNum).setOpponentName(opponentName);
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
	
	// send the clients the current user's commands in FIFO if playing the AI, passes him his turn
	private void updateClients( int playerNumber, Deque<Command> playerCommands, boolean isAIGame) {
		
		int playerToSendCommandsTo = playerNumber % 2 == 0 ? playerNumber+1 : playerNumber-1;
		ClientHandler client = clients.get(playerToSendCommandsTo);

		while (!playerCommands.isEmpty()) {
			System.out.println("sending command: " + 
					playerCommands.peekFirst() +
					" to player " + client.playerNumber);
			client.sendCommand(playerCommands.removeFirst());
		}
		
		if (isAIGame && playerToSendCommandsTo % 2 != 0) 
			clients.get(playerToSendCommandsTo).sendCommand(
					new ClientServerCommand(ClientServerCommandType.NewComputerPlayer, null));
	}
	
	// send the clients the starting game
	private void sendNewGame(int player, int opponent, Game g) {	
		clients.get(player).sendGame(g);
		clients.get(opponent).sendGame(g);
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
		private int AINumber;
		private String opponentName;
		private String playerName;
		private Game game;	
		private boolean isAIGame;
		private Deque<Command> playerCommands;		
		private WinCondition condition;
		
		public ClientHandler(Socket clientSock, int clientNum) {
			
			playerNumber = clientNum;
			playerCommands = new LinkedList<Command>();
			try {
				output = new ObjectOutputStream(clientSock.getOutputStream());
				input = new ObjectInputStream(clientSock.getInputStream());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void resolveClientServerCommand(ClientServerCommand com) {
			
			switch (com.getType()) {
			
			case JoinGame:
				String playerJoining = com.getData().get(0);
				setOpponent(playerJoining, playerName);
				opponentName = playerJoining;
				break;
				
			case Login:
				String name =com.getData().get(0);
				String password = com.getData().get(1);
				if (!database.isValidUser(name, password)) 
					database.addUser(name, password, setNewUserUnits(), playerNumber);
				else {
					// TODO: check the password against database
					database.getUser(name).resetPlayerNumber(playerNumber);
				}
				database.getUser(name).setLoggedOn(true);
				playerName = name;
				break;
				
			case NewComputerPlayer:
				AINumber = numPlayers;
				new ComputerPlayer(generateAIUnits());
				isAIGame = true;
				break;
				
			case PlayerForfeit:
				break;
				
			case Ready:
				database.getUser(playerName).setIsReady(true);
				break;
				
			case StartGame:
				this.condition = WinCondition.valueOf(com.getData().get(0));
				if (isAIGame) {
					game = new Game(database.getUnits(playerName),
							generateAIUnits(), condition);
					sendNewGame(playerNumber, AINumber, game);
				}
				
				else if (database.getUser(playerName).isReady() &&
						database.getUser(opponentName).isReady()) {
					
					game = new Game(database.getUnits(playerName),
							database.getUnits(opponentName), condition);
					sendNewGame(playerNumber, 
							database.getUser(opponentName).getPlayerNumber(), game);
				}
				break;
				
			default:
				break;
			
			}
		}
		
		private void resolveGameCommand(Command com) {
			
			if (game.isCurrentPlayer(playerNumber % 2 )) {

				if (com instanceof EndTurnCommand) {
					sendCommand(com);
					updateClients(playerNumber, playerCommands, isAIGame);
					
				} else  {
					playerCommands.add(com);
					sendCommand(com);
				}
				game.executeCommand((GameCommand)com);
			}
		}

		@Override
		public void run() {
			
			try {					
				while(true) {	
					Command com = (Command) input.readObject();
					
					if (com != null) {
						if (com instanceof ClientServerCommand) {
							resolveClientServerCommand((ClientServerCommand)com);
							
						} else if (com instanceof GameCommand) {
							resolveGameCommand(com);
						}
					}
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void sendGame(Game g) {
			try {
				output.writeObject(g);
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
		
		private void setOpponentName(String opponent) {
			this.opponentName = opponent;
		}
	
	}
	

}