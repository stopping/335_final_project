package unit;

import java.util.ArrayList;

import shared.GameSquare;
import shared.HealthItem;
import shared.Item;
import shared.Obstacle;

public class EngineerUnit extends Unit {

	public EngineerUnit(String newName) {
		super(newName);
		strength = 4;
		defense = 0;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 5;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 5.0;
		abilityRange = 1.5;
		itemList = new ArrayList<Item>();
		abilityCoolDown = 3;
		abilityCoolDownToGo = 0;
		addItem(new HealthItem("Stimpack",5.0,1,this));
	}
	
	@Override
	public boolean useSpecialAbility( int row, int col ) {
		if (canUseAbility(row,col)) {
			GameSquare gs = game.getGameSquareAt(row, col);
			gs.setOccupant(new Obstacle());
			return true;
		}
		return false;
	}
	
	public boolean canUseAbility( int row, int col ) {
		GameSquare gs = game.getGameSquareAt(row, col);
		if (gs != null && !gs.hasOccupant() && abilityCoolDownToGo == 0 && isInRange( row, col, abilityRange )) {
			return true;
		}
		return false;
	}

}
