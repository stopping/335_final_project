package server_commands;

import client.HumanPlayer;

@SuppressWarnings("serial")
public class Surrender extends ServerCommand {

	private String playerSurrendered;
	
	public Surrender(String playerName) {
		this.playerSurrendered = playerName;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		p.playerSurrendered(playerSurrendered);
	}
	

}
