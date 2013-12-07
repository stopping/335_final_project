package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class NewComputerPlayer extends ClientCommand {
	
	private int difficultyLevel;
	
	public NewComputerPlayer(int level) {
		this.difficultyLevel = level;
	}

	@Override
	public boolean executeOn(Server s) {
		return s.newComputerPlayer(gameRoom, difficultyLevel);
	}
}
