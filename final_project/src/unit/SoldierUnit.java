package unit;

import item.ActionItem;
import item.HealthItem;
import item.Item;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class SoldierUnit extends Unit {

	public SoldierUnit(String newName) {
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
		addItem(new ActionItem("AP Serum",3.0,1,this));
		unitClass = UnitClass.Soldier;
	}
	
	@Override
	public String toString() {
		return "Name: " + this.name + " (Soldier)";
	}

}
