package map;

import game.GameSquare;
import game.GameSquare.Terrain;

import java.io.Serializable;
import java.util.ArrayList;

import obstacle.WallObstacle;
import unit.Unit;

@SuppressWarnings("serial")
public class StandardMap implements MapBehavior, Serializable {

	private char[][] charBoard = {
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
			{' ',' ',' ',' ',' ','X',' ',' ',' ',' ',' ','B'}};
	
	
	private GameSquare[][] board;

	
	public StandardMap() {
		board = new GameSquare[charBoard.length][charBoard[0].length];
	}
	
	@Override
	public GameSquare[][] getMap() {
		return board;
	}
	
	public void setMap(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits) {
		int redCount = 0, blueCount = 0;
		
		for (int r = 0; r < charBoard.length; r++) {
			for (int c = 0; c < charBoard[r].length; c++) {
				board[r][c] = new GameSquare( Terrain.Grass, r, c );
				if(charBoard[r][c] == 'X') board[r][c].setOccupant(new WallObstacle());
				if(charBoard[r][c] == 'R') board[r][c].setOccupant(redUnits.get(redCount++));
				if(charBoard[r][c] == 'B') board[r][c].setOccupant(blueUnits.get(blueCount++));
			}
		}
	}

}
