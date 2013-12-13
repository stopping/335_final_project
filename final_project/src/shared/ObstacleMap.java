package shared;

import java.util.ArrayList;

import shared.GameSquare.Terrain;
import unit.Unit;

@SuppressWarnings("serial")
public class ObstacleMap implements MapBehavior {
	
	private char[][] charBoard = {
			{'X','X','X','X','X',' ',' ',' ',' ',' ',' ',' '},
			{'X','R','R','X',' ','X',' ',' ',' ','X','X','X'},
			{'X','R','R','X',' ',' ','X',' ','X',' ',' ',' '},
			{'X','R','X','X',' ',' ',' ','X',' ','X',' ',' '},
			{'X','X','X','X',' ',' ','X',' ','X','X',' ',' '},
			{'X','X','X',' ','X','X','X','X',' ','X',' ',' '},
			{'X','X','X','X','X','X',' ','X',' ','X','X','X'},
			{' ',' ','X','X',' ','X',' ','X',' ','X',' ',' '},
			{' ',' ','X',' ',' ','X',' ','X','X','X','X','X'},
			{'X','X','X','X',' ','X','X','X','X','X','B','B'},
			{' ','X',' ',' ','X',' ',' ',' ','X','B','B','B'},
			{'X',' ',' ',' ',' ','X',' ',' ',' ','X','X','X'}};
	
	
	private GameSquare[][] board;

	
	public ObstacleMap() {
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
