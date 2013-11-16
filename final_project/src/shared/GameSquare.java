package shared;

public class GameSquare {
	
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
	
}
