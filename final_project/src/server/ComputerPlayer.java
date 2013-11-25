package server;

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
import shared.GameSquare;
import shared.Unit;

public class ComputerPlayer implements Player, Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Game game;

	public ComputerPlayer() {
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
		
		if (sourceRow == 0)
			res[0] = sourceRow++;
		if (sourceRow == 11)
			res[0] = sourceRow--;
		if (sourceCol == 0)
			res[1] = sourceCol ++;
		if (sourceCol == 11)
			res[1] = sourceCol++;
		
		Random r = new Random();
		int rand = r.nextInt(3);
		
		switch (rand) {
		case 0:
			res[0] = sourceRow--;
			break;
		case 1:
			res[1] = sourceRow++;
		case 2:
			res[0] = sourceCol--;
		case 3: 
			res[0] = sourceCol++;
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
		List<Unit> computerUnits = game.getBlueUnitList();

		for (Unit u : computerUnits) {	
			while (u.getActionPoints() > 1) {
				
				int source[] = new int[2];
				GameSquare g = u.getLocation();
				source[0] = g.getRow();
				source[1] = g.getCol();
				int dest[] = getAdjacentCoords(source);
				
				//int attackCoords[] = getAttackableCoords(source);
//				if (attackCoords != null) {
//					 sendCommand(new AttackCommand(source, dest);
//					//game.attack(source, attackCoords);
//				}
//				else {
					sendCommand(new MoveCommand(source, dest));
//				}
			}
		}
		
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

		try {
			Game g = (Game) input.readObject();
			setGame(g);
			
			while (true) {
				Command com = (Command) input.readObject();
				if (com instanceof ClientServerCommand) {
					if (((ClientServerCommand)com).getType() == ClientServerCommandType.ComputerTurn) {
						doComputerTurn();
					}
				}
				
				else if (com instanceof GameCommand) {
					parseAndExecuteCommand((GameCommand)com);
				}
			}

		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}

}
