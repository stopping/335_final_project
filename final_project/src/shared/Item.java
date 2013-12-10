package shared;

import java.io.Serializable;

import unit.Unit;

@SuppressWarnings("serial")
public class Item implements Serializable {

	protected String name;
	protected Attribute attribute;
	protected double modifier;
	protected int cost;
	protected Unit owner;
	protected ItemType type;
	
	public Item (String newName, Attribute newAttribute, double newModifier, int newCost, Unit newUnit, ItemType type) {
		name = newName;
		attribute = newAttribute;
		modifier = newModifier;
		cost = newCost;
		owner = newUnit;
		this.type = type;
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
		owner = newUnit;
	}
	
	public int getResaleValue() {
		return cost * 3 / 4;
	}
	
	/**
	 * This method may exist solely to be overwritten by items inheriting this class 
	 */
	
	public void remove() {
		
	}
	public void use() {
		
	}
	
	public ItemType getType() {
		return this.type;
	}
	
	public String toString() {
		return name;
	}
	
	public enum ItemType {
		HealthItem
	}
}
