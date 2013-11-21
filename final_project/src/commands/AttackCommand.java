package commands;

@SuppressWarnings("serial")
public class AttackCommand extends GameCommand {

	public AttackCommand(int[] source, int[] dest) {
		super.source = source;
		super.dest = dest;
	}
	
}
