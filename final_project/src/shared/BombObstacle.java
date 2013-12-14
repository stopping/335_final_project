package shared;

@SuppressWarnings("serial")
public class BombObstacle extends Obstacle {

	
	
	public BombObstacle(String type) {
		super(type);
		maxHitPoints = 10000;
		hitPoints = maxHitPoints;
		defense = 10000;
	}
	/**
	 * This should only be called by EscortUnit's special ability 
	 * Sets hitPoints to 0 and calls checkDeath() which sets the GameSquare 
	 * it's on to point to null
	 */
	public void defuse() {
		hitPoints = 0;
		checkDeath();
	}

}
