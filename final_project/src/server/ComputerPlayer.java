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
import shared.Game;

import unit.Unit;
import shared.GameSquare;

public class ComputerPlayer implements Player, Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Game game;

	public ComputerPlayer() {
		System.out.println("Computer player created");
		Socket sock = null;
		try {
			sock = new Socket("localhost", 4009);				
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			
		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}

	@Override
	public void parseAndExecuteCommand(GameCommand c) {	
		game.executeCommand(c);
	}
	
	private int[] getAdjacentCoords(int source[]) {
		int res[] = new int[2];	
		int sourceRow = source[0];
		int sourceCol = source[1];
		
		Random r = new Random();
		int rand = r.nextInt(4);
		
		switch (rand) {
		case 0:
			res[0] = sourceRow == 0 ? 0 : sourceRow-1 ;
			res[1] = sourceCol;
			break;
		case 1:
			res[0] = sourceRow == 11 ? 11 : sourceRow+1 ;
			res[1] = sourceCol;
			break;
		case 2:
			res[0] = sourceRow;
			res[1] = sourceCol == 0 ? 0 : sourceCol-1;
			break;
		case 3:
			res[0] = sourceRow;
			res[1] = sourceCol == 11 ? 11 : sourceCol+1;
			break;
		}
		
		return res;
	}
	
	private boolean inRange(int[] source) {
		int sourceRow = source[0];
		int sourceCol = source[1];
		if (sourceRow-- < 0 || sourceRow++ > 11 ||
				sourceCol-- < 0 || sourceCol++ > 11)
			return false;
		return true;
	}
	
	private int[] getAttackableCoords(int[] source) {
		int res[] = new int[2];	
		int sourceRow = source[0];
		int sourceCol = source[1];
		
		
		return res;
	}


	private void doComputerTurn() {
		System.out.println("Beginning computer turn");
		List<Unit> computerUnits = game.getBlueUnitList();

		for (Unit u : computerUnits) {	
			if( u.getLocation() == null ) continue;
			for(int i = 0; i < 3; i++) {
			//while (u.getActionPoints() > 1) {
				
				int source[] = new int[2];
				GameSquare g = u.getLocation();
				source[0] = g.getRow();
				source[1] = g.getCol();
				int[] dest = getAdjacentCoords(source);
				
				//int attackCoords[] = getAttackableCoords(source);
//				if (attackCoords != null) {
//					 sendCommand(new AttackCommand(source, dest);
//					//game.attack(source, attackCoords);
//				}
//				else {
					
					if(u.canMoveTo(game.getGameSquareAt(dest[0], dest[1]))) {
						MoveCommand c = new MoveCommand(source, dest);
						sendCommand(c);
						
						try {
							c = (MoveCommand) input.readObject();
							parseAndExecuteCommand(c);
						} catch (ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}

//				}
			}
		}
		
		sendCommand(new EndTurnCommand());
		System.out.println("Ending computer turn");
		
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
			System.out.println("Starting...");
			Game g = (Game) input.readObject();
			setGame(g);
			System.out.println("Game received");
			
			while (true) {

				Command com = (Command) input.readObject();
				System.out.println("Command Received");
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
