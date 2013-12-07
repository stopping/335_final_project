package client_commands;

import server.ClientHandler;
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
			ch.setPlayerName(name);
			UserAccount account = s.getUserInfo(name);
			if(account == null) System.out.println("account is null");
			if(ch == null) System.out.println("handler is null");
			ch.sendCommand(new SendingUserInfo( account.getUnits(), account.getNumCredits() ));
			return true;
		}
		return false;
	}

}
