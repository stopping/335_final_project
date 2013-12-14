package unit;

import java.util.ArrayList;

import shared.Attribute;
import shared.GameSquare;
import shared.HealthItem;
import shared.Item;
import shared.Occupant;

@SuppressWarnings("serial")
public class RocketUnit extends Unit {

	public RocketUnit(String newName) {
		super(newName);
		strength = 6;
		defense = 0;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 3;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 5.0;
		abilityRange = 5.0;
		itemList = new ArrayList<Item>();
		addItem(new HealthItem("Stimpack",5.0,1,this));
		unitClass = UnitClass.Rocket;
	}
	
	public boolean attack( int row, int col ) {
		if (canAttack(row,col)) {
			
			for(int r = row-1; r <= row+1; r++) {
				for(int c = col-1; c <= col+1; c++) {
					GameSquare gs = game.getGameSquareAt(r, c);
					if(gs == null || !gs.hasOccupant()) continue;
					Occupant o = game.getGameSquareAt(r, c).getOccupant();
					double attackModifier = getModifier(Attribute.Strength);
					double distance = Math.sqrt(Math.pow(r-row, 2)+Math.pow(c-col, 2));
					double multiplier = (1.0-distance/2.0);
					multiplier = multiplier >= 0 ? multiplier : 0 ;
					o.takeDamage((strength+attackModifier)*multiplier); 
				}
			}
			consumeActionPoints(2.0);
			return true;
		}
		return false;
	}

	public boolean canAttack( int row, int col ) {
		return isInRange( row, col, attackRange ) && actionPoints >= 2.0 && lineOfSightExists(row,col);
	}
	
	@Override
	public String toString() {
		return "Name: " + this.name + " (Rocket)";
	}

}
