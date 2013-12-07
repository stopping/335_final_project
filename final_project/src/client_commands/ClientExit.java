package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class ClientExit extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.removeClient(source, gameRoom, ch);
	}

}
