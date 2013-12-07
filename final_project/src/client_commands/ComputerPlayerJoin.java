package client_commands;

import server.Server;

@SuppressWarnings("serial")
public class ComputerPlayerJoin extends ClientCommand {
	
	private int roomToJoin;
	
	public ComputerPlayerJoin(int g) {
		this.roomToJoin = g;
	}
	
	
	@Override
	public boolean executeOn(Server s) {
		return s.computerPlayerJoin(roomToJoin, ch);
	}

}
