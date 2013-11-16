package shared;
import java.io.Serializable;

/**
 *   Class:		Command
 *   
 *   Purpose:	Provide a universal means of communication 
 *   				between the client and server.
 *   
 *   @param 	commandType		The type of Command
 *   @param		sourceRow		Unit initial row position
 *   @param		sourceCol		Unit initial col position
 *   @param		destRow			Unit destination row position
 *   @param 	destCol			Unit destination col position
 *   @param 	item				Item involved in command
 *   @param		message			Associated String message
 *   
 *   @author Kyle Criddle
 **/
@SuppressWarnings("serial")
public class Command implements Serializable {
	
	private int[] source;
	private int[] dest;
	private CommandType commandType;
	private Item item;
	private String message;
	
	public Command (CommandType commandType, int sourceRow, int sourceCol, 
			int destRow, int destCol, Item item, String message) {
		
		source = new int[2];
		dest = new int[2];
		
		this.commandType = commandType;
		this.source[0] = sourceRow;
		this.source[1] = sourceCol;
		this.dest[0] = destRow;
		this.dest[1] = destCol;
		this.item = item;
		this.message = message;
	}
	
	public Command (CommandType commandType, int[] source, 
			int[] destination, Item item, String message) {
		
		this.commandType = commandType;
		this.source = source;
		this.dest = destination;
		this.item = item;
		this.message = message;
	}
	
	public CommandType getCommandType() { return this.commandType; }
	public int[] getSource() { return this.source; }
	public int[] getDest() { return this.dest; }
	public Item getItem() { return this.item; }
	public String getMessage() { return this.message; }
	
	public enum CommandType {
		Attack,
		EndTurn,
		Login,
		Message,
		Move,
		NewAI,
		NewGame,
		GiveItem,
		UseItem,
		Quit
	}
}
