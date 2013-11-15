package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import shared.Command;
import shared.Game;

/**
 * Class:	Client
 * Purpose:	Facilitate communication between Server and the User.
 * @author  Kyle Criddle
 */
public class Client implements Runnable {

	private HumanPlayer player;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String host;
	private int port;
		
	public Client (String host, int port, HumanPlayer p) {
		this.host = host;
		this.port = port;
		this.player = p;
	}
	
	@Override
	public void run() {
		Socket sock = null;

		try {
			sock = new Socket(host, port);				
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			
			Game g = (Game) input.readObject();
			player.setGame(g);
			player.update();
			
			while (true) {
				Command com = (Command) input.readObject();
				player.parseAndExecuteCommand(com);
				player.update();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
