package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class Login extends ClientCommand {

	private String name;
	private String password;
	
	public Login(String n, String p) {
		name = n;
		password  = p;
	}
	
	public boolean executeOn(Server s) {
		return s.login(source, name, password, ch);
	}
}
