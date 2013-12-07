package shared;

@SuppressWarnings("serial")
public class ObstacleMap implements MapBehavior {
	
	private char[][] board = {
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
	

	public ObstacleMap() {
		
	}
	
	
	public char[][] getMap() {
		return board;
	}
}
