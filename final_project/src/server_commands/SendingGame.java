package server_commands;

import game.Game;
import unit.Unit;
import client.HumanPlayer;

@SuppressWarnings("serial")
public class SendingGame extends ServerCommand {
	
	private Game game;
	
	public SendingGame( Game g ) {
		game = g;
	}
	
	@Override
	public void executeOn(HumanPlayer player) {
		player.showGamePanel();
		player.setGame(game);
		player.update();
		
		System.out.println(game.getRedUnitList().size() + " " + game.getBlueUnitList().size());
		
		for(Unit u : game.getRedUnitList() )
			System.out.println(u.toString());
		for(Unit u : game.getBlueUnitList() )
			System.out.println(u.toString());
		
		System.out.println("Game received");
	}
	
	public Game getGame() {
		return game;
	}
}
