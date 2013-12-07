package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class NewComputerPlayer extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.newComputerPlayer(gameRoom);
	}
}
