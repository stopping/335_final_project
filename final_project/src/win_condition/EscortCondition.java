package win_condition;

import game.Game;
import game.GameSquare;

import java.io.Serializable;

import obstacle.BombObstacle;

@SuppressWarnings("serial")
public class EscortCondition implements WinCondition, Serializable {

	Game game;
	
	public EscortCondition() {
		game = null;
	}
	
	
	public GameSquare[][] setWinCondition(GameSquare[][] modifyBoard) {
		modifyBoard[1][1].setOccupant(new BombObstacle(0));
		modifyBoard[10][10].setOccupant(new BombObstacle(1));
		return modifyBoard;
	}

	@Override
	public int checkWinCondition() {
		if (game.isWon()) 
			return game.getWinner();
		
		return -1;
	}
	
	public void setGame(Game g) {
		game = g;
	}
	
	@Override
	public String toString() {
		return "Escort";
	}



}
