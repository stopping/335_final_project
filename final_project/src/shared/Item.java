package shared;

public class Item {

	

	protected String name;
	protected Attribute attribute;
	protected double modifier;
	protected int cost;
	protected Unit unit;
	
	public Item (String newName, Attribute newAttribute, double newModifier, int newCost, Unit newUnit ) {
		name = newName;
		attribute = newAttribute;
		modifier = newModifier;
		cost = newCost;
		unit = newUnit;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}

	public double getModifier() {
		return modifier;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setOwner(Unit newUnit) {
		unit = newUnit;
	}
	
	public int getResaleValue() {
		return cost * 3 / 4;
	}
	
	/**
	 * This method may exist solely to be overwritten by items inheriting this class 
	 */
	public void execute() {
		
	}
}
