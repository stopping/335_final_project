package unit;

import java.util.ArrayList;

import shared.Item;

public class MeleeUnit extends Unit {

	public MeleeUnit(String newName) {
		super(newName);
		strength = 8;
		defense = 3;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 5;
		actionPoints = maxActionPoints;
		speed = 1.5;
		attackRange = 1.0;
		itemList = new ArrayList<Item>();
	}
	
}
