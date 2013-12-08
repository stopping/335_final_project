package server;

import java.util.ArrayList;

import unit.*;
import unit.Unit.UnitClass;

public class UserAccount {
	
	private ArrayList<Unit> units;
	private boolean isLoggedOn;
	private boolean isReady;
	private int credits;
	
	public UserAccount() {
		this.units = new ArrayList<Unit>();
		this.credits = 1000;		// new user starting credits
		this.isReady = false;
	}
	
	public boolean addUnit(String name, UnitClass type) {
		switch (type) {
		case Melee:
			units.add(new MeleeUnit(name));
			break;
		case Rocket:
			units.add(new RocketUnit(name));
			break;
		case Engineer:
			units.add(new EngineerUnit(name));
			break;
		case Demolition:
			units.add(new DemolitionUnit(name));
			break;
		case Soldier:
			units.add(new SoldierUnit(name));
			break;
		}
		System.out.println("added new unit: " + name);
		return true;
	}
	
	public boolean addUnit(Unit u) {
		System.out.println("added new unit: " + u.getName());
		units.add(u);
		return true;
	}
	
	public ArrayList<Unit> getUnits() {
		return this.units;
	}
	
	public void setLoggedOn(boolean status) {
		this.isLoggedOn = status;
	}
	
	public boolean isLoggedOn() {
		return this.isLoggedOn;
	}
	
	public boolean isReady() {
		return this.isReady;
	}
	
	public void setIsReady(boolean status) {
		System.out.println("set is ready for player ");
		this.isReady = status;
	}
	
	public int getNumCredits() {
		return this.credits;
	}
}
