package server_commands;

import java.util.ArrayList;

import unit.Unit;
import client.HumanPlayer;

@SuppressWarnings("serial")
public class SendingUserInfo extends ServerCommand {
	
	private ArrayList<Unit> units;
	private int credits;
	
	public SendingUserInfo( ArrayList<Unit> u, int c ) {
		units = u;
		credits = c;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		System.out.println("Sending user info");
		//p.setUnits(units);
		p.setCredits(credits);
		p.update();
	}
	
	public String toString() {
		return units.toString() + " " + credits;
	}
}

