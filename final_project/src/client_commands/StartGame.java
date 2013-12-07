package client_commands;

import server.Server;
import shared.Game.WinCondition;

@SuppressWarnings("serial")
public class StartGame extends ClientCommand {

	WinCondition condition;
	
	public StartGame(WinCondition wc) {
		this.condition = wc;
	}

	@Override
	public boolean executeOn(Server s) {
		return s.startGame(source, gameRoom, condition);
	}

}
