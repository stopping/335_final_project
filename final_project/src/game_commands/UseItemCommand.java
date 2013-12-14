package game_commands;

import game.Game;

@SuppressWarnings("serial")
public class UseItemCommand extends GameCommand {

	private int itemIndex;
	
	public UseItemCommand(int[] source, int[] dest, int i) {
		this.source = source;
		this.dest = dest;
		this.itemIndex = i;
	}
	
	@Override
	public boolean executeOn(Game g) {
		return g.useItem(source, dest, itemIndex);
	}
	
	
}
