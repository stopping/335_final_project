package game_commands;

import game.Game;

@SuppressWarnings("serial")
public class EndTurnCommand extends GameCommand {
	
	public EndTurnCommand() {
		super();
	}
	
	public boolean executeOn( Game g ) {
		return g.endTurn();
	}
	
}
