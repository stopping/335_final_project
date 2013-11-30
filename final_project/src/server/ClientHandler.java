package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import shared.Attribute;
import shared.Game;
import shared.Game.WinCondition;
import unit.Unit;
import unit.Unit.UnitClass;

import commands.ClientServerCommand;
import commands.ClientServerCommand.ClientServerCommandType;
import commands.Command;
import commands.EndTurnCommand;
import commands.GameCommand;

/**
 * Class:	ClientHandler
 * Purpose:	Handle input from a single client
 * @param   Socket clientSock:	Socket connection to the client
 */
public class ClientHandler implements Runnable {

	private ObjectInputStream input;
	private ObjectOutputStream output;
	private int playerNumber;	// 1 or 0
	private int gameNumber;
	private String opponentName;
	public String playerName;
	private Game game;	
	private boolean isplayingComputer;
	private int computerPlayerLevel;
	private Deque<GameCommand> playerCommands;	
	private Server server;
	private Socket sock;
	
	public ClientHandler(Socket clientSock, Server s) {
		
		playerCommands = new LinkedList<GameCommand>();
		server = s;
		sock = clientSock;
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
			int gameRoom = Integer.parseInt(com.getData().get(1));
			server.joinGame(gameRoom, this);
			gameNumber = gameRoom;
			opponentName = playerJoining;
			playerNumber = 1;
			break;
			
		case Login:
			String name =com.getData().get(0);
			String password = com.getData().get(1);
			if (!server.processUser(name, password)) {
				sendCommand(new ClientServerCommand(
						ClientServerCommandType.IllegalOption, 
						new String[] {"Incorrect Username or Password"}));
				break;
			}
			playerName = name;
			break;
			
		case NewComputerPlayer:
			computerPlayerLevel = Integer.parseInt(com.getData().get(0));
			isplayingComputer = true;
			break;
			
		case ComputerPlayerJoin:
			gameNumber = Integer.parseInt(com.getData().get(0));
			server.joinGame(gameNumber, this);
			playerNumber = 1;
			playerName = "Computer";
			break;
			
		case ModifyUnit:
			String unitName = com.getData().get(0);
			Attribute attr = Attribute.valueOf(com.getData().get(1));
			if (!server.modifyUnit(playerName, unitName, attr))
				sendCommand(new ClientServerCommand(
						ClientServerCommandType.IllegalOption, 
						new String[] {"Cannot modify unit"}));
			break;
			
		case NewGame:
			this.gameNumber = server.numGameRooms();
			server.requestNewGameRoom(this);
			break;
			
		case NewUnit:
			String uName = com.getData().get(0);
			UnitClass type = UnitClass.valueOf(com.getData().get(1));
			server.newUnit(playerName, uName, type);
			break;
			
		case NewUser:
			String nm =com.getData().get(0);
			String pw = com.getData().get(1);
			server.newUser(nm, pw);
			playerName = nm;
			break;
			
		case Message:
			server.sendMessage(gameNumber, playerName, com, String.valueOf(playerNumber));
			break;
			
		case PlayerForfeit:
			server.playerForfeit(gameNumber, this);
			break;
			
		case Ready:
			server.userSetReady(playerName);
			break;
			
		case ResumeSession:
			Game g = server.getGameSession(playerName);
			if (g != null)
				sendCommand(new ClientServerCommand(ClientServerCommandType.ResumeSession, null));
				sendGame(g);
			
		case StartGame:
			WinCondition condition = WinCondition.valueOf(com.getData().get(0));
			if (isplayingComputer) {
				ArrayList<Unit> userUnits = server.getUserUnits(playerName);
				game = new Game(userUnits, ComputerPlayer.generateAIUnits(computerPlayerLevel), condition);
				server.sendNewGame(game, gameNumber, computerPlayerLevel);
			}
			
			else if (server.playersAreReady(playerName, opponentName)) {
				ArrayList<Unit> player1Units = server.getUserUnits(playerName);
				ArrayList<Unit> player2Units = server.getUserUnits(opponentName);

				game = new Game(player1Units, player2Units, condition);
				server.sendNewGame(game, gameNumber, computerPlayerLevel);
			}
			break;
			
		case SuspendSession:
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!playerCommands.isEmpty()) {
				game.executeCommand((playerCommands.removeFirst()));
			}
			server.saveGameSession(playerName, game);
			System.out.println("saved session for " + playerName);
			break;
			
		default:
			break;
		}
	}
	
	private void resolveGameCommand(GameCommand com) {
		
		if (game.isCurrentPlayer(playerNumber)) {

			if (com instanceof EndTurnCommand) {
				sendCommand(com);
				playerCommands.add(com);
				server.updateClients(gameNumber, playerNumber, playerCommands, isplayingComputer);
				
			} else  {
				playerCommands.add(com);
				sendCommand(com);
			}
			if (game.executeCommand((GameCommand)com) == false) {
				System.out.println("player: " + playerName + " gave illegal option");
				sendCommand(new ClientServerCommand(ClientServerCommandType.IllegalOption, null));
				playerCommands.removeLast();
			}
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
						resolveGameCommand((GameCommand)com);
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendGame(Game g) {
		try {
			this.game = g;
			output.writeObject(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendCommand(Command c) {
		try {
			output.writeObject(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setOpponent(String n) {
		this.opponentName = n;
	}

}
