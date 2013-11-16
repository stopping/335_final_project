package shared;

public class Unit extends Occupant {
	
	private int strength;
	
	public Unit(String newName) {
		name = newName;
		strength = 6;
		defense = 0;
		hitPoints = 10;
		movable = true;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public void attack( Occupant o ) {
		o.takeDamage(strength);
	}

}
