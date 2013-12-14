package obstacle;

import game.Occupant;

@SuppressWarnings("serial")
public abstract class Obstacle extends Occupant {
	
	public Obstacle(String type) {
		super(type);
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		defense = 0;
	}
	
}
