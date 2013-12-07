package client_commands;

import server.ClientHandler;
import server.Server;
import server.UserAccount;
import server_commands.SendingUserInfo;

@SuppressWarnings("serial")
public class Login extends ClientCommand {

	private String name;
	private String password;
	
	public Login(String n, String p) {
		name = n;
		password  = p;
	}
	
	public boolean executeOn(Server s) {
		if(s.login(source, name, password, ch)) {
			UserAccount account = s.getUserInfo(name);
			ch.sendCommand(new SendingUserInfo( account.getUnits(), account.getNumCredits() ));
			return true;
		}
		return false;
	}
}
