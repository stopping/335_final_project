package game_commands;

import game.Game;

@SuppressWarnings("serial")
public class AttackCommand extends GameCommand {

	public AttackCommand(int[] source, int[] dest) {
		super.source = source;
		super.dest = dest;
	}
	
	public boolean executeOn( Game g ) {
		return g.attack(source,dest);
	}
	
}
