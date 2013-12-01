package shared;

import unit.Unit;

@SuppressWarnings("serial")
public class HealthItem extends Item {

	public HealthItem(String newName, double newModifier, int newCost, Unit newUnit) {
		super(newName, Attribute.HitPoints, newModifier, newCost, newUnit);
	}
	
	@Override
	public void use() {
		owner.heal(modifier);
		owner.removeItem(this);
	}
	
}
