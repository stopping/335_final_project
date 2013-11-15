package client;

import java.util.Scanner;

import shared.Command;
import shared.Command.CommandType;

public class TextView {

	private Scanner keyboard; 
	private HumanPlayer player;
	private String controls;
	
	public static void main(String[] args) {
		new TextView();
	}
	
	public TextView() {
		
		controls = "";
		System.out.println("Welcome");
		System.out.println(controls);
		
		player = new HumanPlayer();
		keyboard = new Scanner(System.in);

		runGame();
	}
	
	private void sendCommand(String key) {
		switch (key) {
			case "q":
				player.sendCommand(new Command(CommandType.Quit,
						0, 0, 0, 0, null, null));
				System.exit(0);
				break;
		}
	}
	
	private void runGame() {
		
		while (keyboard.hasNext()) {
			String key = keyboard.next();
			sendCommand(key);			
		}
	}

}
