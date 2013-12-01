package shared;

import java.util.ArrayList;
import java.util.Scanner;

import commands.*;
import shared.Game.WinCondition;
import unit.Unit;

public class PlayConsoleGame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Unit> list1 = new ArrayList<Unit>();
		ArrayList<Unit> list2 = new ArrayList<Unit>();
		
		list1.add(new Unit("Alice"));
		list1.add(new Unit("Bob"));
		list1.add(new Unit("Charlie"));
		list1.add(new Unit("David"));
		list1.add(new Unit("Eric"));
		
		list2.add(new Unit("Zander"));
		list2.add(new Unit("Yvonne"));
		list2.add(new Unit("Xavier"));
		list2.add(new Unit("Will"));
		list2.add(new Unit("Van"));
		
		Game game = new Game(list1,list2,WinCondition.Deathmatch);
		
		Scanner input = new Scanner( System.in );
		int[] src = new int[2];
		int[] dest = new int[2];
		
		while(!game.checkWinCondition()) {
			System.out.println(game.toString());
			
			System.out.println(game.lineOfSightGrid(list1.get(0).getLocation()));
			System.out.print("Select action: ");
			String action = input.next();
			
			GameCommand c = null;
			
			if(action.equalsIgnoreCase("m")) {
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				c = new MoveCommand(src, dest);
			}
			if(action.equalsIgnoreCase("a")) {
				System.out.print("Select source: ");
				src[0] = input.nextInt();
				src[1] = input.nextInt();
				System.out.print("Select destination: ");
				dest[0] = input.nextInt();
				dest[1] = input.nextInt();
				c = new AttackCommand(src,dest);
			}
			if(action.equalsIgnoreCase("e")) {
				c = new EndTurnCommand();
			}
			
			System.out.println(game.executeCommand(c));
			
			if(game.checkWinCondition()) {
				System.out.println("You won!");
			}
		}
		
		input.close();

	}

}
