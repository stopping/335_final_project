package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client_commands.ClientCommand;
import game_commands.GameCommand;
import server_commands.ServerCommand;
import shared.Command;
import shared.Game;

public class ClientHandler implements Runnable {

	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Server server;
	private Socket sock;
	protected String playerName;
	protected int gameRoom;

	public ClientHandler(Socket clientSock, Server s) {
			
		server = s;
		sock = clientSock;
		try {
			output = new ObjectOutputStream(clientSock.getOutputStream());
			input = new ObjectInputStream(clientSock.getInputStream());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
	
		Command com = null;
		try{
			while(true) {	
				com = (Command) input.readObject();
				
				if (com instanceof ClientCommand) {
					System.out.println("received client command");
					ClientCommand c = (ClientCommand)com;
					c.setSources(playerName, gameRoom, this);
					c.executeOn(server);
				}
				
				else if (com instanceof GameCommand) {
					server.executeGameCommand(gameRoom, this, (GameCommand)com);
				}
			}	
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayerName(String p) {
		this.playerName = p;
	}
	
	public void setGameRoom(int gr) {
		this.gameRoom = gr;
	}
	
//	public void sendGame(Game g) {
//		try {
//			output.writeObject(g);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public void sendCommand(ServerCommand c) {
		try {
			output.writeObject(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendCommand(GameCommand c) {
		try {
			output.writeObject(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public String getName() {
		return playerName;
	}
}
