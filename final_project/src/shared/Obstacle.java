package shared;

@SuppressWarnings("serial")
public class Obstacle extends Occupant {
	
	public Obstacle() {
		super("Wall");
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		defense = 0;
	}
	
}
