package game_commands;

import shared.Game;

@SuppressWarnings("serial")
public class PlaceMineCommand extends GameCommand {

	public PlaceMineCommand(int source[], int dest[]) {
		super.source = source;
		super.dest = dest;
	}	
	
	public boolean executeOn( Game g ) {
		return g.placeMine(source, dest);
	}
	
}
