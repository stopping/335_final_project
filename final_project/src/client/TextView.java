package client;

import java.util.Scanner;

import shared.Command;
import shared.Command.CommandType;

public class TextView {

	private Scanner keyboard; 
	private HumanPlayer humanPlayer;
	private String controls;
	
	public static void main(String[] args) {
		new TextView();
	}
	
	public TextView() {
		controls = "'m' to move \n'a' to attack'\n";
		humanPlayer = new HumanPlayer();
		keyboard = new Scanner(System.in);
		runGame();
	}
	
	private void sendCommand(String key) {
		switch (key) {
			case "q":
				humanPlayer.sendCommand(new Command(CommandType.Quit,
						0, 0, 0, 0, null, null));
				System.exit(0);
				break;
				
			case "ai":
				humanPlayer.sendCommand(new Command(CommandType.NewAI,
						0, 0, 0, 0, null, null));

				break;
				
			case "start":
				humanPlayer.sendCommand(new Command(CommandType.StartGame,
						0, 0, 0, 0, null, null));
				break;
				

		}
	}
	
	private void runGame() {
		String key = "";
		
		// login 
		System.out.println("Login:");
		key = keyboard.next();
		humanPlayer.sendCommand(new Command (CommandType.Login, 
						0, 0, 0, 0, null, key));
		
		// create new AI
		System.out.println("Enter 'AI' to play against computer.");
		key = keyboard.next();
		sendCommand(key);
		
		// start the game
		System.out.println("Enter 'START' to begin the game.");
		key = keyboard.next();
		sendCommand(key);
		
		// loop and send commands
		while (keyboard.hasNext()) {
			System.out.println(controls);
			key = keyboard.next();
			sendCommand(key);			
		}
	}

}
