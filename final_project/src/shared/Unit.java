package shared;

public class Unit extends Occupant {
	
	private int strength;
	private double actionPoints;
	private int speed;
	
	public Unit(String newName) {
		name = newName;
		strength = 6;
		defense = 0;
		hitPoints = 10;
		movable = true;
		actionPoints = 5;
		speed = 1;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public void attack( Occupant o ) {
		o.takeDamage(strength);
	}
	
	public void consumeActionPoints(double pointsUsed) {
		actionPoints -= pointsUsed;
	}

	public double getActionPoints() {
		return actionPoints;
	}
}
