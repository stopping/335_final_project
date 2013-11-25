package commands;

import shared.Game;
import shared.Item;

@SuppressWarnings("serial")
public class UseItemCommand extends GameCommand {

	private Item item;
	
	public UseItemCommand(int[] source, int[] dest, Item item) {
		this.source = source;
		this.dest = dest;
		this.item = item;
	}
	
	public void execute(Game g) {
		g.useItem(source, dest, item);
	}
	
	
}
