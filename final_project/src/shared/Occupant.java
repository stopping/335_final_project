package shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class Occupant implements Serializable {
	
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
	public String getName() { return name; }
	
	public void heal( double amount ) {
		hitPoints = hitPoints + amount > maxHitPoints ? maxHitPoints : hitPoints + amount ;
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

	public String toString() {
		if(name == null) return "";
		return "Name: " + name + "\n" +
				"HP: " + (int) hitPoints + "/" + (int) maxHitPoints + "\n";
	}
	
	
	
}
