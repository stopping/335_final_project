package unit;

import game.Occupant;
import obstacle.BombObstacle;
import unit.Unit.UnitClass;

/**
 * 
 * This is the unit that will be escorted around for the escort game type
 * 
 * Doesn't have items, defense slightly buffed since it can't attack, standard amount of hitpoints
 * 
 * When clicked on in the GUI should only show squares it can move to, not attack 
 */
@SuppressWarnings("serial")
public class EscortUnit extends Unit {

	protected int team;
	
	public EscortUnit(String newName, int team) {
		super(newName);
		strength = 0;
		defense = 5;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 5;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 5.0;
		abilityRange = 1;
		abilityCoolDown = 2;
		abilityCoolDownToGo = 0;
		unitClass = UnitClass.Escort;
		this.team = team;
	}

	/**
	 * This unit's special ability is to defuse the bomb
	 */
	public boolean canUseAbility(int row, int col) {
		Occupant o = game.getGameSquareAt(row, col).getOccupant();
		return (canUseSpecialAbility(row, col) && o instanceof BombObstacle); 
	}
	
	public boolean useSpecialAbility(int row, int col) {
		if (canUseSpecialAbility(row, col)) {
			BombObstacle bomb = (BombObstacle)game.getGameSquareAt(row, col).getOccupant();
			bomb.defuse();
			return true;
		}
		return false;
		
	}
	/**
	 * Can't attack 
	 */
	public boolean canAttack(int row, int col) {
		return false;
	}
	
	public boolean canUseSpecialAbility(int row, int col) {
		if (game.getGameSquareAt(row, col) != null) {
			if (game.getGameSquareAt(row, col).hasOccupant() 
					&& game.getGameSquareAt(row, col).getOccupant() instanceof BombObstacle) {
					BombObstacle bomb = (BombObstacle)game.getGameSquareAt(row, col).getOccupant();
						if (bomb.getSource() == team) 
							return false;
						else 
							return true;
					}
			return false;
		}
		return false;
	}
}
