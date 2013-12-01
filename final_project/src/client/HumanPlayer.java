package client;

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
	public void parseAndExecuteCommand(GameCommand com) {
		System.out.println(game.executeCommand(com));
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
	
	public void receiveMessage(ClientServerCommand com) {
		// to be over written by GUI
	}
	
	public void failLogin(ClientServerCommand com) {
		// to be over written by GUI sounds like an abstract method/class to me
	}
	
	public void login() {
		// to be over written by GUI sounds like an abstract method/class to me

	}
}
