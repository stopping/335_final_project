package shared;

public class HealthItem extends Item {
	
	

	public HealthItem(String newName, Attribute newAttribute, double newModifier, int newCost, Unit newUnit) {
		super(newName, newAttribute, newModifier, newCost, newUnit);
	}
	
	
	@Override 
	public void execute() {
		unit.heal(modifier);
	}
}
