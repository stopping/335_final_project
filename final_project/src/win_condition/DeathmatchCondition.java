package win_condition;

import game.Game;
import game.GameSquare;

import java.io.Serializable;
import java.util.ArrayList;

import unit.Unit;


@SuppressWarnings("serial")
public class DeathmatchCondition implements WinCondition, Serializable {

	Game game;
	
	public DeathmatchCondition() {
		game = null;
	}
	
	
	public GameSquare[][] setWinCondition(GameSquare[][] modifyBoard) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int checkWinCondition() {
		ArrayList<Unit> redUnits = (ArrayList<Unit>) game.getRedUnitList();
		ArrayList<Unit> blueUnits = (ArrayList<Unit>) game.getBlueUnitList();
		
		boolean redLost = true;
		boolean blueLost = true;
		
		for (Unit u : redUnits) {
			if (!u.isDead()) {
				redLost = false;
			}
		}
		
		for (Unit u : blueUnits) {
			if (!u.isDead()) {
				blueLost = false;
			}
		}
		
		if (redLost)
			return 0;
		if (blueLost)
			return 1;
		return -1;
	}
	
	public void setGame(Game g) {
		game = g;
	}
	
	@Override
	public String toString() {
		return "Deathmatch";
	}

}
