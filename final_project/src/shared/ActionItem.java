package shared;

import unit.Unit;

@SuppressWarnings("serial")
public class ActionItem extends Item {

	public ActionItem(String newName,
			double newModifier, int newCost, Unit newUnit) {
		super(newName, Attribute.ActionPoints, newModifier, newCost, newUnit, ItemType.ActionItem);
	}

	public void use() {
		owner.restoreActionPoints(modifier);
	}
}
