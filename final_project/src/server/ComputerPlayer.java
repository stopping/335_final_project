package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;
import client.Player;
import shared.Attribute;
import shared.Game;

import unit.Unit;
import shared.GameSquare;

public class ComputerPlayer implements Player, Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Game game;
	private int difficulty;
	private List<Unit> units;

	public ComputerPlayer(int gameNumber, int difficultyLevel) {
		System.out.println("Computer player created");
		Socket sock = null;
		this.difficulty = difficultyLevel;
		try {
			sock = new Socket("localhost", 4009);				
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			output.writeObject(new ClientServerCommand(
					ClientServerCommandType.ComputerPlayerJoin, 
					new String[] {String.valueOf(gameNumber)}));
			
		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	
	// returns ComputerPlayer units based on selected difficulty level
	public static ArrayList<Unit> generateAIUnits(int level) {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new Unit("Zander"));
		units.add(new Unit("Yvonne"));
		units.add(new Unit("Xavier"));
		units.add(new Unit("Will"));
		units.add(new Unit("Van"));
		double modifier = level * 0.5;
		if (modifier > 0.0) {
			for (Unit u : units) {
				u.upgrade(Attribute.Strength, modifier);
				u.upgrade(Attribute.Defense, modifier);
				u.upgrade(Attribute.MaxActionPoints, modifier*2);
				u.upgrade(Attribute.MaxHitPoints, modifier*2);
			}
		}
		System.out.println("AI difficulty: " + modifier);
		return units;
	}

	@Override
	public void parseAndExecuteCommand(GameCommand c) {	
		game.executeCommand(c);
	}
	
	private int[][] getAdjacentCoords(int source[]) {
		int res[][] = new int[4][2];	
		int sourceRow = source[0];
		int sourceCol = source[1];
		
		res[0][0] = sourceRow == 0 ? 0 : sourceRow-1 ;
		res[0][1] = sourceCol;

		res[1][0] = sourceRow == 11 ? 11 : sourceRow+1 ;
		res[1][1] = sourceCol;

		res[2][0] = sourceRow;
		res[2][1] = sourceCol == 0 ? 0 : sourceCol-1;

		res[3][0] = sourceRow;
		res[3][1] = sourceCol == 11 ? 11 : sourceCol+1;	
		
		return res;
	}
	
	private int[] getAttackableCoords(int[][] options) {
		
		for (int i = 0 ; i< 4 ; i++) {
			int destRow = options[i][0];
			int destCol = options[i][1];
			GameSquare g = game.getGameSquareAt(destRow, destCol);
			if (g.hasOccupant() && g.getOccupant().getClass().equals(Unit.class)) {
				return new int[] {destRow, destCol};
			}
		}
		return null;
	}

	private void doRandomMove(Unit u, int[] source, int[][] options) {
		
		Random r = new Random();
		int rand = r.nextInt(4);
		int[] dest = options[rand];
		
		if(u.canMoveTo(dest[0], dest[1])) {
			MoveCommand c = new MoveCommand(source, dest);
			sendCommand(c);
			
			try {
				c = (MoveCommand) input.readObject();
				parseAndExecuteCommand(c);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void doAttackOnSite(Unit u, int[] source, int[][] options) {
		int dest[] = getAttackableCoords(options);

		if (dest != null) {
			System.out.println(dest[0] + " " + dest[1]);
			Unit uToAttack = (Unit)game.getGameSquareAt(dest[0], dest[1]).getOccupant();
		
			if (units.contains(uToAttack))
				doRandomMove(u, source, options);
			else {
				AttackCommand c = new AttackCommand(source, dest);
				sendCommand(c);
				try {
					c = (AttackCommand) input.readObject();
					parseAndExecuteCommand(c);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		else 
			doRandomMove(u, source, options);
	}

	private void doComputerTurn() {
		System.out.println("Beginning computer turn");
		units = game.getBlueUnitList();

		for (Unit u : units) {	
			if( u.getLocation() == null ) continue;
			for(int i = 0; i < 3; i++) {
				
				int source[] = new int[2];
				GameSquare g = u.getLocation();
				source[0] = g.getRow();
				source[1] = g.getCol();
				int[][] destOpt = getAdjacentCoords(source);
				
				switch(difficulty) {
				case 0:
					doRandomMove(u, source, destOpt);
					break;
				case 1:
					doAttackOnSite(u, source, destOpt);
					break;
				}
			}
		}
		sendCommand(new EndTurnCommand());
	}
	
	@Override
	public void setGame(Game g) {
		this.game = g;
	}

	
	@Override
	public void sendCommand(Command com) {
		try {
			this.output.writeObject(com);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		boolean isAiTurn = false;

		try {
			Game g = (Game) input.readObject();
			setGame(g);
			
			while (true) {

				Command com = (Command) input.readObject();
				if (com instanceof ClientServerCommand) {
					if (((ClientServerCommand)com).getType() == ClientServerCommandType.ComputerTurn) {
						isAiTurn = true;
						doComputerTurn();
						isAiTurn = false;
					}
				}
				
				else if (com instanceof GameCommand && !isAiTurn) {
					parseAndExecuteCommand((GameCommand)com);
				}

			}

		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}

}
