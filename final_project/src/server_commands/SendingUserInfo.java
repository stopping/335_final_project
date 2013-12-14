package server_commands;

import java.util.ArrayList;

import unit.Unit;
import client.HumanPlayer;

@SuppressWarnings("serial")
public class SendingUserInfo extends ServerCommand {
	
	private ArrayList<Unit> units;
	private int credits;
	private String name;
	
	public SendingUserInfo( ArrayList<Unit> u, int c, String name ) {
		units = new ArrayList<Unit>();
		System.out.println("sending user info units size: " + u.size());
		for (Unit un : u)
			units.add(un.cloneUnit(un));
		credits = c;
		this.name = name;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		System.out.println("Sending user info");
		p.setUnits(units);
		p.setName(name);
		p.setCredits(credits);
		p.updateUserInfo();
		p.update();
	}
	
	public String toString() {
		return units.toString() + " " + credits;
	}
}

