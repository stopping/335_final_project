package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class LeaveGameRoom extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.playerLeaveGameRoom(source, gameRoom, ch);
	}

}
