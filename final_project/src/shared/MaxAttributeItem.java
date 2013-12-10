package shared;

import unit.Unit;

public class MaxAttributeItem extends Item {

	public MaxAttributeItem(String newName, Attribute newAttribute, double newModifier, int newCost, Unit newUnit) {
		super(newName, newAttribute, newModifier, newCost, newUnit, ItemType.MaxAttributeItem);
	}
	
	public void use() {
		owner.upgrade(attribute, modifier);
	}
	
	public void remove() {
		owner.upgrade(attribute, -(modifier));
	}

}
