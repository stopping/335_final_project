package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class ResumeSession extends ClientCommand {


	@Override
	public boolean executeOn(Server s) {
		return s.resumeSession(source, ch);
	}

}
