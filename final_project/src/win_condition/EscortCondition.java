package win_condition;

import game.Game;
import game.GameSquare;

import java.io.Serializable;
import java.util.ArrayList;

import unit.Unit;

@SuppressWarnings("serial")
public class EscortCondition implements WinCondition, Serializable {

	Game game;
	
	public EscortCondition() {
		game = null;
	}
	
	
	public GameSquare[][] setWinCondition(GameSquare[][] modifyBoard) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int checkWinCondition() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	public void setGame(Game g) {
		game = g;
	}




}
