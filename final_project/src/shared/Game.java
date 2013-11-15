package shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Game implements Serializable {

	public boolean isLegalPlay(Command com, int whoseTurn, int playerNumber) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString() {
		String ret = "the Game[][]";
		return ret;
	}
	
}
