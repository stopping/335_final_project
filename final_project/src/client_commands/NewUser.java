package client_commands;

import server.Server;

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
		return s.setupNewUser(name, password, ch);
	}

}
