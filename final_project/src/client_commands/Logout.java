package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class Logout extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.logout(source, gameRoom, ch);
	}
}