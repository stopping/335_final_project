package shared;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import server.Server;
import shared.GameSquare.Terrain;

@SuppressWarnings("serial")
public class Game implements Serializable {
	
	GameSquare[][] board;
	ArrayList<Unit> unitListRed = new ArrayList<Unit>();
	ArrayList<Unit> unitListBlue = new ArrayList<Unit>();
	Server server;
	
	int currentPlayer;
	
	
	public Game(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits) {
		
		char[][] fieldArray = {
				{'R','R','R','X',' ',' ',' ',' ',' ',' ',' ',' '},
				{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
				{' ','R','R','X',' ',' ','X','X','X','X',' ',' '},
				{' ','X','X','X',' ',' ',' ',' ',' ','X',' ',' '},
				{' ',' ','X',' ',' ',' ',' ',' ',' ','X',' ',' '},
				{' ',' ','X',' ',' ',' ',' ',' ',' ','X',' ',' '},
				{'X',' ','X',' ',' ',' ',' ',' ',' ',' ',' ',' '},
				{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
				{' ',' ','X',' ',' ',' ',' ',' ','X','X',' ','X'},
				{' ',' ','X','X',' ','X','X','X','X','B',' ','B'},
				{' ',' ',' ',' ',' ',' ',' ',' ','X','B',' ','B'},
				{' ',' ',' ',' ',' ','X',' ',' ',' ',' ',' ','B'} };
		
		int rows = fieldArray.length;
		int cols = fieldArray[0].length;
		
		board = new GameSquare[rows][cols];
		
		int redCount = 0;
		int blueCount = 0;
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				board[r][c] = new GameSquare( Terrain.Grass, r, c );
				if(fieldArray[r][c] == 'X') board[r][c].setOccupant(new Obstacle());
				if(fieldArray[r][c] == 'R') board[r][c].setOccupant(redUnits.get(redCount++));
				if(fieldArray[r][c] == 'B') board[r][c].setOccupant(blueUnits.get(blueCount++));
			}
		}
		
		currentPlayer = 0;
		
		unitListRed = redUnits;
		unitListBlue = blueUnits;
		
	}
	
	public boolean executeCommand( Command com ) {
		boolean executed = false;
		if(com == null) return false;
		
		switch(com.getCommandType()) {
		case Move: 
			executed = doMoveCommand(com);
			break;
		case Attack:
			executed = doAttackCommand(com);
			break;
		case EndTurn:
			executed = doEndTurnCommand(com);
			break;
		}
		
		return executed;
		
	}
	
	public boolean doMoveCommand( Command com ) {
		
		int srcCoords[] = com.getSource();
		int destCoords[] = com.getDest();
		GameSquare srcSquare = board[srcCoords[0]][srcCoords[1]];
		GameSquare destSquare = board[destCoords[0]][destCoords[1]];
		Unit performer = (Unit) srcSquare.getOccupant();
		Occupant atDest = destSquare.getOccupant();
		
		if( currentPlayer == 0 && !unitListRed.contains(performer) ) {
			System.out.println("Red does not own this unit!");
			return false;
		}
		if( currentPlayer == 1 && !unitListBlue.contains(performer) ) {
			System.out.println("Blue does not own this unit!");
			return false;
		}
		
		if( performer == null || !performer.isMovable() || atDest != null) {
			return false;
		}
		
		double apCost = Math.sqrt(Math.pow(srcCoords[0]-destCoords[0], 2)+Math.pow(srcCoords[1]-destCoords[1], 2));
		System.out.println(apCost);
		if(performer.getActionPoints() >= apCost && lineOfSightExists(srcSquare,destSquare)) {
			destSquare.setOccupant(performer);
			srcSquare.setOccupant(null);
			performer.consumeActionPoints(apCost);
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean doAttackCommand( Command com ) {
		
		int srcCoords[] = com.getSource();
		int destCoords[] = com.getDest();
		GameSquare srcSquare = board[srcCoords[0]][srcCoords[1]];
		GameSquare destSquare = board[destCoords[0]][destCoords[1]];
		Unit performer = (Unit) srcSquare.getOccupant();
		Occupant receiver = destSquare.getOccupant();
		
		if( currentPlayer == 0 && !unitListRed.contains(performer) ) {
			System.out.println("Red does not own this unit!");
			return false;
		}
		if( currentPlayer == 1 && !unitListBlue.contains(performer) ) {
			System.out.println("Blue does not own this unit!");
			return false;
		}
		
		if(!(performer instanceof Unit) || receiver == null) return false;
		
		if(lineOfSightExists(srcSquare, destSquare)) {
			performer.attack(receiver);
			return true;
		}
		
		return false;
	}
	
	public boolean doEndTurnCommand( Command com ) {
		for(int i = 0; i < unitListRed.size(); i++) {
			unitListRed.get(i).restoreActionPoints();
		}
		for(int i = 0; i < unitListBlue.size(); i++) {
			unitListBlue.get(i).restoreActionPoints();
		}
		currentPlayer = currentPlayer == 0 ? 1 : 0;
		return true;
	}

	public boolean isLegalPlay(Command com, int whoseTurn, int playerNumber) {
		// TODO Auto-generated method stub
		return false;
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
	private ArrayList<Point> BresenhamLine(int x0, int y0, int x1, int y1) {
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
	
}
