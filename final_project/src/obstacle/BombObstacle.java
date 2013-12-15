package obstacle;

import unit.Unit;

@SuppressWarnings("serial")
public class BombObstacle extends Obstacle {

	private int source;
	
	public BombObstacle(int team) {
		super("Bomb");
		maxHitPoints = 10000;
		hitPoints = maxHitPoints;
		defense = 10000;
		this.source = team;
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
	
	public int getSource() {
		return this.source;
	}

}
