package client_commands;

import map.MapBehavior;
import map.ObstacleMap;
import map.StandardMap;
import server.Server;
import win_condition.DeathmatchCondition;
import win_condition.EscortCondition;
import win_condition.WinCondition;

@SuppressWarnings("serial")
public class PlayerReady extends ClientCommand {

	private WinCondition wc;
	private MapBehavior mb;
	
	public PlayerReady(int w, int m) {
		if (w == 0)
			this.wc = new DeathmatchCondition();
		else 
			this.wc = new EscortCondition();
		if (m == 0)
			this.mb = new StandardMap();
		else 
			this.mb = new ObstacleMap();
	}
	
	@Override
	public boolean executeOn(Server s) {
		if (s.setReady(source, ch, gameRoom, wc)) {
			s.updateOpenGameRooms();
			return true;
		}
		return false;
	}

}
