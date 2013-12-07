package client;

import game_commands.GameCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import client_commands.ClientCommand;
import server_commands.SendingGame;
import server_commands.SendingUserInfo;
import server_commands.ServerCommand;
import shared.Command;
import shared.Game;

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
				Object o = input.readObject();
				System.out.println(o.toString());
				Command com = (Command) o;
				System.out.println("Client: Object read");
				
				if (com instanceof ServerCommand) {
					ServerCommand c = (ServerCommand)com;
//					if (c instanceof SendingGame) {
//						Game g = (Game) input.readObject();
//						System.out.println("Game received");
//						player.showGamePanel();
//						player.setGame(g);
//						player.update();
//					}
//					else  {
//						System.out.println("executing command " + com.hashCode());
//						((ServerCommand)com).executeOn(player);
//
//					}
					
					System.out.println("executing command " + c.hashCode());
					c.executeOn(player);
					
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
	
	
	public void sendCommand(Command com) {
		try {
			this.output.writeObject(com);
			
//			if (com instanceof ClientCommand)
//				if (((ClientServerCommandOld) com).getType() ==
//				ClientServerCommandType.SuspendSession)
//					closeSock();
			
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
