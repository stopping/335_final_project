package shared;

import java.util.ArrayList;
import java.util.List;

public class Unit extends Occupant {
	
	private double strength;
	private double actionPoints;
	private double maxActionPoints;
	private double attackRange;
	private double speed;
	private List<Item> itemList;
	
	public Unit(String newName) {
		super(newName);
		strength = 6;
		defense = 0;
		maxHitPoints = 10;
		hitPoints = maxHitPoints;
		maxActionPoints = 5;
		actionPoints = maxActionPoints;
		speed = 1.0;
		attackRange = 5.0;
		itemList = new ArrayList<Item>();
	}
	
	public void takeDamage( double attackStrength ) {
		double defenseModifier = 0;
		for(int i = 0; i < itemList.size(); i++) {
			Item currItem = itemList.get(i);
			if(currItem.getAttribute() == Attribute.Defense) defenseModifier += currItem.getModifier();
		}
		double damage = attackStrength*(1 - (defense+defenseModifier)*damageReduction);
		hitPoints -= damage > 0 ? damage : 0;
		checkDeath();
	}
	
	public double getStrength() {
		return strength;
	}
	
	public void attack( Occupant o ) {
		double attackModifier = 0;
		for(int i = 0; i < itemList.size(); i++) {
			Item currItem = itemList.get(i);
			if(currItem.getAttribute() == Attribute.Strength) attackModifier += currItem.getModifier();
		}
		o.takeDamage(strength+attackModifier);
	}
	
	public boolean isInRange( Occupant o ) {
		int aRow = this.getLocation().getRow();
		int aCol = this.getLocation().getCol();
		int tRow = o.getLocation().getRow();
		int tCol = o.getLocation().getCol();
		double distance = Math.sqrt(Math.pow(aRow-tRow, 2)+Math.pow(aCol-tCol, 2));
		return attackRange >= distance;
	}
	
	public void consumeActionPoints(double pointsUsed) {
		actionPoints -= pointsUsed;
	}

	public double getActionPoints() {
		return actionPoints;
	}
	
	public void restoreActionPoints() {
		actionPoints = maxActionPoints;
	}
	
	public boolean addItem(Item i) {
		if(inventoryHasRoom()) {
			itemList.add(i);
			return true;
		}
		return false;
	}
	
	public boolean removeItem(Item i) {
		if(itemList.contains(i)) {
			itemList.remove(i);
			return true;
		}
		return false;
	}
	
	public void upgrade( Attribute a, double value) {
		switch(a) {
		case Strength:
			strength += value;
			break;
		case Defense:
			defense += value;
			break;
		case MaxHitPoints:
			maxHitPoints += value;
			break;
		case MaxActionPoints:
			maxActionPoints += value;
			break;
		default:
			break;
		}
	}

	public void moveTo(GameSquare destSquare) {
		int srcRow = location.getRow();
		int destRow = destSquare.getRow();
		int srcCol = location.getCol();
		int destCol = destSquare.getCol();
		double apCost = Math.sqrt(Math.pow(srcRow-destRow, 2)+Math.pow(srcCol-destCol, 2));
		
		location.setOccupant(null);
		destSquare.setOccupant(this);
		consumeActionPoints(apCost/speed);		
	}

	public boolean canMoveTo(GameSquare destSquare) {
		int srcRow = location.getRow();
		int destRow = destSquare.getRow();
		int srcCol = location.getCol();
		int destCol = destSquare.getCol();
		double apCost = Math.sqrt(Math.pow(srcRow-destRow, 2)+Math.pow(srcCol-destCol, 2));
		
		return apCost/speed >= actionPoints;
	}

	public boolean giveItem( Item i, Unit receiver ) {
		if(itemList.contains(i) && receiver.inventoryHasRoom()) {
			removeItem(i);
			receiver.addItem(i);
			return true;
		}
		return false;
	}

	private boolean inventoryHasRoom() {
		return itemList.size() < 3;
	}

}
