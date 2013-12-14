package map;

import game.GameSquare;

import java.io.Serializable;
import java.util.ArrayList;

import unit.Unit;

public interface MapBehavior extends Serializable {

	
	public GameSquare[][] getMap();
	
	public void setMap(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits);
	
}
