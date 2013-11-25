package commands;

import shared.Game;

@SuppressWarnings("serial")
public class EndTurnCommand extends GameCommand {
	
	public boolean executeOn( Game g ) {
		return g.endTurn();
	}
	
}
