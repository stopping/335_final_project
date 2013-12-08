package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class NewGame extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		boolean ret = s.createNewGameRoom(ch);
		s.updateOpenGameRooms();
		return ret;
	}

}
