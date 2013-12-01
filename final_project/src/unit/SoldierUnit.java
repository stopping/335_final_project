package unit;

import java.util.ArrayList;

import shared.HealthItem;
import shared.Item;

public class SoldierUnit extends Unit {

	public SoldierUnit(String newName) {
		super(newName);
		strength = 6;
		defense = 0;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 5;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 5.0;
		abilityRange = 5.0;
		itemList = new ArrayList<Item>();
		abilityCoolDown = 3;
		abilityCoolDownToGo = 3;
		addItem(new HealthItem("Stimpack",5.0,1,this));
	}

}
