package server;

import java.util.ArrayList;

import unit.Unit;

public class UserAccount {
	
	private ArrayList<Unit> units;
	private boolean isLoggedOn;
	private boolean isReady;
	private int playerNumber;
	
	public UserAccount(ArrayList<Unit> units, int playerNumber) {
		this.units = units;		
		this.playerNumber = playerNumber;
	}
	
	public ArrayList<Unit> getUnits() {
		return this.units;
	}
	
	public void resetPlayerNumber(int num) {
		this.playerNumber = num;
	}
	
	public int getPlayerNumber() {
		return this.playerNumber;
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
