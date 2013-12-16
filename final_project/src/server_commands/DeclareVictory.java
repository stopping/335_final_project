package server_commands;

import client.HumanPlayer;

@SuppressWarnings("serial")
public class DeclareVictory extends ServerCommand {
	
	private boolean condition;
	
	public DeclareVictory(boolean c) {
		condition = c;
	}
	
	public void executeOn(HumanPlayer p) {
		System.out.println(p.hasWon());
		p.setVictory(condition);
		System.out.println(p.hasWon());
	}
	
}
