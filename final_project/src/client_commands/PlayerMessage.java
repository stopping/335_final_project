package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class PlayerMessage extends ClientCommand {

	private String message;
	
	public PlayerMessage(String msg) {
		this.message = msg;
	}

	@Override
	public boolean executeOn(Server s) {
		return s.sendMessage(source, gameRoom, message);
	}

}
