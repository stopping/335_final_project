package shared;

import java.util.ArrayList;

import unit.Unit;


public class DeathmatchCondition implements WinCondition {

	
	
	public DeathmatchCondition() {
	}
	
	
	public GameSquare[][] setWinCondition(GameSquare[][] modifyBoard) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean checkWinCondition(ArrayList<Unit> units) {
		for (Unit u : units) {
			if (!u.isDead()) {
				return false;
			}
		}
		
		return true;
	}

}
