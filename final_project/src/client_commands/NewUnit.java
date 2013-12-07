package client_commands;

import server.Server;
import unit.Unit.UnitClass;

@SuppressWarnings("serial")
public class NewUnit extends ClientCommand {

	private UnitClass type;
	private String name;
	
	public NewUnit(String name, UnitClass type) {
		this.type = type;
		this.name = name;
	}
	
	@Override
	public boolean executeOn(Server s) {
		return s.newUnit(source, name, type);
	}

}
