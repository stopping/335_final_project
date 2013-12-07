package game_commands;

import shared.Game;

@SuppressWarnings("serial")
public class UseAbilityCommand extends GameCommand {

	public UseAbilityCommand(int[] source, int[] dest) {
		super.source = source;
		super.dest = dest;
	}
	
	public boolean executeOn( Game g ) {
		return g.useAbility(source,dest);
	}
	
}
