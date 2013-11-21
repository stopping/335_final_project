package commands;

@SuppressWarnings("serial")
public abstract class GameCommand extends Command { 
	
	protected int[] source;
	protected int[] dest;
	
	public int[] getSource() { return this.source; }
	public int[] getDest() { return this.dest; }
}