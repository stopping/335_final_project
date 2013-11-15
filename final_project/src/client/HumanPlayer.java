package client;

import shared.Command;
import shared.Game;

public class HumanPlayer implements Player {

	private Game game;
	private Client client;
	
	public static void main(String args[]) {
		new HumanPlayer();
	}
	
	public HumanPlayer() {
		client = new Client("localhost", 4009, this);
		Thread t = new Thread(client);
		t.start();
	}
	
	@Override
	public void parseAndExecuteCommand(Command com) {
		
		switch(com.getCommandType()) {
		
			case Attack:
				//game.attack(com);
				break;
				
			case Message:
				break;
			
			case Move:
				//game.move(com);
				break;
				
			case GiveItem:
				//game.giveItem(com);
				break;
				
			case UseItem:
				//game.useItem(com);
				break;
				
			default:
				break;
		}
	}

	@Override
	public void update() {
		System.out.println(game.toString());
	}

	@Override
	public void setGame(Game g) {
		this.game = g;
	}

	@Override
	public void sendCommand(Command com) {
		client.sendCommand(com);
	}

}
