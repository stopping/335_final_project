package game_commands;

import game.Game;
import item.Item;

@SuppressWarnings("serial")
public class GiveItemCommand extends GameCommand {

	private Item item;
	
	public GiveItemCommand(int source[], int dest[], Item item) {
		super.source = source;
		super.dest = dest;
		this.item = item;
	}	
	
	public Item getItem() {
		return this.item;
	}
	
	public boolean executeOn( Game g ) {
		return g.giveItem(source,dest,item);
	}
}
