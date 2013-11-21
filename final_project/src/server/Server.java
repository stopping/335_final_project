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
	
	private void setOpponent(String player, int opponentNum) {
		clients.get(opponentNum).setOpponentName(player);
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
					playerCommands.peekFirst().getCommandType() +
					" to player " + client.playerNumber);
			client.sendCommand(playerCommands.removeFirst());
		}
		
		if (isAIGame && playerToSendCommandsTo % 2 != 0) 
			clients.get(playerToSendCommandsTo).sendCommand(new Command(CommandType.AITurn,
					0, 0, 0, 0, null, null));
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
		
		private void parseCommand(Command com) {
			
			System.out.println("player " + 
					playerNumber + " Commanded " + com.getCommandType());
			
			switch (com.getCommandType()) {

			case EndTurn:
				if (game.isCurrentPlayer(playerNumber))		
					sendCommand(com);
					game.executeCommand(com);
					updateClients(playerNumber, playerCommands, isAIGame);
				break;
				
			case Login:				
				String name = com.getMessage();
				if (!database.hasUser(name)) 
					database.addUser(name, setNewUserUnits(), playerNumber);
				else
					database.getUser(name).resetPlayerNumber(playerNumber);
				database.getUser(name).setLoggedOn(true);
				playerName = name;
				break;
				
			case NewAI:
				AINumber = numPlayers;
				new ComputerPlayer(generateAIUnits());
				isAIGame = true;
				break;
				
			case Ready:
				boolean status = com.getMessage().equals("t") ? true : false;
				database.getUser(playerName).setIsReady(status);
				
			case NewGame:
				break;
				
			case JoinGame:
				setOpponent(com.getMessage(), playerNumber);
				opponentName = com.getMessage();
				break;
				
			case SetWinCondition:
				this.condition = WinCondition.valueOf(com.getMessage());
				break;
				
			case StartGame:
				
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
				
			case Quit:
				//clients.remove(playerNumber);
				// TODO: game.playerForfeit(playerNumer);
				break;

			default:
				if (game.isCurrentPlayer(playerNumber % 2)) {	
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
