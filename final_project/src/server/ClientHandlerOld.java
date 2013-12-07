package server;

import game_commands.EndTurnCommand;
import game_commands.GameCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import client_commands.ClientServerCommandOld;
import client_commands.ClientCommand;
import client_commands.ClientServerCommandOld.ClientServerCommandType;

import shared.Attribute;
import shared.Game;
import shared.Game.WinCondition;
import unit.Unit;
import unit.Unit.UnitClass;

/**
 * Class:	ClientHandler
 * Purpose:	Handle input from a single client
 * @param   Socket clientSock:	Socket connection to the client
 */
public class ClientHandlerOld implements Runnable {

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
	
	private void resolveClientServerCommand(ClientServerCommandOld com) {
		
		switch (com.getType()) {
		
		case JoinGame:
			String playerJoining = com.getData().get(0);
			gameNumber = server.joinGame(playerJoining, this);
			if (gameNumber == -1)
				sendCommand(new ClientServerCommandOld(ClientServerCommandType.IllegalOption, 
						new String[] {"Cannot join game"}));
			else {
				server.userSetReady(playerName);			// should be set independently
				opponentName = playerJoining;
				playerNumber = 1;
				server.updateClientGameRoomStatus();
			}
			
			break;
			
		case Login:
			String name =com.getData().get(0);
			String password = com.getData().get(1);
			if (!server.processUser(name, password)) {
				sendCommand(new ClientServerCommandOld(
						ClientServerCommandType.IllegalOption, 
						new String[] {"Incorrect Username or Password"}));
				break;
			}
			sendCommand(new ClientServerCommandOld(ClientServerCommandType.ValidLogin, null));
			//sendCommand(new ClientServerCommand(ClientServerCommandType.UnitInfo, server.getUnitInfo(playerName)));
			playerName = name;
			break;
			
		case Logout:
			System.out.println("logging out " + playerName);
			server.logoutUser(playerName);
			//disconnect();
			break;
			
		case ClientExit:
			System.out.println("killing client handler for  " + playerName);
			server.logoutUser(playerName);
			disconnect();
			break;
			
		case NewComputerPlayer:
			computerPlayerLevel = Integer.parseInt(com.getData().get(0));
			isplayingComputer = true;
			server.addComputerPlayer(playerName, computerPlayerLevel);
			break;
			
		case ComputerPlayerJoin:
			gameNumber = server.joinGame(com.getData().get(0), this);
			playerNumber = 1;
			playerName = "Computer";
			break;
			
		case ModifyUnit:
			String unitName = com.getData().get(0);
			Attribute attr = Attribute.valueOf(com.getData().get(1));
			if (!server.modifyUnit(playerName, unitName, attr))
				sendCommand(new ClientServerCommandOld(
						ClientServerCommandType.IllegalOption, 
						new String[] {"Cannot modify unit"}));
			break;
			
		case NewGame:
			this.gameNumber = server.numGameRooms();
			server.requestNewGameRoom(this);
			server.userSetReady(playerName);				// should be set independently
			server.updateClientGameRoomStatus();
			break;
			
		case NewUnit:
			String uName = com.getData().get(0);
			UnitClass type = UnitClass.valueOf(com.getData().get(1));
			server.newUnit(playerName, uName, type);
			break;
			
		case NewUser:
			String nm =com.getData().get(0);
			String pw = com.getData().get(1);
			if (!server.newUser(nm, pw))
				sendCommand(new ClientServerCommandOld(ClientServerCommandType.IllegalOption, 
						new String[] {"Usename taken!"}));
			else {
				server.processUser(nm, pw);
				sendCommand(new ClientServerCommandOld(ClientServerCommandType.ValidLogin, null));
				playerName = nm;
			}
			break;
			
		case Message:
			server.sendMessage(gameNumber, playerName, com, String.valueOf(playerNumber));
			break;
			
		case PlayerForfeit:
			server.playerForfeit(gameNumber, this);
			break;
			
		case Ready:
			server.userSetReady(playerName);
			if (server.playersAreReady(playerName, opponentName)) {
				server.sendCanStartGame(gameNumber);
			}
			break;
			
		case ResumeSession:
			Game g = server.getGameSession(playerName);
			if (g != null)
				sendCommand(new ClientServerCommandOld(ClientServerCommandType.ResumeSession, null));
				sendGame(g);
			
		case StartGame:
			WinCondition condition = WinCondition.valueOf(com.getData().get(0));
			if (isplayingComputer && server.playerIsReady(playerName)) {
				ArrayList<Unit> userUnits = server.getUserUnits(playerName);
				game = new Game(userUnits, ComputerPlayer.generateAIUnits(computerPlayerLevel), condition);
				server.sendNewGame(game, gameNumber, playerName, computerPlayerLevel);
			}
			
			else if (server.playersAreReady(playerName, opponentName)) {
				ArrayList<Unit> player1Units = server.getUserUnits(playerName);
				ArrayList<Unit> player2Units = server.getUserUnits(opponentName);

				game = new Game(player1Units, player2Units, condition);
				server.sendNewGame(game, gameNumber, playerName, computerPlayerLevel);
			}
			break;
			
		case SuspendSession:
			while (!playerCommands.isEmpty()) {
				game.executeCommand((playerCommands.removeFirst()));
			}
			server.saveGameSession(playerName, game);
			System.out.println("saved session for " + playerName);
			disconnect();
			break;
			
		case OpenGameRooms:
			sendCommand(new ClientServerCommandOld(ClientServerCommandType.OpenGameRooms,
					server.getOpenGameRooms()));
			break;
			
		default:
			break;
		}
	}
	
	private void resolveGameCommand(GameCommand com) {
		
		if (!game.isWon() && game.isCurrentPlayer(playerNumber)) {

			if (com instanceof EndTurnCommand) {
				com.executeOn(game);
				sendCommand(com);
				playerCommands.add(com);
				server.updateClients(gameNumber, playerNumber, playerCommands, isplayingComputer);
				
			} else  {
				
				if (game.executeCommand((GameCommand)com)) {
					playerCommands.add(com);
					sendCommand(com);
					if(game.isWon() && playerNumber != game.getWinner()) {
						com = new EndTurnCommand();
						com.executeOn(game);
						sendCommand(com);
						playerCommands.add(com);
						server.updateClients(gameNumber, playerNumber, playerCommands, isplayingComputer);
					}
				} else {
					//sendCommand(com);
					System.out.println("player: " + playerName + " gave illegal option");
					sendCommand(new ClientServerCommandOld(ClientServerCommandType.IllegalOption, new String[] {"Bad game command"}));
				}
			}
		}
	}

	@Override
	public void run() {
		// need exception catch outside to avoid infinite loop
		ClientCommand com = null;
		try{
			while(true) {	
				com = (ClientCommand) input.readObject();

				if (com != null) {
					if (com instanceof ClientServerCommandOld) {
						resolveClientServerCommand((ClientServerCommandOld)com);
	
					} else if (com instanceof GameCommand) {
						resolveGameCommand((GameCommand)com);
					}
				}
			}	
		} catch (ClassNotFoundException | IOException e) {
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
	
	public void sendCommand(ClientCommand c) {
		try {
			output.writeObject(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setOpponent(String n) {
		this.opponentName = n;
	}
	
	public void disconnect() {
		try {
			input.close();
			output.close(); 
			sock.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
