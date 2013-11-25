package commands;

import shared.Game;

@SuppressWarnings("serial")
public class MoveCommand extends GameCommand {

	public MoveCommand(int[] source, int[] dest) {
		this.source = source;
		this.dest = dest;
	}
	
	public boolean executeOn(Game g) {
		return g.moveUnit(source,dest);
	}
}
