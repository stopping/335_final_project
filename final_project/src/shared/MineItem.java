package shared;

import unit.Unit;

@SuppressWarnings("serial")
public class MineItem extends Item {

	public MineItem(String newName, int newCost, Unit newUnit) {
		super(newName, null, 0, newCost, newUnit, ItemType.MineItem);
	}
}
