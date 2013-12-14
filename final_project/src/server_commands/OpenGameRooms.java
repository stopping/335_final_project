package server_commands;

import java.util.ArrayList;
import java.util.HashMap;

import client.HumanPlayer;

@SuppressWarnings("serial")
public class OpenGameRooms extends ServerCommand {

	private ArrayList<String> names;
	private ArrayList<String> types;
	
	
	public OpenGameRooms(ArrayList<String> names, ArrayList<String> types) {
		this.names = names;
		this.types = types;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		p.updateAvailGameRooms(names, types);
	}

}
