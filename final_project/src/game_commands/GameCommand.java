package game_commands;

import shared.Command;
import shared.Game;

@SuppressWarnings("serial")
public abstract class GameCommand extends Command { 
	
	protected int[] source;
	protected int[] dest;
	
	public int[] getSource() { return this.source; }
	public int[] getDest() { return this.dest; }
	
	public boolean executeOn( Game g ) {
		return false;
	}
}