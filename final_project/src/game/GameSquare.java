package game;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GameSquare implements Serializable {
	
	Terrain terrain;
	Occupant occupant;
	int rowLocation;
	int colLocation;
	
	public GameSquare( Terrain newTerrain, int rowLoc, int colLoc ) {
		terrain = newTerrain;
		occupant = null;
		rowLocation = rowLoc;
		colLocation = colLoc;
	}
	
	public enum Terrain {
		Grass;
	}
	
	public void setOccupant(Occupant o) {
		if(occupant != null) occupant.setLocation(null);
		occupant = o;
		if(o != null) o.setLocation(this);
	}
	
	public Occupant getOccupant() {
		return occupant;
	}
	
	public String toString() {
		String symbol = occupant == null ? " " : occupant.toString();
		return "[" + symbol + "]";
	}

	public boolean hasOccupant() {
		return occupant != null;
	}

	public int getRow() {
		return rowLocation;
	}
	
	public int getCol() {
		return colLocation;
	}
	
}
