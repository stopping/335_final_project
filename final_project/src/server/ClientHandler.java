package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import shared.Game;
import shared.Game.WinCondition;
import unit.Unit;

import commands.ClientServerCommand;
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
	private Deque<Command> playerCommands;	
	private Server server;
	
	public ClientHandler(Socket clientSock, Server s) {
		
		playerCommands = new LinkedList<Command>();
		server = s;
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
			server.processUser(name, password);
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
			break;
			
		case NewGame:
			this.gameNumber = server.numGameRooms();
			server.requestNewGameRoom(this);
			break;
			
		case PlayerForfeit:
			server.playerForfeit(gameNumber, this);
			break;
			
		case Ready:
			server.userSetReady(playerName);
			break;
			
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
			
		default:
			break;
		
		}
	}
	
	private void resolveGameCommand(Command com) {
		
		if (game.isCurrentPlayer(playerNumber)) {

			if (com instanceof EndTurnCommand) {
				sendCommand(com);
				playerCommands.add(com);
				server.updateClients(gameNumber, playerNumber, playerCommands, isplayingComputer);
				
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
