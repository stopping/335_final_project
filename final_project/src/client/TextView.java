package client;

import java.util.Scanner;

import shared.Command;
import shared.Command.CommandType;

public class TextView {

	private Scanner input; 
	private HumanPlayer humanPlayer;
	private String controls;
	
	public static void main(String[] args) {
		new TextView();
	}
	
	public TextView() {
		controls = "'m' -- move \n'a' -- attack'\n'e' -- end turn";
		humanPlayer = new HumanPlayer();
		input = new Scanner(System.in);
		runGame();
	}
	
	private void sendCommand(String key) {
		
		int[] src = new int[2];
		int[] dest = new int[2];
		
		switch (key) {
			case "q":
				humanPlayer.sendCommand(new Command(CommandType.Quit,
						0, 0, 0, 0, null, null));
				input.close();
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
				
			case "e":
				humanPlayer.sendCommand(new Command(CommandType.EndTurn,
						null, null, null, null));
				break;
				
			case "n":
				humanPlayer.sendCommand(new Command(CommandType.NewGame,
						null, null, null, null));
				break;
				
			case "m":
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				humanPlayer.sendCommand(new Command(CommandType.Move, 
						src, dest, null, null));
				break;
				
			case "a":
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				humanPlayer.sendCommand(new Command(CommandType.Attack, 
						src, dest, null, null));
				break;
		}
	}
	
	private void runGame() {
		String key = "";
		
		// login 
		System.out.print("Login: ");
		key = input.next();
		humanPlayer.sendCommand(new Command (CommandType.Login, 
						0, 0, 0, 0, null, key));
		
		// new game or join game
		System.out.print("Enter 'n' for new game: ");
		key = input.next();
		sendCommand(key);
		
		// create new AI
		System.out.print("Enter 'AI' to play against computer: ");
		key = input.next();
		sendCommand(key);
		
		// choose win condition
		System.out.print("Choose Victory Condition\n'Deathmatch'\n" +
				"'CTF'\n'Demolition'\n: ");
		key = input.next();
		humanPlayer.sendCommand(new Command(CommandType.SetWinCondition, 
				0,0,0,0, null, key));
		
		// start the game
		System.out.print("Enter 'START' to begin the game: ");
		key = input.next();
		sendCommand(key);
		
		System.out.println("\n" + controls);
		
		// loop and send commands
		while (input.hasNext()) {
			key = input.next();
			sendCommand(key);			
			System.out.println(controls);
		}
	}

}
