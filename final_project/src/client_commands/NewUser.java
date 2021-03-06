package client_commands;

import server.Server;
import server.UserAccount;
import server_commands.SendingUserInfo;

@SuppressWarnings("serial")
public class NewUser extends ClientCommand {

	private String name;
	private String password;
	
	public NewUser(String name, String pw) {
		this.name = name;
		this.password = pw;
	}

	@Override
	public boolean executeOn(Server s) {
		if(s.setupNewUser(name, password, ch)) {
			UserAccount account = s.getUserInfo(name);
			ch.sendCommand(new SendingUserInfo( account.getUnits(), account.getNumCredits(), account.getName()));
			return true;
		}
		return false;
	}

}
