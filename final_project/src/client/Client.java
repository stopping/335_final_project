package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;

import shared.Game;

/**
 * Class:	Client
 * Purpose:	Facilitate communication between Server and the User.
 */
public class Client implements Runnable {

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
				Command com = (Command) input.readObject();
				System.out.println("Client: Object read");
				
				if (com instanceof ClientServerCommand) {
					ClientServerCommand c = (ClientServerCommand)com;
					switch (c.getType()) {
						
						case ValidLogin:
							player.login();
							break;
							
						case IllegalOption:
							player.failLogin((ClientServerCommand) com);
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
							player.receiveMessage((ClientServerCommand)com);
							break;
							
						case ResumeSession:
							g = (Game) input.readObject();
							player.setGame(g);
							player.update();
							break;
							
						case OpenGameRooms:
							player.updateAvailGameRooms(((ClientServerCommand) com).getData());
							break;
							
						case UnitInfo:
							//player.updateUnitInfo(((ClientServerCommand) com).getData());
							break;

					default:
						break;
					}
				}
				
				else if (com instanceof GameCommand) {
					player.parseAndExecuteCommand((GameCommand)com);
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
	
	public void sendCommand(Command com) {
		try {
			this.output.writeObject(com);
			
			if (com instanceof ClientServerCommand)
				if (((ClientServerCommand) com).getType() ==
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
