package server_commands;

import java.util.HashMap;
import client.HumanPlayer;

@SuppressWarnings("serial")
public class OpenGameRooms extends ServerCommand {

	private HashMap<Integer, String> opengames;
	
	public OpenGameRooms(HashMap<Integer, String> map) {
		this.opengames = map;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		p.updateAvailGameRooms(opengames);
	}

}
