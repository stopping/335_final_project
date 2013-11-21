package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import commands.*;
import client.Player;
import shared.Game;
import shared.Unit;

public class ComputerPlayer implements Player, Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Game game;
	private ArrayList<Unit> myUnits;

	public ComputerPlayer(ArrayList<Unit> aiNewUnits) {
		Socket sock = null;
		myUnits = aiNewUnits;
		try {
			sock = new Socket("localhost", 4009);				
			this.output = new ObjectOutputStream(sock.getOutputStream());
			this.input = new ObjectInputStream(sock.getInputStream());
			
		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}

	@Override
	public void parseAndExecuteCommand(GameCommand c) {	
		// TODO Auto-generated method stub
	}


	@Override
	public void setGame(Game g) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendCommand(Command com) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {

		try {
			Game g = (Game) input.readObject();
			setGame(g);
			
			while (true) {
				Command com = (Command) input.readObject();
				parseAndExecuteCommand((GameCommand)com);
			}

		} catch (Exception e) {
	      e.printStackTrace();
	    }
	}

}
