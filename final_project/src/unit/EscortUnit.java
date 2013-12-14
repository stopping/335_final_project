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

	public EscortUnit(String newName) {
		super(newName);
		strength = 0;
		defense = 5;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 5;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 5.0;
		abilityRange = 5.0;
		abilityCoolDown = 2;
		abilityCoolDownToGo = 0;
		unitClass = UnitClass.Escort;
	}

	/**
	 * This unit's special ability is to defuse the bomb
	 */
	public boolean canUseAbility(int row, int col) {
		Occupant o = game.getGameSquareAt(row, col).getOccupant();
		return (canUseSpecialAbility(row, col) && o instanceof BombObstacle); 
	}
	
	public boolean useSpecialAbility(int row, int col) {
		if (canUseAbility(row, col)) {
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
		return isInRange(row, col, abilityRange) && lineOfSightExists(row, col) && abilityCoolDownToGo == 0;
		
	}
}
