package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class ComputerDifficultySet extends ClientCommand {

	private int level;
	
	public ComputerDifficultySet(int level) {
		this.level = level;
	}
	
	@Override
	public boolean executeOn(Server s) {
		return s.setComputerPlayerDifficulty(gameRoom, level);
	}

}
