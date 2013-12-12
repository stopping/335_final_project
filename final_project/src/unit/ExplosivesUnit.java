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
		attackRange = 3.0;
		abilityRange = 1.5;
		itemList = new ArrayList<Item>();
		abilityCoolDown = 4;
		abilityCoolDownToGo = 0;
		addItem(new MineItem("Mine", 1, this));
		unitClass = UnitClass.Demolition;
	}
	
	@Override
	public boolean useSpecialAbility( int row, int col ) {
		if (canUseAbility(row,col)) {
			GameSquare gs = game.getGameSquareAt(row, col);
			gs.setOccupant(new MineObstacle());
			removeItem(new MineItem("Mine", 0, this));
			abilityCoolDownToGo = abilityCoolDown;
			return true;
		}
		return false;
	}
	
	public boolean canUseAbility( int row, int col ) {
		GameSquare gs = game.getGameSquareAt(row, col);
		if (gs != null && gs.hasOccupant() && gs.getOccupant() instanceof Obstacle 
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
