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
	}
	
	public void addUnit(String name, UnitClass type) {
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
		this.isReady = status;
	}
	
	public int getNumCredits() {
		return this.credits;
	}
}
