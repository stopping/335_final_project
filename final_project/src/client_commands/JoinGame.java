package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class JoinGame extends ClientCommand {
		
	private int gameToJoin;
	
	public JoinGame(int g) {
		this.gameToJoin = g;
	}
	
	public boolean executeOn(Server s) {
		return s.userJoinGame(gameToJoin, ch);
	}
}
