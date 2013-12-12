package shared;

import game_commands.GameCommand;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import shared.GameSquare.Terrain;
import unit.Unit;


public class Game implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2029788461691510050L;
	GameSquare[][] board;
	List<Unit> unitListRed = new ArrayList<Unit>();
	List<Unit> unitListBlue = new ArrayList<Unit>();
	WinCondition victoryCondition;
	
	int winner = -1;
	int currentPlayer;
	
	
	public Game(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits, WinCondition condition, MapBehavior map) {
		
		char[][] fieldArray = map.getMap();
		
		int rows = fieldArray.length;
		int cols = fieldArray[0].length;
		
		board = new GameSquare[rows][cols];
		
		int redCount = 0;
		int blueCount = 0;
		
		for(Unit u : redUnits) 
			unitListRed.add(u.cloneUnit(u));
		
		for(Unit u : blueUnits) 
			unitListBlue.add(u.cloneUnit(u));
		
		for(Unit u : unitListRed) {
			u.setGame(this);
			u.setBoard();
		}
		
		for(Unit u : unitListBlue) {
			u.setGame(this);
			u.setBoard();
		}
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				board[r][c] = new GameSquare( Terrain.Grass, r, c );
				if(fieldArray[r][c] == 'X') board[r][c].setOccupant(new WallObstacle());
				if(fieldArray[r][c] == 'R') board[r][c].setOccupant(unitListRed.get(redCount++));
				if(fieldArray[r][c] == 'B') board[r][c].setOccupant(unitListBlue.get(blueCount++));
			}
		}
		
		currentPlayer = 0;
		victoryCondition = condition;
		
	}
	
	public GameSquare[][] getBoard() {
		return board;
	}
	
	public boolean executeCommand( GameCommand com ) {
		
		if(com == null) return false;
		
		return com.executeOn(this);
		
	}
	
	public boolean isTurn(Unit performer) {
		if( currentPlayer == 0 && !unitListRed.contains(performer) ) {
			System.out.println("Red does not own this unit!");
			return false;
		}
		if( currentPlayer == 1 && !unitListBlue.contains(performer) ) {
			System.out.println("Blue does not own this unit!");
			return false;
		}
		return true;
	}

	public boolean checkWinCondition() {
		switch(victoryCondition) {
		case Deathmatch:
			
			boolean allDead = true;
			for(Unit u : unitListRed) {
				if(!u.isDead()) allDead = false;
			}
			if(allDead) {
				winner = 1;
				return true;
			}
			
			allDead = true;
			for(Unit u : unitListBlue) {
				if(!u.isDead()) allDead = false;
			}
			if(allDead) {
				winner = 0;
				return true;
			}
			
			return false;
			
		case CTF:
			
			return false;
			
		case Demolition:
			
			return false;
			
		default:
			return false;
		}
		
	}

	public boolean isCurrentPlayer(int player) {
		return currentPlayer == player ? true : false;
	}
	
	@Override
	public String toString() {
		String output = "";
		for(int r = 0; r < board.length; r++) {
			for(int c = 0; c < board[0].length; c++) {
				output += board[r][c].toString() + " ";
			}
			output += "\n";
		}
		return output;
	}
	
	public String lineOfSightGrid( GameSquare g ) {
		String output = "";
		for(int r = 0; r < board.length; r++) {
			for(int c = 0; c < board[0].length; c++) {
				GameSquare s = board[r][c];
				output += lineOfSightExists(g,s) ? s.toString() + " " : "[X] ";
			}
			output += "\n";
		}
		return output;
	}
	
	public boolean lineOfSightExists( GameSquare g1, GameSquare g2 ) {
		int r0 = g1.rowLocation;
		int c0 = g1.colLocation;
		int r1 = g2.rowLocation;
		int c1 = g2.colLocation;
		ArrayList<Point> line = BresenhamLine(r0,c0,r1,c1);
		for(int i = 1; i < line.size() - 1; i++) {
			int r = line.get(i).x;
			int c = line.get(i).y;
			if(board[r][c].hasOccupant()) return false;
		}
		return true;
	}
	
	public GameSquare getGameSquareAt( int row, int col ) {
		int numRows = board.length;
		int numCols = board[0].length;
		if( row < 0 || row >= numRows || col < 0 || col >= numCols ) return null;
		return board[row][col];
	}
	
	
	/**
	 * Returns a list of grid tile locations which represent a line connecting
	 * two other tiles. Utilizes Bresenham's algorithm, adapted from code by:
	 * Cozic, Laurent. "Ray casting in a 2D tile-based environment", Code Project
	 * 15 Sep 2006, accessed 15 Nov 2013.
	 * 
	 * @param x0 - x coordinate of start point
	 * @param y0 - y coordinate of start point
	 * @param x1 - x coordinate of end point
	 * @param y1 - y coordinate of end point
	 * @return List of points on the line connecting start and end point
	 */
	public static ArrayList<Point> BresenhamLine(int x0, int y0, int x1, int y1) {
		ArrayList<Point> result = new ArrayList<Point>();

	    boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
	    int tmp;
	    if (steep) {
	    	tmp = x0;
	    	x0 = y0;
	    	y0 = tmp;
	    	tmp = x1;
	    	x1 = y1;
	    	y1 = tmp;
	    }
	    if (x0 > x1) {
	    	tmp = x0;
	    	x0 = x1;
	    	x1 = tmp;
	    	tmp = y0;
	    	y0 = y1;
	    	y1 = tmp;
	    }

	    int deltax = x1 - x0;
	    int deltay = Math.abs(y1 - y0);
	    int error = 0;
	    int ystep;
	    int y = y0;
	    if (y0 < y1) ystep = 1; else ystep = -1;
	    for (int x = x0; x <= x1; x++) {
	        if (steep) result.add(new Point(y, x));
	        else result.add(new Point(x, y));
	        error += deltay;
	        if (2 * error > deltax) {
	            y += ystep;
	            error -= deltax;
	        } else if (2 * error == deltax) {
	        	if(x != x1) {
			        if (steep) result.add(new Point(y, x+1));
			        else result.add(new Point(x+1, y));
	        	}
	            y += ystep;
	            error -= deltax;
	        }
	    }

	    return result;
	}
	
	public enum WinCondition {
		Deathmatch,
		CTF,
		Demolition;
	}
	
	public List<Unit> getRedUnitList() {
		return unitListRed;
	}
	
	public List<Unit> getBlueUnitList() {
		return unitListBlue;
	}

	public boolean moveUnit(int[] source, int[] dest) {
		GameSquare srcSquare = board[source[0]][source[1]];
		
		Unit performer;
		
		if(srcSquare.getOccupant() instanceof Unit) 
			performer = (Unit) srcSquare.getOccupant();
		else
			return false;
		
		if(!isTurn(performer))
			return false;
		
		// unit's move method now performs the requisite checks
		return performer.moveTo(dest[0],dest[1]);

	}

	public boolean attack(int[] source, int[] dest) {
		GameSquare srcSquare = board[source[0]][source[1]];
		
		Unit performer;
		
		if(srcSquare.getOccupant() instanceof Unit) 
			performer = (Unit) srcSquare.getOccupant();
		else
			return false;
		
		if(!isTurn(performer)) { return false; }
		
		checkWinCondition();
		// attack returns true if attack was successful false if otherwise
		// important to note that the unit's attack method now perform the requisite checks
		return performer.attack(dest[0],dest[1]);

	}
	
	public boolean useAbility(int[] source, int[] dest) {
		GameSquare srcSquare = board[source[0]][source[1]];
		
		Unit performer;
		
		if(srcSquare.getOccupant() instanceof Unit) 
			performer = (Unit) srcSquare.getOccupant();
		else
			return false;
		
		if(!isTurn(performer)) { return false; }
		
		// attack returns true if attack was successful false if otherwise
		// important to note that the unit's attack method now perform the requisite checks
		return performer.useSpecialAbility(dest[0],dest[1]);

	}

	public boolean endTurn() {
		for(Unit u : unitListRed) {
			u.restoreActionPoints();
			u.coolDown();
		}
		for(Unit u : unitListBlue) {
			u.restoreActionPoints();
			u.coolDown();
		}
		currentPlayer = (currentPlayer + 1) % 2;
		checkWinCondition();
		return true;
	}
	
	public boolean giveItem(int[] source, int[] dest, Item item) {
		GameSquare srcSquare = board[source[0]][source[1]];
		
		Unit performer;
		
		if(srcSquare.getOccupant() instanceof Unit) 
			performer = (Unit) srcSquare.getOccupant();
		else
			return false;
		
		//If it's not your turn you can't do anything
		if(!isTurn(performer))
			return false;
		//otherwise try to give the item. giveItem() returns true if successful otherwise false.
		else
			return performer.giveItem(dest[0],dest[1],item);

	}
	
	public boolean useItem(int[] source, int[] dest, int itemIndex) {
		GameSquare srcSquare = board[source[0]][source[1]];
		Unit performer = (Unit) srcSquare.getOccupant();

		//If it's not your turn you can't do anything
		if(!isTurn(performer)) {
			return false;
		}
		//otherwise try to give the item. giveItem() returns true if successful otherwise false.
		if (performer.getItemList().size() > 0) {
			Item toUse = performer.getItemList().get(itemIndex);
			return performer.useItem(toUse);
		}
		return false;
	}
	
	public int getWinner() {
		return winner;
	}
	
	public boolean isWon() {
		return winner >= 0;
	}
}

