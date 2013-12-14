package client_commands;

import map.MapBehavior;
import server.Server;
import win_condition.WinCondition;

@SuppressWarnings("serial")
public class StartGame extends ClientCommand {

	WinCondition condition;
	MapBehavior map;
	public StartGame(WinCondition wc, MapBehavior newMap) {
		this.condition = wc;
		this.map = newMap;
	}


	@Override
	public boolean executeOn(Server s) {
		return s.startGame(source, gameRoom, condition, map);
	}

}
