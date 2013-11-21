package commands;

@SuppressWarnings("serial")
public class MoveCommand extends GameCommand {

	public MoveCommand(int[] source, int[] dest) {
		this.source = source;
		this.dest = dest;
	}
}
