package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class SuspendSession extends ClientCommand {

	@Override
	public boolean executeOn(Server s) {
		return s.suspendSession(source, ch);
	}

}
