package server_commands;

import client.HumanPlayer;

@SuppressWarnings("serial")
public class ComputerDifficultySet extends ServerCommand {

	public int level;
	
	public ComputerDifficultySet(int level) {
		this.level = level;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {

	}

}
