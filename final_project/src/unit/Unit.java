package unit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import shared.ActionItem;
import shared.Attribute;
import shared.Game;
import shared.GameSquare;
import shared.HealthItem;
import shared.Item;
import shared.MineItem;
import shared.MineObstacle;
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
	protected UnitClass unitClass;
	
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
		abilityCoolDown = 2;
		abilityCoolDownToGo = 0;
		//addItem(new HealthItem("Stimpack",5.0,1,this));
	}
	
	public void setGame(Game g) {
		game = g;
	}
	
	public void setBoard() {
		board = game.getBoard();
	}
	
	public boolean useSpecialAbility( int row, int col ) {
		if (canUseAbility(row,col)) {
			return true;
		}
		return false;
	}
	
	public boolean canUseAbility( int row, int col ) {
		return false;
	}
	
	
	public void takeDamage( double attackStrength ) {
		double defenseModifier = getModifier(Attribute.Defense);
		double damage = attackStrength*(1 - (defense+defenseModifier)*damageReduction);
		hitPoints -= damage > 0 ? damage : 0;
		checkDeath();
	}
	
	public double getStrength() {
		return strength;
	}
	public boolean lineOfSightExists( int row, int col ) {
		int r0 = getLocation().getRow();
		int c0 = getLocation().getCol();
		int r1 = row;
		int c1 = col;
		ArrayList<Point> line = Game.BresenhamLine(r0,c0,r1,c1);
		for(int i = 1; i < line.size() - 1; i++) {
			int r = line.get(i).x;
			int c = line.get(i).y;
			//System.out.println("" + r + " " + c);
			if(board[r][c].hasOccupant()) return false;
		}
		return true;
	}
	
	public GameSquare getGameSquareAt( int row, int col ) {
		return board[row][col];
	}
	
	
	public boolean attack( int row, int col ) {
		if (canAttack(row,col)) {
			Occupant o = game.getGameSquareAt(row, col).getOccupant();
			double attackModifier = getModifier(Attribute.Strength);
			consumeActionPoints(2.0);
			o.takeDamage(strength+attackModifier); 
			return true;
		}
		return false;
	}

	public boolean canAttack( int row, int col ) {
		GameSquare gs = game.getGameSquareAt(row, col);
		if( !gs.hasOccupant() ) return false;
		return isInRange( row, col, attackRange ) && actionPoints >= 2.0 && lineOfSightExists(row,col);
	}
	
	public boolean isInRange( int row, int col , double range) {
		int aRow = this.getLocation().getRow();
		int aCol = this.getLocation().getCol();
		double distance = Math.sqrt(Math.pow(aRow-row, 2)+Math.pow(aCol-col, 2));
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
		for (Item itm : itemList)
			if(itm.getType() == i.getType()) {
				i.remove();
				itemList.remove(itm);
				return true;
			}
		return false;
	}
	
	public boolean useItem(Item i) {
		i.use();
		System.out.println("Item Used");
		return true;
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
			hitPoints = maxHitPoints;
			break;
		case MaxActionPoints:
			maxActionPoints += value;
			actionPoints = maxActionPoints;
			break;
		default:
			break;
		}
	}

	public boolean moveTo( int row, int col ) {
		if (canMoveTo(row,col)) {
			GameSquare destSquare = game.getGameSquareAt(row, col);
			int srcRow = location.getRow();
			int destRow = destSquare.getRow();
			int srcCol = location.getCol();
			int destCol = destSquare.getCol();
			double apCost = Math.sqrt(Math.pow(srcRow-destRow, 2)+Math.pow(srcCol-destCol, 2));
		
			if (destSquare.hasOccupant() && destSquare.getOccupant() instanceof MineObstacle) 
				takeDamage(7);
			
			if (!isDead()) {
				location.setOccupant(null);
				destSquare.setOccupant(this);
				location = destSquare;
				consumeActionPoints(apCost/speed);
			}
			else
				destSquare.setOccupant(null);
				
			return true;
		}
		else {
			return false;
		}
	}

	public boolean canMoveTo( int row, int col ) {
		GameSquare destSquare = game.getGameSquareAt(row, col);
		
		int srcRow = location.getRow();
		int destRow = destSquare.getRow();
		int srcCol = location.getCol();
		int destCol = destSquare.getCol();
		
		double apCost = Math.sqrt(Math.pow(srcRow-destRow, 2)+Math.pow(srcCol-destCol, 2));

		return  checkMineOccupant(destSquare) && apCost/speed <= actionPoints && lineOfSightExists( row, col );
	}
	
	private boolean checkMineOccupant(GameSquare gs) {
		Occupant o = null;
		if (gs.hasOccupant()) {
			o = gs.getOccupant();
			if (o instanceof MineObstacle)
				return true;
			return false;
		}
		return true;
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
	public boolean giveItem( int row, int col, Item i ) {
		if(canGiveItem(row,col,i)) {
			GameSquare gs = game.getGameSquareAt(row, col);
			Unit receiver = (Unit) gs.getOccupant();
			removeItem(i);
			receiver.addItem(i);
			i.setOwner(receiver);
			return true;
		}
		System.out.println("cant give item");
		return false;
	}
	
	public boolean hasItem(Item i) {
		for (Item itm : itemList)
			if (itm.getType() == i.getType())
				return true;
		return false;
	}

	public boolean canGiveItem(int row, int col, Item i) {
		GameSquare gs = game.getGameSquareAt(row, col);
		if(!gs.hasOccupant()) return false;
		Occupant o = gs.getOccupant();
		if(!(o instanceof Unit)) return false;
		Unit receiver = (Unit) o;
		return hasItem(i) && receiver.inventoryHasRoom() && lineOfSightExists( row, col ) && isInRange( row, col, 1.5 );
	}

	private boolean inventoryHasRoom() {
		return itemList.size() < 3;
	}
	
	public List<Item> getItemList() {
		return itemList;
	}
	
	public enum UnitClass {
		Soldier,
		Melee,
		Rocket,
		Engineer,
		Demolition,
		Explosives
	}
	
	public double getModifier(Attribute a) {
		double modifier = 0;
		for(int i = 0; i < itemList.size(); i++) {
			Item currItem = itemList.get(i);
			if(currItem.getAttribute() == a) modifier += currItem.getModifier();
		}
		return modifier;
	}

	public void coolDown() {
		abilityCoolDownToGo -= 1;
		abilityCoolDownToGo = abilityCoolDownToGo < 0 ? 0 : abilityCoolDownToGo ;
	}
	
	public Unit cloneUnit(Unit u) {
		UnitClass cl = u.unitClass;
		Unit clone = null;
		switch (cl) {
		case Melee:
			clone = new MeleeUnit(u.getName());
			break;
		case Soldier:
			clone = new SoldierUnit(u.getName());
			break;
		case Demolition:
			clone = new DemolitionUnit(u.getName());
			break;
		case Engineer:
			clone = new EngineerUnit(u.getName());
			break;
		case Rocket:
			clone = new RocketUnit(u.getName());
			break;
		case Explosives:
			clone = new ExplosivesUnit(u.getName());
			break;
		}
		
		clone.strength = u.strength;
		clone.defense = u.defense;
		clone.maxHitPoints = u.maxHitPoints;
		clone.actionPoints = u.actionPoints;
		clone.speed = u.speed;
		clone.attackRange = u.attackRange;
		clone.abilityRange = u.abilityRange;
		clone.itemList = new ArrayList<Item>();
		clone.abilityCoolDown = u.abilityCoolDown;
		clone.abilityCoolDownToGo = 0;
		for (Item i : u.itemList) {
			switch (i.getType()) {
			case HealthItem:
				clone.addItem(new HealthItem("Stimpack",5.0,1,clone));
				break;
			case MineItem:
				clone.addItem(new MineItem("Mine",1,clone));
				break;
			case ActionItem:
				clone.addItem(new ActionItem("AP Serum", 3.0, 1, clone));
				break;
			}
		}
			
		return clone;
	}
	
	public String getInfo() {
		return "Name: " + name + "\n" +
				"HP: " + (int) hitPoints + "/" + (int) maxHitPoints + "\n" +
				"AP: " + (int) actionPoints + "/" + (int) maxActionPoints + "\n" +
				"Cooldown Remaining: " + abilityCoolDownToGo / 2 + " turns\n";
	}
	
	public UnitClass getUnitClass() {
		return this.unitClass;
	}
}

