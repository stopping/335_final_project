package obstacle;

import java.util.List;

import unit.Unit;

@SuppressWarnings("serial")
public class MineObstacle extends Obstacle {
	
	private Unit placer;
	
	public MineObstacle (Unit u) {
		super("Mine");
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		defense = 0;
		placer = u;
	}
	
	public boolean isVisible(List<Unit> units) {
		for (Unit u : units)
			if (u.equals(placer))
				return true;
		return false;
	}
}
