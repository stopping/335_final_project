package client;

import java.util.Scanner;

import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;


public class TextView {

	private Scanner input; 
	private HumanPlayer humanPlayer;
	private String controls;
	
	public static void main(String[] args) {
		new TextView();
	}
	
	public TextView() {
		controls = "'m' -- move \n'a' -- attack''\n'i' -- give item\n'e' -- end turn";
		humanPlayer = new HumanPlayer();
		input = new Scanner(System.in);
		runGame();
	}
	
	private void sendCommand(String key) {
		
		int[] src = new int[2];
		int[] dest = new int[2];
		
		switch (key) {
			case "f":
				humanPlayer.sendCommand(new ClientServerCommand(
						ClientServerCommandType.PlayerForfeit, null));
				input.close();
				System.exit(0);
				break;
				
			case "ai":
				humanPlayer.sendCommand(new ClientServerCommand(
						ClientServerCommandType.NewComputerPlayer, null));
				break;
				
			case "e":
				humanPlayer.sendCommand(new EndTurnCommand());
				break;
				
			case "i":
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				System.out.print("Select item: ");
				// TODO: how to access the game here?
				//humanPlayer.sendCommand(new GiveItemCommand(src, dest));
				
			case "n":
					// This will probably just change things client side
				break;
				
			case "m":
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				humanPlayer.sendCommand(new MoveCommand(src, dest));
				break;
				
			case "a":
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				humanPlayer.sendCommand(new AttackCommand(src, dest));
				break;
		}
	}
	
	private void runGame() {
		String args[] = new String[4];
		String key = "";
		
		// login 
		System.out.print("Login ID: ");
		args[0] = input.next();
		System.out.print("Password: ");
		args[1] = input.next();
		humanPlayer.sendCommand(new ClientServerCommand(
				ClientServerCommandType.Login, args));
		
		// new game or join game
		System.out.print("Enter 'n' for new game: ");
		key = input.next();
		sendCommand(key);
		
		// create new AI
		System.out.print("Enter 'AI' to play against computer: ");
		key = input.next();
		sendCommand(key);
		
		// choose win condition
		System.out.print("Choose Victory Condition\n'Deathmatch'\n'CTF'\n'Demolition'\n: ");
		args[0] = input.next();
		
		// start the game
		System.out.print("Enter 'START' to begin the game: ");
		key = input.next();
		humanPlayer.sendCommand(new ClientServerCommand(
				ClientServerCommandType.StartGame, args));

		System.out.println("\n" + controls);
		
		// loop and send commands
		while (input.hasNext()) {
			key = input.next();
			sendCommand(key);			
			System.out.println(controls);
		}
	}

}
