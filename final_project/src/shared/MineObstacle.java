package shared;

@SuppressWarnings("serial")
public class MineObstacle extends Obstacle {
	
	public MineObstacle () {
		super("Mine");
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		defense = 0;
	}

}
