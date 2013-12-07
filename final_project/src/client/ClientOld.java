package client;

import game_commands.GameCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import client_commands.*;
import client_commands.ClientServerCommandOld.ClientServerCommandType;


import shared.Game;

/**
 * Class:	Client
 * Purpose:	Facilitate communication between Server and the User.
 */
public class ClientOld implements Runnable {

	private HumanPlayer player;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String host;
	private int port;
	private Socket sock;
		
	public Client (String host, int port, HumanPlayer p) {
		this.host = host;
		this.port = port;
		this.player = p;
	}
	
	@Override
	public void run() {
		sock = null;

		try {
			sock = new Socket(host, port);				
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			
			while (true) {
				ClientCommand com = (ClientCommand) input.readObject();
				System.out.println("Client: Object read");
				
				if (com instanceof ClientServerCommandOld) {
					ClientServerCommandOld c = (ClientServerCommandOld)com;
					switch (c.getType()) {
						
						case ValidLogin:
							player.login();
							break;
							
						case IllegalOption:
							player.failLogin((ClientServerCommandOld) com);
							break;
							
						case SendingGame:
							Game g = (Game) input.readObject();
							System.out.println("Game received");
							player.showGamePanel();
							player.setGame(g);
							player.update();
							break;
							
						case StartGame:
							player.setStartGameAvail();
							break;
							
						case Message:
							player.receiveMessage((ClientServerCommandOld)com);
							break;
							
						case ResumeSession:
							g = (Game) input.readObject();
							player.setGame(g);
							player.update();
							break;
							
						case OpenGameRooms:
							player.updateAvailGameRooms(((ClientServerCommandOld) com).getData());
							break;
							
						case UnitInfo:
							//player.updateUnitInfo(((ClientServerCommand) com).getData());
							break;

					default:
						break;
					}
				}
				
				else if (com instanceof GameCommand) {
					player.executeGameCommand((GameCommand)com);
					player.update();
				}
			}

		} catch (ConnectException ce) {
			ce.printStackTrace();
			System.out.println("Server is not running.");
		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	
	public void sendCommand(ClientCommand com) {
		try {
			this.output.writeObject(com);
			
			if (com instanceof ClientServerCommandOld)
				if (((ClientServerCommandOld) com).getType() ==
				ClientServerCommandType.SuspendSession)
					closeSock();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeSock() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
