package game_commands;

import shared.Game;

@SuppressWarnings("serial")
public class EndTurnCommand extends GameCommand {
	
	public EndTurnCommand() {
		super();
	}
	
	public boolean executeOn( Game g ) {
		return g.endTurn();
	}
	
}
