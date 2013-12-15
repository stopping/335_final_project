package win_condition;

import game.Game;
import game.GameSquare;

public interface WinCondition {
	
	public GameSquare[][] setWinCondition(GameSquare[][] toModify);
	
	public int checkWinCondition();
	
	public void setGame(Game g);
}
