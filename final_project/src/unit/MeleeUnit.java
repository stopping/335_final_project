package unit;

import java.util.ArrayList;

import shared.ActionItem;
import shared.HealthItem;
import shared.Item;

@SuppressWarnings("serial")
public class MeleeUnit extends Unit {

	public MeleeUnit(String newName) {
		super(newName);
		strength = 8;
		defense = 3;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 3;
		actionPoints = maxActionPoints;
		speed = 1.5;
		attackRange = 1.5;
		itemList = new ArrayList<Item>();
		addItem(new ActionItem("AP Serum",3.0,1,this));
		unitClass = UnitClass.Melee;
	}
	
	@Override
	public String toString() {
		return "Name: " + this.name + " (Melee)";
	}
	
}
