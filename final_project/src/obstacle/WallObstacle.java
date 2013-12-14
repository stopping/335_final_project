package obstacle;

@SuppressWarnings("serial")
public class WallObstacle extends Obstacle {

	public WallObstacle() {
		super("Wall");
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		defense = 0;
	}
}
