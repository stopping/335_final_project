package client_commands;

import server.Server;
import server.UserAccount;
import server_commands.SendingUserInfo;
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
		if (s.newUnit(source, name, type)) {
			UserAccount account = s.getUserInfo(source);
			ch.sendCommand(new SendingUserInfo( account.getUnits(), account.getNumCredits() ));
			return true;
		}
		return false;
	}

}
