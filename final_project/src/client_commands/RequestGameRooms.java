package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class RequestGameRooms extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		s.updateOpenGameRooms();
		return true;
	}

}
