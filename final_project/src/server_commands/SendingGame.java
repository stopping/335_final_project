package server_commands;

import shared.Game;
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
		System.out.println("Game received");
	}
	
	public Game getGame() {
		return game;
	}
}
