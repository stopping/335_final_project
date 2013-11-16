package shared;

public abstract class Occupant {
	
	protected String name;
	protected GameSquare location;
	protected double hitPoints;
	protected double maxHitPoints;
	protected double defense;
	protected final double damageReduction = 0.1;
	
	protected Occupant( String newName ) {
		name = newName;
		location = null;
	}
	
	public void setLocation( GameSquare g ) { location = g; }
	public GameSquare getLocation() { return location; }
	protected String getName() { return name; }
	
	public void heal( double amount ) {
		hitPoints = hitPoints + amount > maxHitPoints ? maxHitPoints : hitPoints + amount ;
	}
	
	public String toString() {
		if(name == null) return " ";
		return name.substring(0,1);
	}
	
	public void takeDamage( double attackStrength ) {
		double damage = attackStrength*(1 - defense*damageReduction);
		hitPoints -= damage > 0 ? damage : 0;
		checkDeath();
	}
	
	protected void checkDeath() {
		if(hitPoints <= 0) {
			hitPoints = 0;
			location.setOccupant(null);
			location = null;
		}
	}
	
	public boolean isDead() {
		return location == null;
	}
	
	protected boolean equals( Occupant otherOccupant ) {
		return name.equals(otherOccupant.getName());
	}
	
	
	
}
