package commands;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ClientServerCommand extends Command {

	private ClientServerCommandType type;
	private ArrayList<String> data;
	
	public ClientServerCommand(ClientServerCommandType type, String[] args) {
		this.type = type;
		if (args != null) {
			data = new ArrayList<String>();
			for (String s: args)
			data.add(s);
		}
	}
	
	public enum ClientServerCommandType {
		ComputerTurn,
		PlayerForfeit,
		JoinGame,
		ComputerPlayerJoin,
		NewGame,
		Login,
		NewComputerPlayer,
		Ready,
		StartGame,
		ModifyUnit,
		IllegalOption,
		SendingGame,
		NewUnit,
		NewUser,
		Message
	}
	
	public ArrayList<String> getData() {
		return this.data;
	}
	
	public ClientServerCommandType getType() {
		return this.type;
	}
}
