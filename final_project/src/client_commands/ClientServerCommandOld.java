//package client_commands;
//
//import java.util.ArrayList;
//
//@SuppressWarnings("serial")
//public class ClientServerCommandOld extends ClientCommand {
//
//	private ClientServerCommandType type;
//	private ArrayList<String> data;
//	
//	public ClientServerCommandOld(ClientServerCommandType type, String[] args) {
//		this.type = type;
//		if (args != null) {
//			data = new ArrayList<String>();
//			for (String s: args)
//			data.add(s);
//		}
//	}
//	
//	public enum ClientServerCommandType {
//		ComputerTurn,
//		PlayerForfeit,
//		JoinGame,
//		ComputerPlayerJoin,
//		NewGame,
//		Login,
//		ValidLogin,
//		NewComputerPlayer,
//		Ready,
//		StartGame,
//		GameOver,
//		ModifyUnit,
//		IllegalOption,
//		SendingGame,
//		NewUnit,
//		NewUser,
//		Message,
//		SuspendSession,
//		ResumeSession,
//		Logout,
//		OpenGameRooms,
//		UnitInfo,
//		ClientExit
//	}
//	
//	public ArrayList<String> getData() {
//		return this.data;
//	}
//	
//	public ClientServerCommandType getType() {
//		return this.type;
//	}
//}
