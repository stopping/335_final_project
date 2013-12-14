package server_commands;

import map.MapBehavior;
import win_condition.WinCondition;
import client.HumanPlayer;

@SuppressWarnings("serial")
public class GameJoinedInfo extends ServerCommand {

	private WinCondition wc;
	private MapBehavior mb;
	
	public GameJoinedInfo(WinCondition wc, MapBehavior mb) {
		this.wc = wc;
		this.mb = mb;
	}
	
	@Override
	public void executeOn(HumanPlayer p) {
		p.updateGameType(wc, mb);
	}

}
