package server;

import java.util.ArrayList;

import unit.Unit;

public class UserAccount {
	
	private ArrayList<Unit> units;
	private boolean isLoggedOn;
	private boolean isReady;
	
	public UserAccount(ArrayList<Unit> units) {
		this.units = units;		
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
}
