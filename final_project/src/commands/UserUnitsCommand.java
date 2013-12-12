package commands;

import java.util.ArrayList;

import unit.Unit;

@SuppressWarnings("serial")
public class UserUnitsCommand extends GameCommand {

	private ArrayList<Unit> units;
	
	public UserUnitsCommand(ArrayList<Unit> u) {
		this.units = u;
	}
	
	public ArrayList<Unit> getUnits() {
		return this.units;
	}
}
