package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class Surrender extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.playerSurrender(source, gameRoom, ch);
	}

}
