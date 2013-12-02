package client;

import java.util.ArrayList;

import commands.*;

import shared.Game;

public class HumanPlayer implements Player {

	protected Game game;
	protected Client client;
	
	public static void main(String args[]) {
		new HumanPlayer();
	}
	
	public HumanPlayer() {
		client = new Client("localhost", 4009, this);
		Thread t = new Thread(client);
		t.start();
	}
	
	@Override
	public boolean parseAndExecuteCommand(GameCommand com) {
		boolean ret = game.executeCommand(com);
		System.out.println(ret);
		return ret;
	}

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

	@Override
	public void receiveMessage(ClientServerCommand com) {}

	@Override
	public void failLogin(ClientServerCommand com) {}

	@Override
	public void login() {}

	@Override
	public void updateAvailGameRooms(ArrayList<String> names) {}

	@Override
	public void setStartGameAvail() {}

	@Override
	public void showGamePanel() {}

	@Override
	public void updateUnitInfo(ArrayList<String> info) {}

}
