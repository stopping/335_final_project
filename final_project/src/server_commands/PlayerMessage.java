package server_commands;

import client.HumanPlayer;

@SuppressWarnings("serial")
public class PlayerMessage extends ServerCommand {

	private String message;
	private String source;
	
	public PlayerMessage(String source, String msg) {
		this.source = source;
		this.message = msg;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		p.receiveMessage(source, message);
	}
}
