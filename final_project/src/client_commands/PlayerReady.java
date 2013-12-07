package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class PlayerReady extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.setReady(source, ch);
	}

}
