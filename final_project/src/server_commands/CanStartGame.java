package server_commands;

import client.HumanPlayer;

@SuppressWarnings("serial")
public class CanStartGame extends ServerCommand {

	@Override
	public void executeOn(HumanPlayer p) {
		p.canStartGame();
	}

}
