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
		boolean ret= s.computerPlayerJoin(roomToJoin, ch);
		s.updateOpenGameRooms();
		return ret;
	}

}
