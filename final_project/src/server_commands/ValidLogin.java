package server_commands;

import client.HumanPlayer;


@SuppressWarnings("serial")
public class ValidLogin extends ServerCommand {

	@Override
	public void executeOn(HumanPlayer p) {
		p.login();
	}
	
}
