package client;

import shared.Command;
import shared.Game;

/**
 * Interface Player provides a set of methods that both
 * ComputerPlayers and HumanPlayers share.
 * 
 * @author Kyle Criddle
 */
public interface Player {
	
	
	/**
	 * Player receives a valid command and executes it.
	 * 
	 * @param c		The Command to be executed. 
	 * 				Precondition: command has been validated by server.
	 */
	public abstract void parseAndExecuteCommand(Command com);
	
	
	/**
	 * Player receives a new game when they join.
	 * Should only be called once.
	 * 
	 * @param g
	 */
	public abstract void setGame(Game g);
	
	
	/**
	 * Player sends a command request to the server. 
	 * 
	 * @param c
	 */
	public abstract void sendCommand(Command com);
	
}
