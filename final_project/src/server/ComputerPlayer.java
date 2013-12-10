package server;

import game_commands.AttackCommand;
import game_commands.EndTurnCommand;
import game_commands.GameCommand;
import game_commands.MoveCommand;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import shared.Command;
import client_commands.*;
import server_commands.ComputerTurn;
import server_commands.ComputerDifficultySet;
import server_commands.IllegalOption;
import server_commands.SendingGame;
import server_commands.ServerCommand;
import shared.Game;
import shared.Occupant;
import shared.Player;
import unit.*;
import shared.GameSquare;

public class ComputerPlayer implements Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private int difficulty;
	protected Game game;
	protected List<Unit> units;

	public ComputerPlayer(int gameRoom) {
		System.out.println("Computer player created");
		Socket sock = null;
		difficulty = 0;

		try {
			sock = new Socket("localhost", 4009);		
			//sock.setSoTimeout(4000);
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			output.writeObject(new ComputerPlayerJoin(gameRoom));
			
		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}
	
	public void setLevel(int level) {
		this.difficulty = level;
	}

	public boolean executeGameCommand(GameCommand c) {	
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
			Command c = new MoveCommand(source, dest);
			sendCommand(c);
			
			try {
				Command ret = (Command) input.readObject();
				if (ret instanceof ServerCommand) {
					ServerCommand com = (ServerCommand)ret;
					if (com instanceof IllegalOption)
						return false;
				}
				else {
					GameCommand com = (GameCommand) ret;
					return executeGameCommand(com);
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
				if (ret instanceof ServerCommand) {
					ServerCommand com = (ServerCommand)ret;
					if (com instanceof IllegalOption)
						return false;
				}
				else {
					GameCommand com = (GameCommand) ret;
					return executeGameCommand(com);
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
		System.out.println("starting seek and destroy for unit: " + u.getName());
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
			if (game.getGameSquareAt(dest[0], dest[1]).getOccupant() instanceof Unit) {
				Unit uToAttack = (Unit)game.getGameSquareAt(dest[0], dest[1]).getOccupant();
		
				if (units.contains(uToAttack))
					doRandomMove(u, source, options);
		
				else 
					tryAttack(u, source, dest);
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
				case 2: 
					doAttackAnything(u ,source, destOpt);
					break;
				case 3:
					doSeekAndDestroy(u, source, destOpt);
					break;
				}
			}
		}
		System.out.println("Ending computer turn");

		sendCommand(new EndTurnCommand());
	}
	
	public void setGame(Game g) {
		this.game = g;
	}

	
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
				System.out.println("Computer Player: Object read");
				
				if (com instanceof ServerCommand) {
					ServerCommand c = (ServerCommand)com;

						if (c instanceof ComputerTurn) {				
							isAiTurn = true;
							doComputerTurn();
							isAiTurn = false;
						}
						else if (c instanceof SendingGame) {
							System.out.println("Game received by CopmuterPlayer");
							setGame(((SendingGame) c).getGame());
						}
						else if (c instanceof ComputerDifficultySet) {
							ComputerDifficultySet levelcom = (ComputerDifficultySet)c;
							System.out.println("Set difficulty level: " + levelcom.level);
							setLevel(levelcom.level);
						}
					}
				
				else if (com instanceof GameCommand && !isAiTurn) {
					executeGameCommand((GameCommand)com);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
