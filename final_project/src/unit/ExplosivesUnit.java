package unit;

import java.util.ArrayList;

import shared.GameSquare;
import shared.Item;
import shared.Item.ItemType;
import shared.MineItem;
import shared.MineObstacle;
import shared.Obstacle;
import shared.Occupant;

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
		abilityRange = 3;
		itemList = new ArrayList<Item>();
		abilityCoolDown = 4;
		abilityCoolDownToGo = 0;
		addItem(new MineItem("Mine", 1, this));
		unitClass = UnitClass.Explosives;
	}
	
	@Override
	public boolean useSpecialAbility( int row, int col ) {
		if (canUseAbility(row, col)) 
			if (mineSense(row, col)) {
				addItem(new MineItem("Mine", 1, this));
				return true;
			}
		return false;
	}
	
	public boolean canUseAbility( int row, int col ) {
		if (abilityCoolDownToGo == 0 && isInRange( row, col, abilityRange )) 
			return true;
		return false;
	}
	
	public boolean mineSense(int row, int col) {
		boolean ret = false;
		for (int r = row-1 ; r < row+2 ; r++) 
			for (int c = col-1 ; c < col+2 ; c++) 
				if (r > -1 && r < 13 && c > -1 && c < 13 ) 
					if (game.getGameSquareAt(r, c).hasOccupant()) {
						Occupant o = game.getGameSquareAt(r, c).getOccupant();
						if ( o instanceof MineObstacle) {
							System.out.println("its a mine");
							ret = true;
							game.getGameSquareAt(r, c).setOccupant(null);
						}
					}
		return ret;
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
		return isInRange( row, col, attackRange ) && actionPoints >= 2.0 && 
			 hasAMine() && lineOfSightExists(row,col) && !gs.hasOccupant() ;
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
