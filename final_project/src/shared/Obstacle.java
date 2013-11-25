package shared;

@SuppressWarnings("serial")
public class Obstacle extends Occupant {
	
	public Obstacle() {
		super("Wall");
		hitPoints = 10;
		defense = 0;
	}
	
}
