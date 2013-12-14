package server_commands;

import server.Command;
import client.HumanPlayer;

/**
 * ServerCommand is sent from the server to the client, 
 * to be executed on the player(client).
 *
 */
@SuppressWarnings("serial")
public abstract class ServerCommand extends Command {

	public abstract void executeOn(HumanPlayer p);
}
