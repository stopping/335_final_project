package unit;

import game.GameSquare;
import item.HealthItem;
import item.Item;

import java.util.ArrayList;

import obstacle.Obstacle;

@SuppressWarnings("serial")
public class DemolitionUnit extends Unit {
	
	public DemolitionUnit(String newName) {
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
		addItem(new HealthItem("Stimpack",5.0,1,this));
		unitClass = UnitClass.Demolition;
	}
	
	@Override
	public boolean useSpecialAbility( int row, int col ) {
		if (canUseAbility(row,col)) {
			GameSquare gs = game.getGameSquareAt(row, col);
			gs.setOccupant(null);
			abilityCoolDownToGo = abilityCoolDown;
			return true;
		}
		return false;
	}
	
	public boolean canUseAbility( int row, int col ) {
		GameSquare gs = game.getGameSquareAt(row, col);
		if (gs != null && gs.hasOccupant() && gs.getOccupant() instanceof Obstacle && abilityCoolDownToGo == 0 && isInRange( row, col, abilityRange )) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Name: " + this.name + " (Demolition)";
	}
}
