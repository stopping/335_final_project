package client_commands;

import server.Server;
import unit.Attribute;

@SuppressWarnings("serial")
public class ModifyUnit extends ClientCommand {
	
	Attribute attribute;
	double amount;
	int unitNumber;
	
	public ModifyUnit(Attribute a, double amt, int unitNum) {
		attribute = a;
		amount = amt;
		unitNumber = unitNum;
	}
	
	@Override
	public boolean executeOn(Server s) {
		// TODO Auto-generated method stub
		return false;
	}

}
