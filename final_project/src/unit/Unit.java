package unit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import shared.Attribute;
import shared.Game;
import shared.GameSquare;
import shared.Item;
import shared.Occupant;

@SuppressWarnings("serial")
public class Unit extends Occupant {
	
	protected double strength;
	protected double actionPoints;
	protected double maxActionPoints;
	protected double attackRange;
	protected double speed;
	protected List<Item> itemList;
	protected int abilityCoolDown;
	protected int abilityCoolDownToGo;
	protected double abilityRange;
	protected Game game = null;
	protected GameSquare[][] board = null;
	
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
		abilityRange = 5.0;
		itemList = new ArrayList<Item>();
		abilityCoolDown = 3;
		abilityCoolDownToGo = 3;
	}
	
	public void setGame(Game g) {
		game = g;
	}
	
	public void setBoard() {
		board = game.getBoard();
	}
	
	public boolean useSpecialAbility( Occupant o ) {
		if (abilityCoolDownToGo == 0 && canAttack(o, abilityRange)) {
			attack(o);
			restoreActionPoints(2.0);
			return true;
		}
		return false;
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
	public boolean lineOfSightExists( GameSquare g1, GameSquare g2 ) {
		int r0 = g1.getRow();
		int c0 = g1.getCol();
		int r1 = g2.getRow();
		int c1 = g2.getCol();
		ArrayList<Point> line = Game.BresenhamLine(r0,c0,r1,c1);
		for(int i = 1; i < line.size() - 1; i++) {
			int r = line.get(i).x;
			int c = line.get(i).y;
			System.out.println("" + r + " " + c);
			if(board[r][c].hasOccupant()) return false;
		}
		return true;
	}
	
	public GameSquare getGameSquareAt( int row, int col ) {
		return board[row][col];
	}
	
	
	public boolean attack( Occupant o ) {
		if (canAttack(o, attackRange)) {
			double attackModifier = 0;
			for(int i = 0; i < itemList.size(); i++) {
				Item currItem = itemList.get(i);
				if(currItem.getAttribute() == Attribute.Strength) attackModifier += currItem.getModifier();
			}
			consumeActionPoints(2.0);
			o.takeDamage(strength+attackModifier); 
			return true;
		}
		return false;
	}

	public boolean canAttack(Occupant o, double range) {
		return isInRange(o, range) && actionPoints >= 2.0 && lineOfSightExists(this.location, o.getLocation());
	}
	
	public boolean isInRange( Occupant o , double range) {
		int aRow = this.getLocation().getRow();
		int aCol = this.getLocation().getCol();
		int tRow = o.getLocation().getRow();
		int tCol = o.getLocation().getCol();
		double distance = Math.sqrt(Math.pow(aRow-tRow, 2)+Math.pow(aCol-tCol, 2));
		return range >= distance;
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
	
	public void restoreActionPoints(double amount) {
		actionPoints += amount;
	}
	
	public boolean addItem(Item i) {
		if(inventoryHasRoom()) {
			itemList.add(i);
			i.setOwner(this);
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
	
	public void useItem(Item i) {
		i.execute();
		removeItem(i);
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

	public boolean moveTo(GameSquare destSquare) {
		if (canMoveTo(destSquare)) {
			int srcRow = location.getRow();
			int destRow = destSquare.getRow();
			int srcCol = location.getCol();
			int destCol = destSquare.getCol();
			double apCost = Math.sqrt(Math.pow(srcRow-destRow, 2)+Math.pow(srcCol-destCol, 2));
		
			location.setOccupant(null);
			destSquare.setOccupant(this);
			location = destSquare;
			consumeActionPoints(apCost/speed);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean canMoveTo(GameSquare destSquare) {
		int srcRow = location.getRow();
		int destRow = destSquare.getRow();
		int srcCol = location.getCol();
		int destCol = destSquare.getCol();
		double apCost = Math.sqrt(Math.pow(srcRow-destRow, 2)+Math.pow(srcCol-destCol, 2));

		return apCost/speed <= actionPoints && lineOfSightExists(this.location, destSquare);
	}
	
	/** 
	 * The if-statement checks to see (in order)
	 * 	1. The current unit has the item
	 * 	2. The receiver of the trade has room
	 * 	3. The line of sight exists 
	 * 	4. The receiver is next to the giver
	 * 
	 * @param i The item you're giving away 
	 * @param receiver The receiver of the item
	 * @return
	 */
	public boolean giveItem( Item i, Unit receiver ) {
		if(itemList.contains(i) && receiver.inventoryHasRoom() && lineOfSightExists(this.location, receiver.getLocation()) && isInRange(receiver, 1.0)) {
			removeItem(i);
			receiver.addItem(i);
			i.setOwner(receiver);
			return true;
		}
		return false;
	}

	private boolean inventoryHasRoom() {
		return itemList.size() < 3;
	}
	
	@Override
	public String toString() {
		return "Name: " + name + "\n" +
				"HP: " + (int) hitPoints + "/" + (int) maxHitPoints + "\n" +
				"AP: " + (int) actionPoints + "/" + (int) maxActionPoints + "\n";
				
	}

}
