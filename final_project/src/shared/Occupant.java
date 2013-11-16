package shared;

public abstract class Occupant {
	
	protected String name;
	protected GameSquare location;
	protected int hitPoints;
	protected int defense;
	protected boolean movable;
	
	public void setLocation( GameSquare g ) { location = g; }
	public GameSquare getLocation() { return location; }
	public boolean isMovable() { return movable; }
	protected String getName() { return name; }
	
	public String toString() {
		if(name == null) return " ";
		return name.substring(0,1);
	}
	
	public void takeDamage( int attackStrength ) {
		int damage = attackStrength - defense;
		hitPoints -= damage > 0 ? damage : 0;
		if(hitPoints <= 0) {
			hitPoints = 0;
			location.setOccupant(null);
			location = null;
		}
	}
	
	protected boolean equals( Unit otherUnit ) {
		return name.equals(otherUnit.getName());
	}
	
}
