package game_commands;

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
	
	public String toString() {
		return "" + source[0] + " " + source[1] + " " + dest[0] + " " + dest[1];
	}
}
