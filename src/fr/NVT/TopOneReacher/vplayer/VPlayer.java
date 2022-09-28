package fr.NVT.TopOneReacher.vplayer;


import fr.NVT.TopOneReacher.Main;
import fr.NVT.TopOneReacher.boardgame.Board;
import fr.NVT.TopOneReacher.boardgame.Position;

public abstract class VPlayer {
	
	
	
	//Initialization
	public abstract boolean init(Main main, int id, int index);
	
	
	//Call for each loop turn
	public abstract Position loop(Board board);

	
	//Get Player ID (must be different than zero)
	public abstract int getID();

	
}
