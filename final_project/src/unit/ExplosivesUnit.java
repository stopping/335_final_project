package unit;

import java.util.ArrayList;

import shared.GameSquare;
import shared.Item;
import shared.Item.ItemType;
import shared.MineItem;
import shared.MineObstacle;
import shared.Obstacle;

@SuppressWarnings("serial")
public class ExplosivesUnit extends Unit {

	public ExplosivesUnit(String newName) {
		super(newName);
		strength = 7.0;
		defense = 0;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 3;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 1.0;
		abilityRange = 1.5;
		itemList = new ArrayList<Item>();
		abilityCoolDown = 4;
		abilityCoolDownToGo = 0;
		addItem(new MineItem("Mine", 1, this));
		unitClass = UnitClass.Explosives;
	}
	
	@Override
	public boolean attack( int row, int col ) {
		if (canPlaceMine(row,col)) {
			GameSquare gs = game.getGameSquareAt(row, col);
			gs.setOccupant(new MineObstacle(this));
			removeItem(new MineItem("Mine", 1, null));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canAttack( int row, int col ) {
		GameSquare gs = game.getGameSquareAt(row, col);
		return isInRange( row, col, attackRange ) && actionPoints >= 2.0 && lineOfSightExists(row,col) && !gs.hasOccupant() ;
	}
	
	@Override
	public boolean useSpecialAbility( int row, int col ) {
		if (canUseAbility(row,col)) {
			// detect mines 
			abilityCoolDownToGo = abilityCoolDown;
			return true;
		}
		return false;
	}
	
	public boolean canPlaceMine( int row, int col ) {
		GameSquare gs = game.getGameSquareAt(row, col);
		if (gs != null && !gs.hasOccupant()
				&& abilityCoolDownToGo == 0 && isInRange( row, col, abilityRange )
					&& hasAMine()) {
			return true;
		}
		return false;
	}
	
	public boolean hasAMine() {
		for (Item i : itemList)
			if (i.getType() == ItemType.MineItem)
				return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "Name: " + this.name + "(Explosives)";
	}

}
