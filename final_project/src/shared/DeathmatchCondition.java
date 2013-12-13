package shared;

import java.io.Serializable;
import java.util.ArrayList;

import unit.Unit;


@SuppressWarnings("serial")
public class DeathmatchCondition implements WinCondition, Serializable {

	
	
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
