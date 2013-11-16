package shared;

public class Item {

	private String name;
	private Attribute attribute;
	private double modifier;
	private int cost;
	
	public Item (String newName, Attribute newAttribute, double newModifier, int newCost ) {
		name = newName;
		attribute = newAttribute;
		modifier = newModifier;
		cost = newCost;
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
	
	public int getResaleValue() {
		return cost * 3 / 4;
	}

}
