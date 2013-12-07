package client_commands;



import server.ClientHandler;
import server.Server;
import shared.Command;

/**
 * ClientCommand is sent from the client to the server, 
 * to be executed on the server.
 *
 */
@SuppressWarnings("serial")
public abstract class ClientCommand extends Command {

	// the name of the player.
	protected String source;
	
	// the ganeRoom they are in
	protected int gameRoom;
	
	// the client handler that received the request
	protected ClientHandler ch;
	
	// we are going to execute the command on the server
	public abstract boolean executeOn(Server s);
	
	// client handler will set this
	public void setSources(String s, int gr, ClientHandler ch) {
		this.source = s;
		this.ch = ch;
		this.gameRoom = gr;
	}
}
