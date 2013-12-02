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
import shared.Occupant;
import unit.*;
import shared.GameSquare;

public class ComputerPlayer implements Player, Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Game game;
	private int difficulty;
	private List<Unit> units;

	public ComputerPlayer(String opponent, int difficultyLevel) {
		System.out.println("Computer player created");
		Socket sock = null;
		this.difficulty = difficultyLevel;
		try {
			sock = new Socket("localhost", 4009);				
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			output.writeObject(new ClientServerCommand(
					ClientServerCommandType.ComputerPlayerJoin, 
					new String[] {opponent}));
			
		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	
	// returns ComputerPlayer units based on selected difficulty level
	public static ArrayList<Unit> generateAIUnits(int level) {
		ArrayList<Unit> units = new ArrayList<Unit>();
		units.add(new SoldierUnit("Zander"));
		units.add(new SoldierUnit("Yvonne"));
		units.add(new SoldierUnit("Xavier"));
		units.add(new SoldierUnit("Will"));
		units.add(new SoldierUnit("Van"));
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
	public boolean parseAndExecuteCommand(GameCommand c) {	
		return game.executeCommand(c);
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
	
	private boolean tryMove(Unit u, int[] source, int[] dest) {
		if(u.canMoveTo(dest[0], dest[1])) {
			MoveCommand c = new MoveCommand(source, dest);
			sendCommand(c);
			
			try {
				Command ret = (Command) input.readObject();
				if (ret instanceof ClientServerCommand) {
					if (((ClientServerCommand) ret).getType() ==
						(ClientServerCommandType.IllegalOption))
						return false;
				}
				else {
					GameCommand com = (GameCommand) ret;
					return parseAndExecuteCommand(com);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean tryAttack(Unit u ,int[] source, int[] dest) {
		for (Unit ut : units) {
			if (ut != null) {
				GameSquare g = ut.getLocation();
				if (g != null && g.hasOccupant()) {
					g = game.getGameSquareAt(dest[0], dest[1]);
					if (g != null && g.hasOccupant()) 
						if (ut.equals(g.getOccupant()))
							return false;
				}
			}
		}
		
		if (u.canAttack(dest[0], dest[1])) {
			AttackCommand c = new AttackCommand(source, dest);
			sendCommand(c);
			try {
				Command ret = (Command) input.readObject();
				if (ret instanceof ClientServerCommand) {
					if (((ClientServerCommand) ret).getType() ==
							(ClientServerCommandType.IllegalOption))
						return false;
				}
				else {
					GameCommand com = (GameCommand) ret;
					return parseAndExecuteCommand(com);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private int[] getAttackableCoords(int[][] options, boolean unit) {
		
		for (int i = 0 ; i< 4 ; i++) {
			int destRow = options[i][0];
			int destCol = options[i][1];
			GameSquare g = game.getGameSquareAt(destRow, destCol);
			
			if (g.hasOccupant()) {
				if (unit && g.getOccupant() instanceof Unit) {
					return new int[] {destRow, destCol};
				}
				else
					return new int[] {destRow, destCol};
			}
		}
		return null;
	}

	private void doRandomMove(Unit u, int[] source, int[][] options) {
		
		Random r = new Random();
		int rand = r.nextInt(4);
		int[] dest = options[rand];
		for (int i=0; i < 4 ; i++)
			if (tryMove(u, source, dest))
				break;
	}
	
	private void doAttackAnything(Unit u, int[] source, int[][] options) {
		int dest[] = getAttackableCoords(options, false);
		
		if (dest != null) {
			Occupant oToAttack = (Occupant)game.getGameSquareAt(dest[0], dest[1]).getOccupant();
		
			if (units.contains(oToAttack))
				doRandomMove(u, source, options);
			else 
				tryAttack(u, source, dest);		
		}
		else 
			doRandomMove(u, source, options);
	}
	
	private boolean attackThyNeighboor(Unit u, int[] source, int[][] opt) {
		for (int i=0 ; i < 4 ; i++) {
			GameSquare g = game.getGameSquareAt(opt[i][0], opt[i][1]);
			if (g.hasOccupant() && g.getOccupant() instanceof Unit) {
				return tryAttack(u, source, new int[] {opt[i][0], opt[i][1]});
			}		
		}
		return false;
	}
	
	private void doSeekAndDestroy(Unit u, int[] source, int[][] opt) {
		
		if (attackThyNeighboor(u, source, opt))
			return;
		
		List<Unit> opUnits = game.getRedUnitList();
		int  closest[] = new int[2];
		int in = 0;
		for (Unit un : opUnits) {
			if (un != null) {
				GameSquare g = un.getLocation();
				if (g != null && g.hasOccupant()) {
					closest[0] = opUnits.get(in).getLocation().getRow();
					closest[1] = opUnits.get(in).getLocation().getCol();
					break;
				}
			}
			in++;
		}
		
		double dist = Math.sqrt( Math.pow((Math.abs(source[0]-closest[0])),2)+
				Math.pow((Math.abs(source[1]-closest[1])),2));

		for (Unit ut : opUnits) {
			if (ut != null) {
				GameSquare g = ut.getLocation();
				if (g != null) {
					int r = ut.getLocation().getRow();
					int c = ut.getLocation().getCol();
					double distComp = Math.sqrt( Math.pow((Math.abs(source[0]-r)),2)+
							Math.pow((Math.abs(source[1]-c)),2));
					if (distComp < dist) {
						dist = distComp;
						closest[0] = r;
						closest[1] = c;
					}
				}
			}
		}
		int dest[] = new int[] {closest[0], closest[1]};

		if (!tryAttack(u, source, dest)) {

			int row = source[0];
			int col = source[1];
			
			if (dest[0] < row)
				row--;
			else if (dest[0] > row)
				row++;
			if (dest[1] < col)
				col--;
			else if (dest[1] > col)
				col++;
			
			dest[0] = row;
			dest[1] = col;
			
			if (!tryMove(u ,source, dest))
				if (!tryAttack(u, source, dest))
					doRandomMove(u, source, opt);
		}
	}
	
	private void doAttackOnSite(Unit u, int[] source, int[][] options) {
		int dest[] = getAttackableCoords(options, true);

		if (dest != null) {
			Unit uToAttack = (Unit)game.getGameSquareAt(dest[0], dest[1]).getOccupant();
		
			if (units.contains(uToAttack))
				doRandomMove(u, source, options);
			else {
				tryAttack(u, source, dest);
			}
		}
		else 
			doRandomMove(u, source, options);
	}

	private void doComputerTurn() {
		//System.out.println("Beginning computer turn");
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
				case 2: 
					doAttackAnything(u ,source, destOpt);
					break;
				case 3:
					doSeekAndDestroy(u, source, destOpt);
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
		System.out.println("Starting computer player...");
		Command com = null;
		
		try {
			while (true) {
				com = (Command) input.readObject();			
				System.out.println("Object read");
				
				if (com instanceof ClientServerCommand) {
					ClientServerCommand c = (ClientServerCommand)com;
					switch (c.getType()) {
					
						case Message: 
							break;
							
						case ComputerTurn:
							isAiTurn = true;
							doComputerTurn();
							isAiTurn = false;
							break;
						
						case SendingGame:
							System.out.println("Game received");
							Game g = null;
							
							try {
								g = (Game) input.readObject();
							} catch (ClassNotFoundException | IOException e) {
								e.printStackTrace();
							}
	
							setGame(g);
							break;
						default:
							break;
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

	@Override
	public void receiveMessage(ClientServerCommand com) {
	}

	@Override
	public void failLogin(ClientServerCommand com) {
	}

	@Override
	public void login() {
	}

	@Override
	public void updateAvailGameRooms(ArrayList<String> names) {
	}

	@Override
	public void setStartGameAvail() {
	}

	@Override
	public void showGamePanel() {
	}

	@Override
	public void updateUnitInfo(ArrayList<String> info) {
	}
}
